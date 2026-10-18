package com.example.studentmgmt.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studentmgmt.data.AuthRepository;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.ProfileRepository;
import com.example.studentmgmt.data.ProfileRepositoryProvider;
import com.example.studentmgmt.model.AppUser;

/**
 * ViewModel managing User Profile state, avatar picking, upload progress,
 * and profile synchronization across lifecycle events.
 */
public class ProfileViewModel extends ViewModel {

    private final ProfileRepository profileRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<AppUser> currentUser = new MutableLiveData<>();
    private final MutableLiveData<AvatarUploadPolicy.UploadUiState> uploadState = new MutableLiveData<>();
    private final MutableLiveData<String> profileLoadError = new MutableLiveData<>(null);

    private ProfileRepository.ListenerHandle profileListenerHandle;
    private long profileListenerGeneration = 0;

    public ProfileViewModel() {
        this(ProfileRepositoryProvider.getRepository(), AuthRepositoryProvider.getRepository());
    }

    public ProfileViewModel(ProfileRepository profileRepository, AuthRepository authRepository) {
        this.profileRepository = profileRepository;
        this.authRepository = authRepository;
    }

    public LiveData<AppUser> getCurrentUser() {
        return currentUser;
    }

    public LiveData<AvatarUploadPolicy.UploadUiState> getUploadState() {
        return uploadState;
    }

    public LiveData<String> getProfileLoadError() {
        return profileLoadError;
    }

    public void clearProfileLoadError() {
        profileLoadError.setValue(null);
    }

    public void start(AppUser initialUser) {
        if (initialUser == null) {
            return;
        }
        if (currentUser.getValue() == null) {
            currentUser.setValue(initialUser);
            uploadState.setValue(AvatarUploadPolicy.UploadUiState.idle(initialUser.getAvatarUrl()));
        }

        if (profileListenerHandle == null && profileRepository != null) {
            final long token = ++profileListenerGeneration;
            profileListenerHandle = profileRepository.listenProfile(initialUser.getUid(), new ProfileRepository.ProfileCallback() {
                @Override
                public void onProfileLoaded(AppUser user) {
                    if (token != profileListenerGeneration) {
                        return; // Stale callback from older listener, discard
                    }
                    profileLoadError.postValue(null);
                    currentUser.postValue(user);
                    AvatarUploadPolicy.UploadUiState currentUpload = uploadState.getValue();
                    if (currentUpload == null || currentUpload.getStep() == AvatarUploadPolicy.UploadStep.IDLE) {
                        uploadState.postValue(AvatarUploadPolicy.UploadUiState.idle(user.getAvatarUrl()));
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (token != profileListenerGeneration) {
                        return; // Stale callback from older listener, discard
                    }
                    // Retain current user if realtime sync encounters temporary issue,
                    // but surface load error so UI can display offline/retry notice
                    profileLoadError.postValue(errorMessage);
                }
            });
        }
    }

    public void retryProfileLoad() {
        if (profileListenerHandle != null) {
            profileListenerHandle.remove();
            profileListenerHandle = null;
        }
        profileLoadError.setValue(null);
        AppUser user = currentUser.getValue();
        if (user != null) {
            start(user);
        }
    }

    public void uploadAvatar(byte[] data, String mimeType) {
        onImagePicked(data, mimeType);
    }

    public void onImagePicked(byte[] data, String mimeType) {
        AvatarUploadPolicy.UploadUiState activeUpload = uploadState.getValue();
        if (activeUpload != null && (activeUpload.getStep() == AvatarUploadPolicy.UploadStep.UPLOADING
                || activeUpload.getStep() == AvatarUploadPolicy.UploadStep.UPDATING_FIRESTORE)) {
            // Guard against duplicate concurrent upload clicks
            return;
        }

        AppUser user = currentUser.getValue();
        String currentUrl = user != null ? user.getAvatarUrl() : null;

        if (data == null) {
            AvatarUploadPolicy.UploadUiState current = uploadState.getValue();
            if (current != null) {
                uploadState.setValue(current.toCancelled());
            } else {
                uploadState.setValue(AvatarUploadPolicy.UploadUiState.idle(currentUrl));
            }
            return;
        }

        if (!AvatarUploadPolicy.isValidMimeType(mimeType)) {
            AvatarUploadPolicy.UploadUiState current = uploadState.getValue();
            String existingUrl = current != null ? current.getAvatarUrl() : currentUrl;
            uploadState.setValue(AvatarUploadPolicy.UploadUiState.idle(existingUrl).toError(AvatarUploadPolicy.ERROR_INVALID_MIME));
            return;
        }

        if (!AvatarUploadPolicy.isValidFileSize(data.length)) {
            AvatarUploadPolicy.UploadUiState current = uploadState.getValue();
            String existingUrl = current != null ? current.getAvatarUrl() : currentUrl;
            uploadState.setValue(AvatarUploadPolicy.UploadUiState.idle(existingUrl).toError(AvatarUploadPolicy.ERROR_FILE_TOO_LARGE));
            return;
        }

        AppUser sessionUser = authRepository != null ? authRepository.getCurrentUser() : null;
        String sessionUid = sessionUser != null ? sessionUser.getUid() : null;
        String targetUid = user != null ? user.getUid() : null;

        if (!AvatarUploadPolicy.isAuthorized(sessionUid, targetUid)) {
            AvatarUploadPolicy.UploadUiState current = uploadState.getValue();
            String existingUrl = current != null ? current.getAvatarUrl() : currentUrl;
            uploadState.setValue(AvatarUploadPolicy.UploadUiState.idle(existingUrl).toError(AvatarUploadPolicy.ERROR_UNAUTHORIZED));
            return;
        }

        AvatarUploadPolicy.UploadUiState current = uploadState.getValue();
        String existingUrl = current != null ? current.getAvatarUrl() : currentUrl;
        uploadState.setValue(AvatarUploadPolicy.UploadUiState.idle(existingUrl).toUploading(0));

        profileRepository.uploadAvatar(targetUid, data, mimeType, new ProfileRepository.UploadCallback() {
            @Override
            public void onProgress(int percent) {
                AvatarUploadPolicy.UploadUiState s = uploadState.getValue();
                if (s != null && s.getStep() == AvatarUploadPolicy.UploadStep.UPLOADING) {
                    uploadState.postValue(s.toUploading(percent));
                }
            }

            @Override
            public void onSuccess(String downloadUrl) {
                AvatarUploadPolicy.UploadUiState s = uploadState.getValue();
                if (s != null) {
                    uploadState.postValue(s.toUpdatingFirestore(downloadUrl));
                }
                saveAvatarUrlToFirestore(targetUid, downloadUrl);
            }

            @Override
            public void onError(String errorMessage) {
                AvatarUploadPolicy.UploadUiState s = uploadState.getValue();
                if (s != null) {
                    uploadState.postValue(s.toError(errorMessage));
                }
            }
        });
    }

    public void retryFirestoreUpdate() {
        AvatarUploadPolicy.UploadUiState state = uploadState.getValue();
        AppUser user = currentUser.getValue();
        if (state != null && state.canRetryFirestoreUpdate() && user != null) {
            String pendingUrl = state.getPendingStorageUrl();
            uploadState.setValue(state.toUpdatingFirestore(pendingUrl));
            saveAvatarUrlToFirestore(user.getUid(), pendingUrl);
        }
    }

    private void saveAvatarUrlToFirestore(String uid, String downloadUrl) {
        profileRepository.updateAvatarUrl(uid, downloadUrl, new ProfileRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                AvatarUploadPolicy.UploadUiState s = uploadState.getValue();
                if (s != null) {
                    uploadState.postValue(s.toSuccess(downloadUrl));
                }
                AppUser user = currentUser.getValue();
                if (user != null) {
                    AppUser updated = new AppUser(
                            user.getUid(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getName(),
                            user.getAge(),
                            user.getPhone(),
                            user.getStatus(),
                            user.getRole(),
                            downloadUrl
                    );
                    currentUser.postValue(updated);
                }
            }

            @Override
            public void onError(String errorMessage) {
                AvatarUploadPolicy.UploadUiState s = uploadState.getValue();
                if (s != null) {
                    uploadState.postValue(s.toFirestoreUpdateError(errorMessage));
                }
            }
        });
    }

    @Override
    public void onCleared() {
        super.onCleared();
        if (profileListenerHandle != null) {
            profileListenerHandle.remove();
            profileListenerHandle = null;
        }
    }
}
