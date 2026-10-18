package com.example.studentmgmt.ui.user;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.UserRepository;
import com.example.studentmgmt.data.UserRepositoryProvider;
import com.example.studentmgmt.databinding.DialogUserEditBinding;
import com.example.studentmgmt.databinding.FragmentUserListBinding;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.utils.ValidationUtils;

/**
 * Screen displaying the realtime list of users for Administrator management.
 * Provides user profile creation, updating, lock/unlock status toggling,
 * and user document deletion under strict RBAC checks.
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
            Toast.makeText(requireContext(), "Bạn không có quyền truy cập quản lý người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }

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
        binding.btnRetryUsers.setOnClickListener(v -> viewModel.retry());
    }

    private void observeViewModel() {
        viewModel.getUserListState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            switch (state.getStatus()) {
                case LOADING:
                    binding.progressLoading.setVisibility(View.VISIBLE);
                    binding.layoutErrorUsers.setVisibility(View.GONE);
                    binding.tvEmptyUsers.setVisibility(View.GONE);
                    break;
                case CONTENT:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.layoutErrorUsers.setVisibility(View.GONE);
                    binding.tvEmptyUsers.setVisibility(View.GONE);
                    binding.rvUsers.setVisibility(View.VISIBLE);
                    adapter.submitList(state.getData());
                    break;
                case EMPTY:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.layoutErrorUsers.setVisibility(View.GONE);
                    binding.tvEmptyUsers.setVisibility(View.VISIBLE);
                    binding.rvUsers.setVisibility(View.GONE);
                    adapter.submitList(null);
                    break;
                case ERROR:
                    binding.progressLoading.setVisibility(View.GONE);
                    binding.layoutErrorUsers.setVisibility(View.VISIBLE);
                    binding.tvErrorUsers.setText(state.getErrorMessage());
                    binding.tvEmptyUsers.setVisibility(View.GONE);
                    binding.rvUsers.setVisibility(View.GONE);
                    adapter.submitList(null);
                    break;
            }
        });

        viewModel.getActionErrorMessage().observe(getViewLifecycleOwner(), actionError -> {
            if (actionError != null && !actionError.isEmpty()) {
                com.google.android.material.snackbar.Snackbar.make(binding.getRoot(), actionError, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                        .show();
                viewModel.clearActionError();
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
        String actionTitle = willLock ? getString(R.string.title_lock_account) : getString(R.string.title_unlock_account);
        String message = getString(
                willLock ? R.string.confirm_lock_user_format : R.string.confirm_unlock_user_format,
                user.getUsername(),
                user.getName()
        );

        new AlertDialog.Builder(requireContext())
                .setTitle(actionTitle)
                .setMessage(message)
                .setPositiveButton(willLock ? R.string.btn_lock : R.string.btn_unlock, (dialog, which) -> {
                    viewModel.lockUser(user.getUid(), willLock);
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    @Override
    public void onEditUser(AppUser user) {
        DialogUserEditBinding dialogBinding = DialogUserEditBinding.inflate(getLayoutInflater());
        dialogBinding.tvDialogTitle.setText(R.string.title_edit_user);
        dialogBinding.tvBackendNotice.setVisibility(View.GONE);
        dialogBinding.tilUserEmail.setVisibility(View.GONE);
        dialogBinding.tvStatusLabel.setVisibility(View.GONE);
        dialogBinding.rgUserStatus.setVisibility(View.GONE);

        // UID and Username immutable on update
        dialogBinding.etUserUid.setText(user.getUid());
        dialogBinding.etUserUid.setEnabled(false);
        dialogBinding.tilUserUid.setEnabled(false);

        dialogBinding.etUserUsername.setText(user.getUsername());
        dialogBinding.etUserUsername.setEnabled(false);
        dialogBinding.tilUserUsername.setEnabled(false);

        dialogBinding.etUserName.setText(user.getName());
        dialogBinding.etUserAge.setText(String.valueOf(user.getAge()));
        dialogBinding.etUserPhone.setText(user.getPhone());

        if (AppUser.ROLE_ADMIN.equalsIgnoreCase(user.getRole())) {
            dialogBinding.rbRoleAdmin.setChecked(true);
        } else if (AppUser.ROLE_MANAGER.equalsIgnoreCase(user.getRole())) {
            dialogBinding.rbRoleManager.setChecked(true);
        } else {
            dialogBinding.rbRoleEmployee.setChecked(true);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.btn_save, null)
                .setNegativeButton(R.string.btn_cancel, null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = dialogBinding.etUserName.getText() != null ?
                    dialogBinding.etUserName.getText().toString().trim() : "";
            String ageStr = dialogBinding.etUserAge.getText() != null ?
                    dialogBinding.etUserAge.getText().toString().trim() : "";
            String phone = dialogBinding.etUserPhone.getText() != null ?
                    dialogBinding.etUserPhone.getText().toString().trim() : "";

            int age = 0;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException ignored) {
            }

            String role = AppUser.ROLE_EMPLOYEE;
            if (dialogBinding.rbRoleAdmin.isChecked()) {
                role = AppUser.ROLE_ADMIN;
            } else if (dialogBinding.rbRoleManager.isChecked()) {
                role = AppUser.ROLE_MANAGER;
            }

            String validationError = UserManagementPolicy.validateUserProfile(name, age, phone, role);
            if (validationError != null) {
                Toast.makeText(requireContext(), validationError, Toast.LENGTH_LONG).show();
                return; // Dialog remains open on validation error
            }

            viewModel.updateUser(user.getUid(), name, age, phone, role);
            dialog.dismiss();
        });
    }

    @Override
    public void onDeleteUser(AppUser user) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_confirm_delete_user)
                .setMessage(getString(R.string.confirm_delete_user_format, user.getUsername()))
                .setPositiveButton(R.string.btn_delete_profile, (dialog, which) -> {
                    viewModel.deleteUser(user.getUid());
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void showAddUserDialog() {
        DialogUserEditBinding dialogBinding = DialogUserEditBinding.inflate(getLayoutInflater());
        dialogBinding.tvDialogTitle.setText(R.string.title_add_user);
        dialogBinding.tvBackendNotice.setVisibility(View.VISIBLE);
        dialogBinding.tilUserEmail.setVisibility(View.VISIBLE);
        dialogBinding.tvStatusLabel.setVisibility(View.VISIBLE);
        dialogBinding.rgUserStatus.setVisibility(View.VISIBLE);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.btn_create_profile, null)
                .setNegativeButton(R.string.btn_cancel, null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String uid = dialogBinding.etUserUid.getText() != null ?
                    dialogBinding.etUserUid.getText().toString().trim() : "";
            String username = dialogBinding.etUserUsername.getText() != null ?
                    dialogBinding.etUserUsername.getText().toString().trim() : "";
            String name = dialogBinding.etUserName.getText() != null ?
                    dialogBinding.etUserName.getText().toString().trim() : "";
            String ageStr = dialogBinding.etUserAge.getText() != null ?
                    dialogBinding.etUserAge.getText().toString().trim() : "";
            String phone = dialogBinding.etUserPhone.getText() != null ?
                    dialogBinding.etUserPhone.getText().toString().trim() : "";
            String email = dialogBinding.etUserEmail.getText() != null ?
                    dialogBinding.etUserEmail.getText().toString().trim() : "";
            if (email.isEmpty()) {
                email = null;
            }

            int age = 0;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException ignored) {
            }

            String role = AppUser.ROLE_EMPLOYEE;
            if (dialogBinding.rbRoleAdmin.isChecked()) {
                role = AppUser.ROLE_ADMIN;
            } else if (dialogBinding.rbRoleManager.isChecked()) {
                role = AppUser.ROLE_MANAGER;
            }

            String status = dialogBinding.rbStatusLocked.isChecked() ?
                    AppUser.STATUS_LOCKED : AppUser.STATUS_NORMAL;

            AppUser newUser = new AppUser(
                    uid,
                    username,
                    email,
                    name,
                    age,
                    phone,
                    status,
                    role,
                    null
            );

            String validationError = UserManagementPolicy.validateNewUser(currentUser, newUser);
            if (validationError != null) {
                Toast.makeText(requireContext(), validationError, Toast.LENGTH_LONG).show();
                return; // Dialog remains open on validation error
            }

            viewModel.createUser(newUser);
            dialog.dismiss();
        });
    }

    @Override
    public void onViewHistory(AppUser user) {
        if (user == null || getView() == null) return;
        android.os.Bundle args = new android.os.Bundle();
        args.putString(com.example.studentmgmt.ui.history.LoginHistoryFragment.ARG_TARGET_UID, user.getUid());
        args.putString(com.example.studentmgmt.ui.history.LoginHistoryFragment.ARG_TARGET_NAME, user.getName());
        androidx.navigation.Navigation.findNavController(requireView()).navigate(com.example.studentmgmt.R.id.nav_login_history, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
