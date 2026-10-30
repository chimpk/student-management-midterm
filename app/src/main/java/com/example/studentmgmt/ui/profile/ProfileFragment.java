package com.example.studentmgmt.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.databinding.FragmentProfileBinding;
import com.example.studentmgmt.databinding.DialogProfileEditBinding;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.UiState;
import com.example.studentmgmt.utils.VietnameseTextInputFormatter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Fragment displaying current user profile information and allowing avatar image upload.
 * Conforms to storage rules: avatars/{uid}.{jpg|png}, size < 5 MiB, JPEG/PNG only.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private AlertDialog editProfileDialog;
    private DialogProfileEditBinding editProfileBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleImageSelected
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        com.example.studentmgmt.data.FirebaseProfileRepository.setLocalDir(requireContext().getFilesDir());
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        AppUser sessionUser = AuthRepositoryProvider.getRepository().getCurrentUser();
        if (sessionUser == null) {
            Toast.makeText(requireContext(), "Phiên làm việc không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.start(sessionUser);

        binding.btnChangeAvatar.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        binding.ivAvatar.setOnClickListener(v -> {
            AvatarUploadPolicy.UploadUiState state = viewModel.getUploadState().getValue();
            if (state == null || !state.isUploadingOrUpdating()) {
                imagePickerLauncher.launch("image/*");
            }
        });

        binding.btnRetryUpdate.setOnClickListener(v -> viewModel.retryFirestoreUpdate());
        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        binding.btnViewLoginHistory.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.nav_login_history);
        });

        binding.btnLogout.setOnClickListener(v -> {
            if (getActivity() instanceof com.example.studentmgmt.MainActivity) {
                ((com.example.studentmgmt.MainActivity) getActivity()).performLogout();
            }
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), this::renderUserInfo);
        viewModel.getUploadState().observe(getViewLifecycleOwner(), this::renderUploadState);
        viewModel.getProfileUpdateState().observe(getViewLifecycleOwner(), this::renderProfileUpdateState);
        viewModel.getProfileLoadError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                com.google.android.material.snackbar.Snackbar.make(binding.getRoot(), error, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_retry, v -> viewModel.retryProfileLoad())
                        .show();
                viewModel.clearProfileLoadError();
            }
        });
    }

    private void showEditProfileDialog() {
        AppUser user = viewModel.getCurrentUser().getValue();
        if (user == null || editProfileDialog != null) return;
        viewModel.clearProfileUpdateState();

        editProfileBinding = DialogProfileEditBinding.inflate(getLayoutInflater());
        editProfileBinding.etProfileName.setText(user.getName());
        editProfileBinding.etProfileAge.setText(user.getAge() > 0 ? String.valueOf(user.getAge()) : "");
        editProfileBinding.etProfilePhone.setText(user.getPhone());

        editProfileDialog = new AlertDialog.Builder(requireContext())
                .setView(editProfileBinding.getRoot())
                .create();
        editProfileDialog.setOnDismissListener(ignored -> {
            editProfileDialog = null;
            editProfileBinding = null;
            viewModel.clearProfileUpdateState();
        });
        editProfileBinding.btnCancelProfile.setOnClickListener(v -> editProfileDialog.dismiss());
        editProfileBinding.btnSaveProfile.setOnClickListener(v -> submitProfileEdit());
        editProfileDialog.show();
    }

    private void submitProfileEdit() {
        if (editProfileBinding == null) return;
        String name = editProfileBinding.etProfileName.getText() == null ? ""
                : VietnameseTextInputFormatter.capitalizeWords(
                        editProfileBinding.etProfileName.getText().toString().trim());
        String ageText = editProfileBinding.etProfileAge.getText() == null ? ""
                : editProfileBinding.etProfileAge.getText().toString().trim();
        String phone = editProfileBinding.etProfilePhone.getText() == null ? ""
                : editProfileBinding.etProfilePhone.getText().toString().trim();

        boolean invalid = false;
        if (name.length() < 2 || name.length() > 100) {
            editProfileBinding.tilProfileName.setError(getString(R.string.error_name_length));
            invalid = true;
        } else {
            editProfileBinding.tilProfileName.setError(null);
        }

        int age = 0;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException ignored) {
            invalid = true;
        }
        if (age < 18 || age > 60) {
            editProfileBinding.tilProfileAge.setError(getString(R.string.error_age_invalid));
            invalid = true;
        } else {
            editProfileBinding.tilProfileAge.setError(null);
        }

        if (TextUtils.isEmpty(phone) || !phone.matches("^0[0-9]{9}$")) {
            editProfileBinding.tilProfilePhone.setError(getString(R.string.error_phone_invalid));
            invalid = true;
        } else {
            editProfileBinding.tilProfilePhone.setError(null);
        }
        if (!invalid) viewModel.updateProfile(name, age, phone);
    }

    private void renderProfileUpdateState(UiState<AppUser> state) {
        if (state == null || editProfileBinding == null) return;
        boolean saving = state.isLoading();
        editProfileBinding.btnSaveProfile.setEnabled(!saving);
        editProfileBinding.btnSaveProfile.setText(saving ? R.string.btn_saving : R.string.btn_save);
        editProfileBinding.btnCancelProfile.setEnabled(!saving);

        if (state.isContent()) {
            editProfileDialog.dismiss();
            Toast.makeText(requireContext(), R.string.toast_profile_update_success, Toast.LENGTH_SHORT).show();
        } else if (state.isError()) {
            com.google.android.material.snackbar.Snackbar.make(
                    editProfileBinding.getRoot(), state.getErrorMessage(),
                    com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show();
        }
    }

    private void renderUserInfo(AppUser user) {
        if (user == null || binding == null) return;

        binding.tvProfileName.setText(user.getName() != null && !user.getName().isEmpty() ? user.getName() : getString(R.string.name_unspecified));
        binding.tvProfileUsername.setText(getString(R.string.username_display_format, user.getUsername()));
        binding.tvProfileEmail.setText(user.getEmail() != null && !user.getEmail().isEmpty() ? user.getEmail() : getString(R.string.value_not_updated));
        binding.tvProfileAge.setText(user.getAge() > 0 ? getString(R.string.age_format, user.getAge()) : "--");
        binding.tvProfilePhone.setText(user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "--");

        // Role display
        String roleText;
        if (AppUser.ROLE_ADMIN.equals(user.getRole())) {
            roleText = getString(R.string.role_admin_display);
        } else if (AppUser.ROLE_MANAGER.equals(user.getRole())) {
            roleText = getString(R.string.role_manager_display);
        } else {
            roleText = getString(R.string.role_employee_display);
        }
        binding.chipProfileRole.setText(roleText);

        // Status display
        String statusText;
        if (AppUser.STATUS_NORMAL.equals(user.getStatus())) {
            statusText = getString(R.string.status_normal);
        } else {
            statusText = getString(R.string.status_locked);
        }
        binding.chipProfileStatus.setText(statusText);
    }

    private void renderUploadState(AvatarUploadPolicy.UploadUiState state) {
        if (state == null || binding == null) return;

        // Avatar image rendering with Glide
        String avatarUrl = state.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .circleCrop()
                    .into(binding.ivAvatar);
        } else {
            binding.ivAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        switch (state.getStep()) {
            case UPLOADING:
                binding.layoutUploadProgress.setVisibility(View.VISIBLE);
                binding.progressUpload.setIndeterminate(false);
                binding.progressUpload.setProgress(state.getProgress());
                binding.tvUploadStatus.setText(getString(R.string.upload_progress_format, state.getProgress()));
                binding.btnChangeAvatar.setEnabled(false);
                binding.cardError.setVisibility(View.GONE);
                break;

            case UPDATING_FIRESTORE:
                binding.layoutUploadProgress.setVisibility(View.VISIBLE);
                binding.progressUpload.setIndeterminate(true);
                binding.tvUploadStatus.setText(R.string.status_updating_profile);
                binding.btnChangeAvatar.setEnabled(false);
                binding.cardError.setVisibility(View.GONE);
                break;

            case ERROR:
                binding.layoutUploadProgress.setVisibility(View.GONE);
                binding.btnChangeAvatar.setEnabled(true);
                binding.cardError.setVisibility(View.VISIBLE);
                binding.tvErrorMessage.setText(state.getErrorMessage());
                if (state.canRetryFirestoreUpdate()) {
                    binding.btnRetryUpdate.setVisibility(View.VISIBLE);
                } else {
                    binding.btnRetryUpdate.setVisibility(View.GONE);
                }
                break;

            case SUCCESS:
                binding.layoutUploadProgress.setVisibility(View.GONE);
                binding.btnChangeAvatar.setEnabled(true);
                binding.cardError.setVisibility(View.GONE);
                Toast.makeText(requireContext(), R.string.toast_avatar_success, Toast.LENGTH_SHORT).show();
                break;

            case IDLE:
            case SELECTING:
            default:
                binding.layoutUploadProgress.setVisibility(View.GONE);
                binding.btnChangeAvatar.setEnabled(true);
                binding.cardError.setVisibility(View.GONE);
                break;
        }
    }

    private void handleImageSelected(Uri uri) {
        if (uri == null) {
            viewModel.onImagePicked(null, null);
            return;
        }

        try {
            String mimeType = requireContext().getContentResolver().getType(uri);
            if (mimeType == null) {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                if (fileExtension != null) {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase(java.util.Locale.ROOT));
                }
            }

            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                viewModel.onImagePicked(new byte[0], mimeType);
                return;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            long totalRead = 0;
            long readLimit = AvatarUploadPolicy.MAX_FILE_SIZE + 1024;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
                totalRead += read;
                if (totalRead > readLimit) {
                    break;
                }
            }
            inputStream.close();

            byte[] bytes = outputStream.toByteArray();
            viewModel.onImagePicked(bytes, mimeType);

        } catch (Exception e) {
            Toast.makeText(requireContext(), getString(R.string.error_read_image_format, e.getMessage()), Toast.LENGTH_SHORT).show();
            viewModel.onImagePicked(null, null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (editProfileDialog != null) editProfileDialog.dismiss();
        binding = null;
    }
}
