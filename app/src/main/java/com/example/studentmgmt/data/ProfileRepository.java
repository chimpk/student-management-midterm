package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;

/**
 * Repository interface for user profile operations.
 * Handles avatar image uploads, avatar URL updates in Firestore,
 * and realtime profile streaming.
 */
public interface ProfileRepository {

    interface ListenerHandle {
        void remove();
    }

    interface UploadCallback {
        void onProgress(int percent);
        void onSuccess(String downloadUrl);
        void onError(String errorMessage);
    }

    interface UpdateCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    interface ProfileCallback {
        void onProfileLoaded(AppUser user);
        void onError(String errorMessage);
    }

    /**
     * Uploads avatar image data to Cloud Storage.
     * Path contract: avatars/{uid}.{jpg|png}
     */
    void uploadAvatar(String uid, byte[] data, String mimeType, UploadCallback callback);

    /**
     * Updates the avatarUrl field of the user document in Firestore.
     */
    void updateAvatarUrl(String uid, String downloadUrl, UpdateCallback callback);

    /**
     * Listens to realtime changes in the user document.
     */
    ListenerHandle listenProfile(String uid, ProfileCallback callback);
}
