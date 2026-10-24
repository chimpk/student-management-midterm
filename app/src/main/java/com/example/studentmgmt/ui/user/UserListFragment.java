package com.example.studentmgmt.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.UserRepository;
import com.example.studentmgmt.data.UserRepositoryProvider;
import com.example.studentmgmt.databinding.DialogUserEditBinding;
import com.example.studentmgmt.databinding.FragmentUserListBinding;
import com.example.studentmgmt.model.AppUser;
import android.content.Context;
import com.example.studentmgmt.data.DefaultUsernameAuthResolver;
import com.example.studentmgmt.utils.ValidationUtils;
import com.example.studentmgmt.utils.VietnameseTextInputFormatter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Screen displaying the realtime list of users for Administrator management.
 * Conforms to S10, S21, S33, S40-S45, S71-S74.
 */
public class UserListFragment extends Fragment implements UserAdapter.OnUserActionListener {

    private FragmentUserListBinding binding;
    private UserManagementViewModel viewModel;
    private UserAdapter adapter;
    private AppUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser = AuthRepositoryProvider.getRepository().getCurrentUser();
        if (!UserManagementPolicy.canManageUsers(currentUser)) {
            Toast.makeText(requireContext(), "Chỉ Admin có quyền truy cập Quản lý người dùng.", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            return;
        }

        binding.toolbarUserList.setNavigationOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigateUp();
            } catch (Exception e) {
                requireActivity().onBackPressed();
            }
        });

        UserRepository userRepository = UserRepositoryProvider.getRepository();
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserManagementViewModel(userRepository, currentUser);
            }
        };
        viewModel = new ViewModelProvider(this, factory).get(UserManagementViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.fabAddUser.setOnClickListener(v -> showAddUserDialog());

        viewModel.startListening();
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter(currentUser, this);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(adapter);

        binding.includeErrorState.btnRetry.setOnClickListener(v -> viewModel.retry());
        binding.btnRetryUsers.setOnClickListener(v -> viewModel.retry());
    }

    private void observeViewModel() {
        viewModel.getUserListState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            switch (state.getStatus()) {
                case LOADING:
                    binding.includeLoadingSkeleton.getRoot().setVisibility(View.VISIBLE);
                    binding.includeErrorState.getRoot().setVisibility(View.GONE);
                    binding.includeEmptyState.getRoot().setVisibility(View.GONE);
                    binding.rvUsers.setVisibility(View.GONE);
                    break;
                case CONTENT:
                    binding.includeLoadingSkeleton.getRoot().setVisibility(View.GONE);
                    binding.includeErrorState.getRoot().setVisibility(View.GONE);
                    binding.includeEmptyState.getRoot().setVisibility(View.GONE);
                    binding.rvUsers.setVisibility(View.VISIBLE);

                    List<AppUser> users = state.getData();
                    adapter.submitList(users);

                    int total = users != null ? users.size() : 0;
                    int active = 0;
                    if (users != null) {
                        for (AppUser u : users) {
                            if (!u.isLocked()) active++;
                        }
                    }
                    binding.tvUserCountSummary.setText(getString(R.string.user_count_summary_format, total, active));
                    break;
                case EMPTY:
                    binding.includeLoadingSkeleton.getRoot().setVisibility(View.GONE);
                    binding.includeErrorState.getRoot().setVisibility(View.GONE);
                    binding.includeEmptyState.getRoot().setVisibility(View.VISIBLE);
                    binding.rvUsers.setVisibility(View.GONE);

                    binding.includeEmptyState.tvEmptyTitle.setText(R.string.empty_users_message);
                    binding.includeEmptyState.layoutEmptyActions.setVisibility(View.GONE);
                    binding.tvUserCountSummary.setText(getString(R.string.user_count_summary_format, 0, 0));
                    break;
                case ERROR:
                    binding.includeLoadingSkeleton.getRoot().setVisibility(View.GONE);
                    binding.includeErrorState.getRoot().setVisibility(View.VISIBLE);
                    binding.includeEmptyState.getRoot().setVisibility(View.GONE);
                    binding.rvUsers.setVisibility(View.GONE);

                    binding.includeErrorState.tvErrorDesc.setText(state.getErrorMessage());
                    break;
            }
        });

        viewModel.getActionErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        viewModel.getActionSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                Toast.makeText(requireContext(), success, Toast.LENGTH_SHORT).show();
                viewModel.clearSuccess();
            }
        });
    }

    @Override
    public void onToggleLock(AppUser user) {
        boolean willLock = !user.isLocked();
        if (willLock) {
            // S21: Dialog khóa tài khoản
            String message = getString(R.string.dialog_lock_user_desc_format, user.getUsername(), user.getName());
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_lock_user_title)
                    .setMessage(message)
                    .setIcon(R.drawable.ic_lock)
                    .setPositiveButton(R.string.btn_lock_account, (dialog, which) -> {
                        viewModel.lockUser(user.getUid(), true);
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        } else {
            // Mở khóa tài khoản
            String message = getString(R.string.card_inline_unlock_confirm_format, user.getName());
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.title_unlock_account)
                    .setMessage(message)
                    .setPositiveButton(R.string.btn_confirm_unlock, (dialog, which) -> {
                        viewModel.lockUser(user.getUid(), false);
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        }
    }

    @Override
    public void onEditUser(AppUser user) {
        DialogUserEditBinding dialogBinding = DialogUserEditBinding.inflate(getLayoutInflater());
        dialogBinding.tvDialogTitle.setText(R.string.title_edit_user);
        dialogBinding.tvDialogSubtitle.setText(getString(R.string.subtitle_edit_student_format, user.getName()));

        // S44: Self-protection checks
        boolean isSelf = currentUser != null && Objects.equals(currentUser.getUid(), user.getUid());
        if (isSelf) {
            dialogBinding.cardSelfProtection.setVisibility(View.VISIBLE);
            dialogBinding.rbRoleAdmin.setChecked(true);
            dialogBinding.rbRoleManager.setEnabled(false);
            dialogBinding.rbRoleEmployee.setEnabled(false);
            dialogBinding.rbStatusLocked.setEnabled(false);
        } else {
            dialogBinding.cardSelfProtection.setVisibility(View.GONE);
        }

        // Username immutable on edit
        dialogBinding.etUserUsername.setText(user.getUsername());
        dialogBinding.etUserUsername.setEnabled(false);
        dialogBinding.tilUserUsername.setEnabled(false);

        dialogBinding.etUserName.setText(user.getName());
        dialogBinding.etUserAge.setText(String.valueOf(user.getAge()));
        dialogBinding.etUserPhone.setText(user.getPhone());
        dialogBinding.etUserEmail.setText(user.getEmail() != null ? user.getEmail() : "");

        if (AppUser.ROLE_ADMIN.equalsIgnoreCase(user.getRole())) {
            dialogBinding.rbRoleAdmin.setChecked(true);
        } else if (AppUser.ROLE_MANAGER.equalsIgnoreCase(user.getRole())) {
            dialogBinding.rbRoleManager.setChecked(true);
        } else {
            dialogBinding.rbRoleEmployee.setChecked(true);
        }

        if (user.isLocked()) {
            dialogBinding.rbStatusLocked.setChecked(true);
        } else {
            dialogBinding.rbStatusNormal.setChecked(true);
        }

        dialogBinding.tilUserPassword.setVisibility(View.GONE);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnCancelUser.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveUser.setOnClickListener(v -> {
            String name = dialogBinding.etUserName.getText() != null ?
                    VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etUserName.getText().toString().trim()) : "";
            String ageStr = dialogBinding.etUserAge.getText() != null ?
                    dialogBinding.etUserAge.getText().toString().trim() : "";
            String phone = dialogBinding.etUserPhone.getText() != null ?
                    dialogBinding.etUserPhone.getText().toString().trim() : "";
            String email = dialogBinding.etUserEmail.getText() != null ?
                    dialogBinding.etUserEmail.getText().toString().trim() : "";

            boolean hasError = false;
            if (TextUtils.isEmpty(name)) {
                dialogBinding.tilUserName.setError(getString(R.string.error_field_required));
                hasError = true;
            } else {
                dialogBinding.tilUserName.setError(null);
            }

            int age = 0;
            try {
                age = Integer.parseInt(ageStr);
                if (age < 18 || age > 60) {
                    dialogBinding.tilUserAge.setError("Tuổi phải từ 18 đến 60");
                    hasError = true;
                } else {
                    dialogBinding.tilUserAge.setError(null);
                }
            } catch (NumberFormatException e) {
                dialogBinding.tilUserAge.setError("Vui lòng nhập tuổi hợp lệ");
                hasError = true;
            }

            if (TextUtils.isEmpty(phone) || !phone.matches("^0\\d{9}$")) {
                dialogBinding.tilUserPhone.setError("SĐT gồm 10 chữ số, bắt đầu bằng 0");
                hasError = true;
            } else {
                dialogBinding.tilUserPhone.setError(null);
            }

            if (!TextUtils.isEmpty(email) && !ValidationUtils.isValidEmail(email)) {
                dialogBinding.tilUserEmail.setError(getString(R.string.error_invalid_email));
                hasError = true;
            } else {
                dialogBinding.tilUserEmail.setError(null);
            }

            if (hasError) return;

            String role = AppUser.ROLE_EMPLOYEE;
            if (dialogBinding.rbRoleAdmin.isChecked()) {
                role = AppUser.ROLE_ADMIN;
            } else if (dialogBinding.rbRoleManager.isChecked()) {
                role = AppUser.ROLE_MANAGER;
            }

            // S43: Saving state
            dialogBinding.btnSaveUser.setText(R.string.btn_saving);
            dialogBinding.btnSaveUser.setEnabled(false);

            viewModel.updateUser(user.getUid(), name, age, phone, role);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDeleteUser(AppUser user) {
        // S45: Dialog xóa người dùng
        String message = getString(R.string.confirm_delete_user_format, user.getUsername());

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_confirm_delete_user)
                .setMessage(message)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    viewModel.deleteUser(user.getUid());
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void showAddUserDialog() {
        DialogUserEditBinding dialogBinding = DialogUserEditBinding.inflate(getLayoutInflater());
        dialogBinding.tvDialogTitle.setText(R.string.title_add_user);
        dialogBinding.cardSelfProtection.setVisibility(View.GONE);
        dialogBinding.tilUserPassword.setVisibility(View.VISIBLE);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnCancelUser.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveUser.setOnClickListener(v -> {
            String username = dialogBinding.etUserUsername.getText() != null ?
                    dialogBinding.etUserUsername.getText().toString().trim() : "";
            String name = dialogBinding.etUserName.getText() != null ?
                    VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etUserName.getText().toString().trim()) : "";
            String ageStr = dialogBinding.etUserAge.getText() != null ?
                    dialogBinding.etUserAge.getText().toString().trim() : "";
            String phone = dialogBinding.etUserPhone.getText() != null ?
                    dialogBinding.etUserPhone.getText().toString().trim() : "";
            String email = dialogBinding.etUserEmail.getText() != null ?
                    dialogBinding.etUserEmail.getText().toString().trim() : "";
            String password = dialogBinding.etUserPassword.getText() != null ?
                    dialogBinding.etUserPassword.getText().toString().trim() : "";

            boolean hasError = false;
            if (TextUtils.isEmpty(username) || username.length() < 3 || username.contains(" ")) {
                dialogBinding.tilUserUsername.setError("Username từ 3-32 ký tự, không chứa khoảng trắng");
                hasError = true;
            } else {
                dialogBinding.tilUserUsername.setError(null);
            }

            if (TextUtils.isEmpty(name)) {
                dialogBinding.tilUserName.setError(getString(R.string.error_field_required));
                hasError = true;
            } else {
                dialogBinding.tilUserName.setError(null);
            }

            int age = 0;
            try {
                age = Integer.parseInt(ageStr);
                if (age < 18 || age > 60) {
                    dialogBinding.tilUserAge.setError("Tuổi phải từ 18 đến 60");
                    hasError = true;
                } else {
                    dialogBinding.tilUserAge.setError(null);
                }
            } catch (NumberFormatException e) {
                dialogBinding.tilUserAge.setError("Vui lòng nhập tuổi hợp lệ");
                hasError = true;
            }

            if (TextUtils.isEmpty(phone) || !phone.matches("^0\\d{9}$")) {
                dialogBinding.tilUserPhone.setError("SĐT gồm 10 chữ số, bắt đầu bằng 0");
                hasError = true;
            } else {
                dialogBinding.tilUserPhone.setError(null);
            }

            if (!TextUtils.isEmpty(email) && !ValidationUtils.isValidEmail(email)) {
                dialogBinding.tilUserEmail.setError(getString(R.string.error_invalid_email));
                hasError = true;
            } else {
                dialogBinding.tilUserEmail.setError(null);
            }

            if (TextUtils.isEmpty(password) || password.length() < 6) {
                dialogBinding.tilUserPassword.setError("Mật khẩu tối thiểu 6 ký tự");
                hasError = true;
            } else {
                dialogBinding.tilUserPassword.setError(null);
            }

            if (hasError) return;

            String role = AppUser.ROLE_EMPLOYEE;
            if (dialogBinding.rbRoleAdmin.isChecked()) {
                role = AppUser.ROLE_ADMIN;
            } else if (dialogBinding.rbRoleManager.isChecked()) {
                role = AppUser.ROLE_MANAGER;
            }

            String status = dialogBinding.rbStatusLocked.isChecked() ?
                    AppUser.STATUS_LOCKED : AppUser.STATUS_NORMAL;

            dialogBinding.btnSaveUser.setText(R.string.btn_saving);
            dialogBinding.btnSaveUser.setEnabled(false);

            String authEmail = !email.isEmpty() ? email : new DefaultUsernameAuthResolver().resolveAuthEmail(username);

            final String finalRole = role;
            final String finalStatus = status;
            final int finalAge = age;

            try {
                Context appContext = requireContext().getApplicationContext();
                FirebaseApp secondaryApp;
                try {
                    secondaryApp = FirebaseApp.getInstance("UserAdminApp");
                } catch (IllegalStateException e) {
                    FirebaseOptions options = FirebaseApp.getInstance().getOptions();
                    secondaryApp = FirebaseApp.initializeApp(appContext, options, "UserAdminApp");
                }
                FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);
                secondaryAuth.createUserWithEmailAndPassword(authEmail, password)
                        .addOnSuccessListener(authResult -> {
                            String realUid = authResult.getUser() != null ? authResult.getUser().getUid() : null;
                            if (realUid == null) {
                                dialogBinding.btnSaveUser.setText(R.string.btn_save);
                                dialogBinding.btnSaveUser.setEnabled(true);
                                dialogBinding.tilUserUsername.setError("Không lấy được UID từ Firebase Auth");
                                return;
                            }
                            AppUser newUser = new AppUser(
                                    realUid,
                                    username,
                                    email.isEmpty() ? null : email,
                                    name,
                                    finalAge,
                                    phone,
                                    finalStatus,
                                    finalRole,
                                    null
                            );
                            viewModel.createUser(newUser);
                            secondaryAuth.signOut();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(error -> {
                            dialogBinding.btnSaveUser.setText(R.string.btn_save);
                            dialogBinding.btnSaveUser.setEnabled(true);
                            String msg = error.getLocalizedMessage() != null ? error.getLocalizedMessage() : "Không thể tạo tài khoản xác thực";
                            dialogBinding.tilUserPassword.setError(msg);
                        });
            } catch (Exception e) {
                // Fallback for mock/test environments
                String generatedUid = "uid_" + UUID.randomUUID().toString().substring(0, 12);
                AppUser newUser = new AppUser(
                        generatedUid,
                        username,
                        email.isEmpty() ? null : email,
                        name,
                        finalAge,
                        phone,
                        finalStatus,
                        finalRole,
                        null
                );
                viewModel.createUser(newUser);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onViewHistory(AppUser user) {
        if (user == null || getView() == null) return;
        Bundle args = new Bundle();
        args.putString(com.example.studentmgmt.ui.history.LoginHistoryFragment.ARG_TARGET_UID, user.getUid());
        args.putString(com.example.studentmgmt.ui.history.LoginHistoryFragment.ARG_TARGET_NAME, user.getName());
        try {
            Navigation.findNavController(requireView()).navigate(R.id.nav_login_history, args);
        } catch (Exception ignored) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
