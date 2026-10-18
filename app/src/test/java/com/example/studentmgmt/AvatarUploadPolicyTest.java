package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.studentmgmt.ui.profile.AvatarUploadPolicy;

import org.junit.Test;

/**
 * Unit tests for AvatarUploadPolicy.
 * Verifies MIME validation, size boundary (< 5 MiB), path contracts,
 * authority checks, and state machine transitions with safe rollback.
 */
public class AvatarUploadPolicyTest {

    private static final long MAX_ALLOWED_SIZE = (5 * 1024 * 1024) - 1; // 5242879 bytes
    private static final long FIVE_MIB = 5 * 1024 * 1024; // 5242880 bytes

    @Test
    public void validateMimeType_acceptsJpegAndPng() {
        assertTrue(AvatarUploadPolicy.isValidMimeType("image/jpeg"));
        assertTrue(AvatarUploadPolicy.isValidMimeType("image/png"));
        assertTrue(AvatarUploadPolicy.isValidMimeType("IMAGE/JPEG"));
        assertTrue(AvatarUploadPolicy.isValidMimeType("IMAGE/PNG"));
    }

    @Test
    public void validateMimeType_rejectsOthers() {
        assertFalse(AvatarUploadPolicy.isValidMimeType("image/webp"));
        assertFalse(AvatarUploadPolicy.isValidMimeType("image/gif"));
        assertFalse(AvatarUploadPolicy.isValidMimeType("application/pdf"));
        assertFalse(AvatarUploadPolicy.isValidMimeType(null));
        assertFalse(AvatarUploadPolicy.isValidMimeType(""));
    }

    @Test
    public void validateFileSize_acceptsUnder5MiB() {
        assertTrue(AvatarUploadPolicy.isValidFileSize(1));
        assertTrue(AvatarUploadPolicy.isValidFileSize(1024 * 1024));
        assertTrue(AvatarUploadPolicy.isValidFileSize(MAX_ALLOWED_SIZE));
    }

    @Test
    public void validateFileSize_rejectsZeroNegativeOrAtAndOver5MiB() {
        assertFalse(AvatarUploadPolicy.isValidFileSize(0));
        assertFalse(AvatarUploadPolicy.isValidFileSize(-1));
        assertFalse(AvatarUploadPolicy.isValidFileSize(FIVE_MIB));
        assertFalse(AvatarUploadPolicy.isValidFileSize(FIVE_MIB + 1));
        assertFalse(AvatarUploadPolicy.isValidFileSize(10 * 1024 * 1024));
    }

    @Test
    public void buildStoragePath_generatesCorrectContract() {
        assertEquals("avatars/user-123.jpg", AvatarUploadPolicy.buildStoragePath("user-123", "image/jpeg"));
        assertEquals("avatars/user-123.png", AvatarUploadPolicy.buildStoragePath("user-123", "image/png"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildStoragePath_rejectsNullOrBlankUid() {
        AvatarUploadPolicy.buildStoragePath("", "image/jpeg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildStoragePath_rejectsInvalidMime() {
        AvatarUploadPolicy.buildStoragePath("user-123", "image/gif");
    }

    @Test
    public void isAuthorized_matchesSessionUid() {
        assertTrue(AvatarUploadPolicy.isAuthorized("uid-abc", "uid-abc"));
        assertFalse(AvatarUploadPolicy.isAuthorized("uid-abc", "uid-other"));
        assertFalse(AvatarUploadPolicy.isAuthorized(null, "uid-abc"));
        assertFalse(AvatarUploadPolicy.isAuthorized("uid-abc", null));
    }

    @Test
    public void isValidAvatarUrl_conformsToFirestoreRules() {
        assertTrue(AvatarUploadPolicy.isValidAvatarUrl("https://firebasestorage.googleapis.com/..."));
        assertFalse(AvatarUploadPolicy.isValidAvatarUrl(null));
        assertFalse(AvatarUploadPolicy.isValidAvatarUrl(""));

        StringBuilder longUrl = new StringBuilder();
        for (int i = 0; i < 2049; i++) {
            longUrl.append('a');
        }
        assertFalse(AvatarUploadPolicy.isValidAvatarUrl(longUrl.toString()));
    }

    @Test
    public void stateMachine_cancelMaintainsOriginalUrl() {
        AvatarUploadPolicy.UploadUiState state = AvatarUploadPolicy.UploadUiState.idle("https://old.url/avatar.jpg");
        AvatarUploadPolicy.UploadUiState selecting = state.toSelecting();
        assertEquals(AvatarUploadPolicy.UploadStep.SELECTING, selecting.getStep());
        assertEquals("https://old.url/avatar.jpg", selecting.getAvatarUrl());

        AvatarUploadPolicy.UploadUiState cancelled = selecting.toCancelled();
        assertEquals(AvatarUploadPolicy.UploadStep.IDLE, cancelled.getStep());
        assertEquals("https://old.url/avatar.jpg", cancelled.getAvatarUrl());
        assertNull(cancelled.getErrorMessage());
    }

    @Test
    public void stateMachine_uploadFailureKeepsOriginalUrl() {
        AvatarUploadPolicy.UploadUiState state = AvatarUploadPolicy.UploadUiState.idle("https://old.url/avatar.jpg")
                .toUploading(0);
        assertEquals(AvatarUploadPolicy.UploadStep.UPLOADING, state.getStep());

        AvatarUploadPolicy.UploadUiState errorState = state.toError("Lỗi kết nối mạng khi tải ảnh lên.");
        assertEquals(AvatarUploadPolicy.UploadStep.ERROR, errorState.getStep());
        assertEquals("https://old.url/avatar.jpg", errorState.getAvatarUrl());
        assertEquals("Lỗi kết nối mạng khi tải ảnh lên.", errorState.getErrorMessage());
        assertNull(errorState.getPendingStorageUrl());
    }

    @Test
    public void stateMachine_downloadUrlFailureKeepsOriginalUrl() {
        AvatarUploadPolicy.UploadUiState state = AvatarUploadPolicy.UploadUiState.idle("https://old.url/avatar.jpg")
                .toUploading(100);

        AvatarUploadPolicy.UploadUiState errorState = state.toError("Không thể lấy đường dẫn tải ảnh.");
        assertEquals(AvatarUploadPolicy.UploadStep.ERROR, errorState.getStep());
        assertEquals("https://old.url/avatar.jpg", errorState.getAvatarUrl());
        assertEquals("Không thể lấy đường dẫn tải ảnh.", errorState.getErrorMessage());
    }

    @Test
    public void stateMachine_firestoreUpdateFailureKeepsOriginalUrlAndCachesPendingDownloadUrl() {
        AvatarUploadPolicy.UploadUiState state = AvatarUploadPolicy.UploadUiState.idle("https://old.url/avatar.jpg")
                .toUpdatingFirestore("https://new.url/uploaded_avatar.jpg");
        assertEquals(AvatarUploadPolicy.UploadStep.UPDATING_FIRESTORE, state.getStep());
        assertEquals("https://old.url/avatar.jpg", state.getAvatarUrl());
        assertEquals("https://new.url/uploaded_avatar.jpg", state.getPendingStorageUrl());

        AvatarUploadPolicy.UploadUiState failureState = state.toFirestoreUpdateError(
                "Không thể lưu thông tin hồ sơ vào cơ sở dữ liệu."
        );
        assertEquals(AvatarUploadPolicy.UploadStep.ERROR, failureState.getStep());
        // Original avatar URL is preserved in the UI/model
        assertEquals("https://old.url/avatar.jpg", failureState.getAvatarUrl());
        // But pending storage URL is cached so user can retry Firestore update without re-uploading!
        assertEquals("https://new.url/uploaded_avatar.jpg", failureState.getPendingStorageUrl());
        assertTrue(failureState.canRetryFirestoreUpdate());
    }

    @Test
    public void stateMachine_successAppliesNewUrl() {
        AvatarUploadPolicy.UploadUiState state = AvatarUploadPolicy.UploadUiState.idle("https://old.url/avatar.jpg")
                .toUpdatingFirestore("https://new.url/uploaded_avatar.jpg");

        AvatarUploadPolicy.UploadUiState successState = state.toSuccess("https://new.url/uploaded_avatar.jpg");
        assertEquals(AvatarUploadPolicy.UploadStep.SUCCESS, successState.getStep());
        assertEquals("https://new.url/uploaded_avatar.jpg", successState.getAvatarUrl());
        assertNull(successState.getPendingStorageUrl());
        assertNull(successState.getErrorMessage());
    }
}
