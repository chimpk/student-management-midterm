package com.example.studentmgmt;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.UserRepository;
import com.example.studentmgmt.data.UserRepositoryProvider;
import com.example.studentmgmt.databinding.ActivityMainBinding;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.auth.LoginActivity;
import com.example.studentmgmt.ui.navigation.RoleNavigationPolicy;
import com.example.studentmgmt.ui.navigation.SessionSecurityPolicy;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_REQUESTED_DESTINATION = "extra_requested_destination";
    public static final String EXTRA_LOGIN_WARNING = "extra_login_warning";
    public static final String MSG_UNAUTHORIZED = "Bạn không có quyền truy cập chức năng này.";

    private ActivityMainBinding binding;
    private NavController navController;
    private AppUser currentUser;
    private UserRepository.ListenerHandle sessionListenerHandle;
    private boolean isLoggingOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Security checkpoint: Only accept authenticated session from Task 2 AuthRepository
        currentUser = AuthRepositoryProvider.getRepository().getCurrentUser();
        if (!RoleNavigationPolicy.isSessionValid(currentUser)) {
            redirectToLogin();
            return;
        }

        // Initialize local avatar cache dir for local storage fallback
        com.example.studentmgmt.data.FirebaseProfileRepository.setLocalDir(getFilesDir());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Surface non-fatal login warnings (e.g. login history write failure)
        String loginWarning = getIntent().getStringExtra(EXTRA_LOGIN_WARNING);
        if (loginWarning != null && !loginWarning.trim().isEmpty()) {
            Toast.makeText(this, loginWarning, Toast.LENGTH_LONG).show();
        }

        // Hide Activity-level ActionBar to prevent duplicate headers with Fragment MaterialToolbars
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            setupBottomNavigation();
            setupDestinationAccessGuard();
        }

        // Monitor current session in realtime: if status becomes Locked, immediately kick out
        observeCurrentSession();

        // Handle direct deep link / intent navigation safely
        handleDirectNavigationIntent(getIntent());
    }

    private void observeCurrentSession() {
        if (currentUser == null) return;
        sessionListenerHandle = UserRepositoryProvider.getRepository().listenUser(currentUser.getUid(), new UserRepository.UserCallback() {
            @Override
            public void onUserLoaded(AppUser user) {
                if (isLoggingOut || isFinishing() || isDestroyed()) return;
                SessionSecurityPolicy.Evaluation eval = SessionSecurityPolicy.evaluateUserSnapshot(currentUser, user);
                if (eval.shouldTerminate()) {
                    boolean isLocked = user != null && (user.isLocked() || !user.isNormal());
                    int type = isLocked
                            ? com.example.studentmgmt.ui.auth.AuthTerminalActivity.TYPE_ACCOUNT_LOCKED
                            : com.example.studentmgmt.ui.auth.AuthTerminalActivity.TYPE_SESSION_EXPIRED;
                    redirectToTerminal(type);
                }
            }

            @Override
            public void onError(SessionSecurityPolicy.SessionErrorType errorType, String errorMessage) {
                if (isLoggingOut || isFinishing() || isDestroyed()) return;
                // If user is intentionally logging out or unauthenticated, ignore permission error
                if (!AuthRepositoryProvider.getRepository().isSignedIn()) {
                    return;
                }
                SessionSecurityPolicy.Evaluation eval = SessionSecurityPolicy.evaluateError(errorType);
                if (eval.shouldTerminate()) {
                    boolean isLocked = (errorType == SessionSecurityPolicy.SessionErrorType.PERMISSION_DENIED);
                    int type = isLocked
                            ? com.example.studentmgmt.ui.auth.AuthTerminalActivity.TYPE_ACCOUNT_LOCKED
                            : com.example.studentmgmt.ui.auth.AuthTerminalActivity.TYPE_SESSION_EXPIRED;
                    redirectToTerminal(type);
                }
            }
        });
    }

    private void redirectToTerminal(int terminalType) {
        if (sessionListenerHandle != null) {
            sessionListenerHandle.remove();
            sessionListenerHandle = null;
        }
        startActivity(com.example.studentmgmt.ui.auth.AuthTerminalActivity.createIntent(this, terminalType));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sessionListenerHandle != null) {
            sessionListenerHandle.remove();
            sessionListenerHandle = null;
        }
    }

    private void setupBottomNavigation() {
        if (binding == null || binding.bottomNavView == null || navController == null) return;

        binding.bottomNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.nav_dashboard) {
                    return true;
                }
                if (!navController.popBackStack(R.id.nav_dashboard, false)) {
                    navController.navigate(R.id.nav_dashboard);
                }
                return true;
            } else if (itemId == R.id.nav_student_list) {
                if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.nav_student_list) {
                    return true;
                }
                navController.popBackStack(R.id.nav_dashboard, false);
                navController.navigate(R.id.nav_student_list);
                return true;
            } else if (itemId == R.id.nav_profile) {
                if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.nav_profile) {
                    return true;
                }
                navController.popBackStack(R.id.nav_dashboard, false);
                navController.navigate(R.id.nav_profile);
                return true;
            }
            return false;
        });

        binding.bottomNavView.setOnItemReselectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.nav_dashboard) {
                    if (!navController.popBackStack(R.id.nav_dashboard, false)) {
                        navController.navigate(R.id.nav_dashboard);
                    }
                }
            }
        });
    }

    private void setupDestinationAccessGuard() {
        if (navController == null) return;

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();

            if (binding != null && binding.bottomNavView != null) {
                MenuItem menuItem = binding.bottomNavView.getMenu().findItem(destId);
                if (menuItem != null) {
                    menuItem.setChecked(true);
                }
            }

            if (destId == R.id.nav_user_management) {
                if (!RoleNavigationPolicy.canAccess(currentUser.getRole(), RoleNavigationPolicy.DEST_USER_MANAGEMENT)) {
                    Toast.makeText(this, MSG_UNAUTHORIZED, Toast.LENGTH_SHORT).show();
                    controller.navigate(R.id.nav_dashboard);
                }
            } else if (destId == R.id.nav_import_export) {
                if (currentUser.isEmployee()) {
                    Toast.makeText(this, MSG_UNAUTHORIZED, Toast.LENGTH_SHORT).show();
                    controller.navigate(R.id.nav_dashboard);
                }
            }
        });
    }

    private void handleDirectNavigationIntent(Intent intent) {
        if (intent == null || navController == null) return;

        int requestedDest = intent.getIntExtra(EXTRA_REQUESTED_DESTINATION, -1);
        if (requestedDest == R.id.nav_user_management) {
            if (RoleNavigationPolicy.canAccess(currentUser.getRole(), RoleNavigationPolicy.DEST_USER_MANAGEMENT)) {
                navController.navigate(R.id.nav_user_management);
            } else {
                Toast.makeText(this, MSG_UNAUTHORIZED, Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.nav_student_list);
            }
        }
    }

    private void redirectToLogin() {
        isLoggingOut = true;
        if (sessionListenerHandle != null) {
            sessionListenerHandle.remove();
            sessionListenerHandle = null;
        }
        AuthRepositoryProvider.getRepository().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void performLogout() {
        redirectToLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentUser != null) {
            MenuItem usersItem = menu.findItem(R.id.action_users);
            if (usersItem != null) {
                usersItem.setVisible(RoleNavigationPolicy.shouldShowMenuItem(
                        currentUser.getRole(), RoleNavigationPolicy.MENU_USERS));
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_students) {
            if (navController != null) {
                navController.navigate(R.id.nav_student_list);
            }
            return true;
        } else if (id == R.id.action_users) {
            if (RoleNavigationPolicy.canAccess(currentUser.getRole(), RoleNavigationPolicy.DEST_USER_MANAGEMENT)) {
                if (navController != null) {
                    navController.navigate(R.id.nav_user_management);
                }
            } else {
                Toast.makeText(this, MSG_UNAUTHORIZED, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_profile) {
            if (navController != null) {
                navController.navigate(R.id.nav_profile);
            }
            return true;
        } else if (id == R.id.action_history) {
            if (navController != null) {
                navController.navigate(R.id.nav_login_history);
            }
            return true;
        } else if (id == R.id.action_logout) {
            redirectToLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
