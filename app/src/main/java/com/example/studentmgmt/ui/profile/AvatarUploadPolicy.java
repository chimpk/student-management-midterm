package com.example.studentmgmt.ui.profile;

import java.util.Locale;
import java.util.Objects;

/**
 * Pure Java policy enforcing storage rules, size/MIME constraints,
 * user authority, and avatar upload state machine transitions.
 */
public final class AvatarUploadPolicy {

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MiB in bytes
    public static final String MIME_JPEG = "image/jpeg";
    public static final String MIME_PNG = "image/png";

    public static final String ERROR_INVALID_MIME = "Định dạng ảnh không hợp lệ. Chỉ chấp nhận ảnh JPG hoặc PNG.";
    public static final String ERROR_FILE_TOO_LARGE = "Kích thước ảnh phải nhỏ hơn 5 MB.";
    public static final String ERROR_UNAUTHORIZED = "Bạn không có quyền cập nhật ảnh đại diện của người dùng khác.";
    public static final String ERROR_UPLOAD_FAILED = "Tải ảnh lên thất bại. Vui lòng kiểm tra kết nối mạng và thử lại.";
    public static final String ERROR_DOWNLOAD_URL_FAILED = "Không thể lấy đường dẫn ảnh sau khi tải lên.";
    public static final String ERROR_FIRESTORE_UPDATE_FAILED = "Không thể lưu thông tin hồ sơ vào cơ sở dữ liệu.";

    private AvatarUploadPolicy() {
        throw new AssertionError("Utility class");
    }

    public static boolean isValidMimeType(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        String normalized = mimeType.trim().toLowerCase(Locale.US);
        return MIME_JPEG.equals(normalized) || MIME_PNG.equals(normalized);
    }

    public static boolean isValidFileSize(long sizeInBytes) {
        // Storage rules enforce: request.resource.size < 5 * 1024 * 1024
        return sizeInBytes > 0 && sizeInBytes < MAX_FILE_SIZE;
    }

    public static String resolveFileExtension(String mimeType) {
        if (mimeType == null) {
            return null;
        }
        String normalized = mimeType.trim().toLowerCase(Locale.US);
        if (MIME_JPEG.equals(normalized)) {
            return ".jpg";
        } else if (MIME_PNG.equals(normalized)) {
            return ".png";
        }
        return null;
    }

    public static String buildStoragePath(String uid, String mimeType) {
        if (uid == null || uid.trim().isEmpty()) {
            throw new IllegalArgumentException("UID must not be null or blank");
        }
        String ext = resolveFileExtension(mimeType);
        if (ext == null) {
            throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
        }
        return "avatars/" + uid.trim() + ext;
    }

    public static boolean isAuthorized(String currentSessionUid, String targetUid) {
        if (currentSessionUid == null || targetUid == null) {
            return false;
        }
        return currentSessionUid.equals(targetUid);
    }

    public static boolean isValidAvatarUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return url.length() <= 2048;
    }

    public enum UploadStep {
        IDLE,
        SELECTING,
        UPLOADING,
        UPDATING_FIRESTORE,
        SUCCESS,
        ERROR
    }

    public static final class UploadUiState {
        private final UploadStep step;
        private final String avatarUrl;
        private final int progress;
        private final String pendingStorageUrl;
        private final String errorMessage;

        public UploadUiState(UploadStep step,
                             String avatarUrl,
                             int progress,
                             String pendingStorageUrl,
                             String errorMessage) {
            this.step = Objects.requireNonNull(step, "step must not be null");
            this.avatarUrl = avatarUrl;
            this.progress = progress;
            this.pendingStorageUrl = pendingStorageUrl;
            this.errorMessage = errorMessage;
        }

        public static UploadUiState idle(String currentAvatarUrl) {
            return new UploadUiState(UploadStep.IDLE, currentAvatarUrl, 0, null, null);
        }

        public UploadUiState toSelecting() {
            return new UploadUiState(UploadStep.SELECTING, avatarUrl, 0, pendingStorageUrl, null);
        }

        public UploadUiState toCancelled() {
            return new UploadUiState(UploadStep.IDLE, avatarUrl, 0, pendingStorageUrl, null);
        }

        public UploadUiState toUploading(int progress) {
            return new UploadUiState(UploadStep.UPLOADING, avatarUrl, progress, null, null);
        }

        public UploadUiState toUpdatingFirestore(String uploadedStorageUrl) {
            return new UploadUiState(UploadStep.UPDATING_FIRESTORE, avatarUrl, 100, uploadedStorageUrl, null);
        }

        public UploadUiState toError(String errorMessage) {
            return new UploadUiState(UploadStep.ERROR, avatarUrl, 0, null, errorMessage);
        }

        public UploadUiState toFirestoreUpdateError(String errorMessage) {
            return new UploadUiState(UploadStep.ERROR, avatarUrl, 0, pendingStorageUrl, errorMessage);
        }

        public UploadUiState toSuccess(String newAvatarUrl) {
            return new UploadUiState(UploadStep.SUCCESS, newAvatarUrl, 100, null, null);
        }

        public UploadStep getStep() {
            return step;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public int getProgress() {
            return progress;
        }

        public String getPendingStorageUrl() {
            return pendingStorageUrl;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean canRetryFirestoreUpdate() {
            return pendingStorageUrl != null && !pendingStorageUrl.trim().isEmpty();
        }

        public boolean isUploadingOrUpdating() {
            return step == UploadStep.UPLOADING || step == UploadStep.UPDATING_FIRESTORE;
        }
    }
}
