package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;

import com.example.studentmgmt.data.AuthCallback;
import com.example.studentmgmt.data.AuthRepository;
import com.example.studentmgmt.data.ProfileRepository;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.profile.AvatarUploadPolicy;
import com.example.studentmgmt.ui.profile.ProfileViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for ProfileViewModel.
 * Verifies profile loading, avatar selection cancellation, MIME/size validation,
 * upload failures, download URL failures, partial failure retry, and listener cleanup.
 */
public class ProfileViewModelTest {

    private FakeProfileRepository fakeProfileRepository;
    private FakeAuthRepository fakeAuthRepository;
    private AppUser currentUser;
    private ProfileViewModel viewModel;

    @Before
    public void setUp() {
        ArchTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
            @Override
            public void executeOnDiskIO(Runnable runnable) {
                runnable.run();
            }

            @Override
            public void postToMainThread(Runnable runnable) {
                runnable.run();
            }

            @Override
            public boolean isMainThread() {
                return true;
            }
        });

        currentUser = new AppUser(
                "user-123",
                "testuser",
                "testuser@studentmgmt.edu.vn",
                "Nguyễn Văn A",
                25,
                "0901234567",
                AppUser.STATUS_NORMAL,
                AppUser.ROLE_EMPLOYEE,
                "https://old.url/avatar.jpg"
        );

        fakeAuthRepository = new FakeAuthRepository(currentUser);
        fakeProfileRepository = new FakeProfileRepository();

        viewModel = new ProfileViewModel(fakeProfileRepository, fakeAuthRepository);
    }

    @After
    public void tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @Test
    public void init_loadsUserAndInitializesIdleState() {
        viewModel.start(currentUser);

        assertNotNull(viewModel.getCurrentUser().getValue());
        assertEquals("user-123", viewModel.getCurrentUser().getValue().getUid());

        AvatarUploadPolicy.UploadUiState state = viewModel.getUploadState().getValue();
        assertNotNull(state);
        assertEquals(AvatarUploadPolicy.UploadStep.IDLE, state.getStep());
        assertEquals("https://old.url/avatar.jpg", state.getAvatarUrl());
        assertNull(state.getErrorMessage());
    }

    @Test
    public void onImagePicked_nullData_revertsToIdleAndKeepsOriginalUrl() {
        viewModel.start(currentUser);
        viewModel.onImagePicked(null, null);

        AvatarUploadPolicy.UploadUiState state = viewModel.getUploadState().getValue();
        assertEquals(AvatarUploadPolicy.UploadStep.IDLE, state.getStep());
        assertEquals("https://old.url/avatar.jpg", state.getAvatarUrl());
    }

    @Test
    public void onImagePicked_invalidMime_emitsErrorAndPreservesUrl() {
        viewModel.start(currentUser);
        byte[] dummyData = new byte[100];
        viewModel.onImagePicked(dummyData, "image/gif");

        AvatarUploadPolicy.UploadUiState state = viewModel.getUploadState().getValue();
        assertEquals(AvatarUploadPolicy.UploadStep.ERROR, state.getStep());
        assertEquals("https://old.url/avatar.jpg", state.getAvatarUrl());
        assertEquals(AvatarUploadPolicy.ERROR_INVALID_MIME, state.getErrorMessage());
    }

    @Test
    public void onImagePicked_fileTooLarge_emitsErrorAndPreservesUrl() {
        viewModel.start(currentUser);
        byte[] largeData = new byte[(int) (5 * 1024 * 1024)]; // Exactly 5 MiB (forbidden, must be < 5 MiB)
        viewModel.onImagePicked(largeData, "image/jpeg");

        AvatarUploadPolicy.UploadUiState state = viewModel.getUploadState().getValue();
        assertEquals(AvatarUploadPolicy.UploadStep.ERROR, state.getStep());
        assertEquals("https://old.url/avatar.jpg", state.getAvatarUrl());
        assertEquals(AvatarUploadPolicy.ERROR_FILE_TOO_LARGE, state.getErrorMessage());
    }

    @Test
    public void onImagePicked_unauthorizedSession_emitsErrorAndPreservesUrl() {
        // Change auth repo to simulate a different user
        fakeAuthRepository.setCurrentUser(new AppUser(
                "other-uid",
                "other",
                "other@studentmgmt.edu.vn",
                "Other",
                30,
                "0911223344",
                AppUser.STATUS_NORMAL,
                AppUser.ROLE_EMPLOYEE,
                null
        ));

        viewModel.start(currentUser);
        byte[] validData = new byte[1024];
        viewModel.onImagePicked(validData, "image/jpeg");

        AvatarUploadPolicy.UploadUiState state = viewModel.getUploadState().getValue();
        assertEquals(AvatarUploadPolicy.UploadStep.ERROR, state.getStep());
        assertEquals("https://old.url/avatar.jpg", state.getAvatarUrl());
        assertEquals(AvatarUploadPolicy.ERROR_UNAUTHORIZED, state.getErrorMessage());
    }

    @Test
    public void uploadAvatar_storageFailure_preservesOriginalUrlAndEmitsError() {
        fakeProfileRepository.shouldUploadFail = true;
        fakeProfileRepository.uploadFailureMessage = "Mất kết nối mạng.";

        viewModel.start(currentUser);
        byte[] validData = new byte[2048];
        viewModel.onImagePicked(validData, "image/jpeg");

        AvatarUploadPolicy.UploadUiState state = viewModel.getUploadState().getValue();
        assertEquals(AvatarUploadPolicy.UploadStep.ERROR, state.getStep());
        assertEquals("https://old.url/avatar.jpg", state.getAvatarUrl());
        assertEquals("Mất kết nối mạng.", state.getErrorMessage());
        assertNull(state.getPendingStorageUrl());
    }

    @Test
    public void uploadAvatar_firestoreUpdateFailure_preservesOriginalUrl_cachesPendingUrl_allowsRetry() {
        fakeProfileRepository.shouldUpdateFail = true;
        fakeProfileRepository.updateFailureMessage = "Lỗi ghi cơ sở dữ liệu.";

        viewModel.start(currentUser);
        byte[] validData = new byte[2048];
        viewModel.onImagePicked(validData, "image/png");

        AvatarUploadPolicy.UploadUiState state = viewModel.getUploadState().getValue();
        assertEquals(AvatarUploadPolicy.UploadStep.ERROR, state.getStep());
        // Preserves original URL in UI/model
        assertEquals("https://old.url/avatar.jpg", state.getAvatarUrl());
        // Retains downloaded storage URL in pending cache for retry
        assertEquals("https://fake-download-url.com/avatars/user-123.png", state.getPendingStorageUrl());
        assertTrue(state.canRetryFirestoreUpdate());
    }

    @Test
    public void retryFirestoreUpdate_success_appliesNewUrl() {
        fakeProfileRepository.shouldUpdateFail = true;
        viewModel.start(currentUser);
        byte[] validData = new byte[2048];
        viewModel.onImagePicked(validData, "image/png");

        assertTrue(viewModel.getUploadState().getValue().canRetryFirestoreUpdate());

        // Now simulate update succeeds on retry
        fakeProfileRepository.shouldUpdateFail = false;
        viewModel.retryFirestoreUpdate();

        AvatarUploadPolicy.UploadUiState successState = viewModel.getUploadState().getValue();
        assertEquals(AvatarUploadPolicy.UploadStep.SUCCESS, successState.getStep());
        assertEquals("https://fake-download-url.com/avatars/user-123.png", successState.getAvatarUrl());
        assertNull(successState.getPendingStorageUrl());
        assertNull(successState.getErrorMessage());
    }

    @Test
    public void uploadAvatar_fullSuccess_appliesNewUrl() {
        viewModel.start(currentUser);
        byte[] validData = new byte[2048];
        viewModel.onImagePicked(validData, "image/jpeg");

        AvatarUploadPolicy.UploadUiState successState = viewModel.getUploadState().getValue();
        assertEquals(AvatarUploadPolicy.UploadStep.SUCCESS, successState.getStep());
        assertEquals("https://fake-download-url.com/avatars/user-123.jpg", successState.getAvatarUrl());
        assertNull(successState.getPendingStorageUrl());
        assertNull(successState.getErrorMessage());
    }

    @Test
    public void onCleared_cleansUpListener() {
        viewModel.start(currentUser);
        assertFalse(fakeProfileRepository.listenerRemoved);

        viewModel.onCleared();
        assertTrue(fakeProfileRepository.listenerRemoved);
    }

    // --- Fake Test Implementations ---

    private static class FakeProfileRepository implements ProfileRepository {
        boolean shouldUploadFail = false;
        String uploadFailureMessage = "Upload error";
        boolean shouldUpdateFail = false;
        String updateFailureMessage = "Update error";
        boolean listenerRemoved = false;

        @Override
        public void uploadAvatar(String uid, byte[] data, String mimeType, UploadCallback callback) {
            if (shouldUploadFail) {
                callback.onError(uploadFailureMessage);
            } else {
                callback.onProgress(50);
                callback.onProgress(100);
                String ext = "image/png".equals(mimeType) ? ".png" : ".jpg";
                callback.onSuccess("https://fake-download-url.com/avatars/" + uid + ext);
            }
        }

        @Override
        public void updateAvatarUrl(String uid, String downloadUrl, UpdateCallback callback) {
            if (shouldUpdateFail) {
                callback.onError(updateFailureMessage);
            } else {
                callback.onSuccess();
            }
        }

        @Override
        public ListenerHandle listenProfile(String uid, ProfileCallback callback) {
            return () -> listenerRemoved = true;
        }
    }

    private static class FakeAuthRepository implements AuthRepository {
        private AppUser currentUser;

        FakeAuthRepository(AppUser currentUser) {
            this.currentUser = currentUser;
        }

        void setCurrentUser(AppUser user) {
            this.currentUser = user;
        }

        @Override
        public void signIn(String identifier, String password, AuthCallback callback) {}

        @Override
        public void signOut() {
            currentUser = null;
        }

        @Override
        public AppUser getCurrentUser() {
            return currentUser;
        }

        @Override
        public boolean isSignedIn() {
            return currentUser != null;
        }
    }
}
