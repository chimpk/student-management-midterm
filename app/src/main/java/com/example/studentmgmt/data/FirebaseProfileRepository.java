package com.example.studentmgmt.data;

import android.net.Uri;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.profile.AvatarUploadPolicy;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Concrete implementation of ProfileRepository.
 * Primary: Firebase Storage for remote uploads.
 * Fallback / Local: Safely stores avatar into private app storage if Cloud Storage is unavailable.
 */
public class FirebaseProfileRepository implements ProfileRepository {

    private static File localDir;

    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;

    public static void setLocalDir(File dir) {
        localDir = dir;
    }

    public FirebaseProfileRepository() {
        FirebaseFirestore fs = null;
        FirebaseStorage st = null;
        try {
            fs = FirebaseFirestore.getInstance();
            st = FirebaseStorage.getInstance();
        } catch (Exception ignored) {
        }
        this.firestore = fs;
        this.storage = st;
    }

    public FirebaseProfileRepository(FirebaseFirestore firestore, FirebaseStorage storage) {
        this.firestore = firestore;
        this.storage = storage;
    }

    private static File saveToLocalFile(String uid, byte[] data, String mimeType) {
        if (localDir == null || data == null || uid == null) {
            return null;
        }
        try {
            File avatarsFolder = new File(localDir, "avatars");
            if (!avatarsFolder.exists()) {
                avatarsFolder.mkdirs();
            }
            String ext = AvatarUploadPolicy.resolveFileExtension(mimeType);
            if (ext == null) {
                ext = ".jpg";
            }
            File targetFile = new File(avatarsFolder, uid.trim() + ext);
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(data);
                fos.flush();
            }
            return targetFile;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void uploadAvatar(String uid, byte[] data, String mimeType, UploadCallback callback) {
        if (callback == null) return;

        if (data == null || !AvatarUploadPolicy.isValidFileSize(data.length)) {
            callback.onError(AvatarUploadPolicy.ERROR_FILE_TOO_LARGE);
            return;
        }

        if (!AvatarUploadPolicy.isValidMimeType(mimeType)) {
            callback.onError(AvatarUploadPolicy.ERROR_INVALID_MIME);
            return;
        }

        String storagePath;
        try {
            storagePath = AvatarUploadPolicy.buildStoragePath(uid, mimeType);
        } catch (Exception e) {
            callback.onError("Đường dẫn lưu trữ không hợp lệ: " + e.getMessage());
            return;
        }

        // If Cloud Storage is not available, immediately fallback to local private storage
        if (storage == null) {
            File localAvatar = saveToLocalFile(uid, data, mimeType);
            if (localAvatar != null) {
                callback.onProgress(100);
                callback.onSuccess(Uri.fromFile(localAvatar).toString());
                return;
            }
            callback.onError("Firebase Storage service không khả dụng.");
            return;
        }

        StorageReference ref = storage.getReference().child(storagePath);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(mimeType.trim().toLowerCase(Locale.US))
                .build();

        UploadTask uploadTask = ref.putBytes(data, metadata);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            long total = taskSnapshot.getTotalByteCount();
            if (total > 0) {
                int percent = (int) (100.0 * taskSnapshot.getBytesTransferred() / total);
                callback.onProgress(percent);
            }
        });

        uploadTask.addOnFailureListener(e -> {
            // Fallback: If Firebase Storage has not been configured / requires billing,
            // seamlessly store avatar in local private app storage so the user experience is not broken.
            File localAvatar = saveToLocalFile(uid, data, mimeType);
            if (localAvatar != null) {
                callback.onProgress(100);
                callback.onSuccess(Uri.fromFile(localAvatar).toString());
            } else {
                callback.onError(AvatarUploadPolicy.ERROR_UPLOAD_FAILED + " (" + e.getMessage() + ")");
            }
        });

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        callback.onSuccess(uri.toString());
                    })
                    .addOnFailureListener(e -> {
                        File localAvatar = saveToLocalFile(uid, data, mimeType);
                        if (localAvatar != null) {
                            callback.onProgress(100);
                            callback.onSuccess(Uri.fromFile(localAvatar).toString());
                        } else {
                            callback.onError(AvatarUploadPolicy.ERROR_DOWNLOAD_URL_FAILED + " (" + e.getMessage() + ")");
                        }
                    });
        });
    }

    @Override
    public void updateAvatarUrl(String uid, String downloadUrl, UpdateCallback callback) {
        if (callback == null) return;

        if (!AvatarUploadPolicy.isValidAvatarUrl(downloadUrl)) {
            callback.onError("Đường dẫn avatar không hợp lệ hoặc vượt quá 2048 ký tự.");
            return;
        }

        if (firestore == null) {
            callback.onError("Firestore service không khả dụng.");
            return;
        }

        firestore.collection("users").document(uid)
                .update("avatarUrl", downloadUrl)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    callback.onError(AvatarUploadPolicy.ERROR_FIRESTORE_UPDATE_FAILED + " (" + e.getMessage() + ")");
                });
    }

    @Override
    public void updateProfile(String uid, String name, int age, String phone, UpdateCallback callback) {
        if (callback == null) return;
        String cleanName = name == null ? "" : name.trim();
        String cleanPhone = phone == null ? "" : phone.trim();
        if (uid == null || uid.trim().isEmpty() || cleanName.length() < 2 || cleanName.length() > 100
                || age < 18 || age > 60 || !cleanPhone.matches("^0[0-9]{9}$")) {
            callback.onError("Thông tin tài khoản không hợp lệ.");
            return;
        }
        if (firestore == null) {
            callback.onError("Firestore service không khả dụng.");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", cleanName);
        updates.put("age", age);
        updates.put("phone", cleanPhone);
        firestore.collection("users").document(uid).update(updates)
                .addOnSuccessListener(ignored -> callback.onSuccess())
                .addOnFailureListener(error -> callback.onError(
                        "Không thể cập nhật thông tin tài khoản: " + error.getMessage()));
    }

    @Override
    public ListenerHandle listenProfile(String uid, ProfileCallback callback) {
        if (callback == null || uid == null || uid.trim().isEmpty() || firestore == null) {
            return () -> {};
        }

        ListenerRegistration registration = firestore.collection("users")
                .document(uid)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        callback.onError("Lỗi khi tải hồ sơ: " + error.getMessage());
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        AppUser user = UserDocumentValidator.parseAppUser(uid, documentSnapshot.getData());
                        if (user != null) {
                            callback.onProfileLoaded(user);
                        } else {
                            callback.onError("Dữ liệu hồ sơ người dùng không hợp lệ.");
                        }
                    } else {
                        callback.onError("Không tìm thấy thông tin người dùng.");
                    }
                });

        return registration::remove;
    }
}
