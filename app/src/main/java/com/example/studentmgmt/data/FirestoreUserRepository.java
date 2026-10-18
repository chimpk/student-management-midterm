package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.navigation.SessionSecurityPolicy;
import com.example.studentmgmt.ui.user.UserManagementPolicy;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Production Firebase Firestore implementation of UserRepository.
 * Enforces fail-closed RBAC: only Admin can list/create/update/delete/lock users.
 * Enforces self-lock and self-delete protections.
 * Enforces create-only transactions to avoid overwriting existing user profiles.
 */
public class FirestoreUserRepository implements UserRepository {

    private final FirebaseFirestore firestore;

    public FirestoreUserRepository() {
        this(FirebaseFirestore.getInstance());
    }

    public FirestoreUserRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public ListenerHandle listenUsers(AppUser currentUser, UserListCallback callback) {
        if (callback == null) {
            return () -> {};
        }

        if (!UserManagementPolicy.canManageUsers(currentUser)) {
            callback.onError("Không có quyền truy cập quản lý người dùng (chỉ dành cho Quản trị viên).");
            return () -> {};
        }

        if (firestore == null) {
            callback.onError("Firestore service không khả dụng.");
            return () -> {};
        }

        ListenerRegistration registration = firestore.collection("users")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        callback.onError("Lỗi khi tải danh sách người dùng: " + error.getMessage());
                        return;
                    }
                    if (querySnapshot != null) {
                        List<AppUser> users = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            AppUser user = UserDocumentValidator.parseAppUser(doc.getId(), doc.getData());
                            if (user != null) {
                                users.add(user);
                            }
                        }
                        callback.onUsersLoaded(users);
                    }
                });

        return registration::remove;
    }

    @Override
    public ListenerHandle listenUser(String uid, UserCallback callback) {
        if (callback == null || uid == null || uid.trim().isEmpty() || firestore == null) {
            return () -> {};
        }

        ListenerRegistration registration = firestore.collection("users")
                .document(uid)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        FirebaseFirestoreException.Code code = error.getCode();
                        if (code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            callback.onError(SessionSecurityPolicy.SessionErrorType.PERMISSION_DENIED,
                                    "Quyền truy cập đã bị thu hồi hoặc tài khoản đã bị khóa: " + error.getMessage());
                        } else if (code == FirebaseFirestoreException.Code.UNAUTHENTICATED) {
                            callback.onError(SessionSecurityPolicy.SessionErrorType.UNAUTHENTICATED,
                                    "Phiên xác thực không hợp lệ hoặc đã hết hạn: " + error.getMessage());
                        } else if (code == FirebaseFirestoreException.Code.UNAVAILABLE
                                || code == FirebaseFirestoreException.Code.DEADLINE_EXCEEDED) {
                            callback.onError(SessionSecurityPolicy.SessionErrorType.NETWORK_TEMPORARY,
                                    "Lỗi kết nối mạng tạm thời: " + error.getMessage());
                        } else {
                            callback.onError(SessionSecurityPolicy.SessionErrorType.UNKNOWN,
                                    error.getMessage());
                        }
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        AppUser user = UserDocumentValidator.parseAppUser(uid, documentSnapshot.getData());
                        if (user != null) {
                            callback.onUserLoaded(user);
                        } else {
                            callback.onError(SessionSecurityPolicy.SessionErrorType.INVALID_DOCUMENT,
                                    "Dữ liệu người dùng không hợp lệ hoặc thiếu trường bắt buộc.");
                        }
                    } else {
                        callback.onError(SessionSecurityPolicy.SessionErrorType.USER_NOT_FOUND,
                                "Tài khoản người dùng không tồn tại.");
                    }
                });

        return registration::remove;
    }

    @Override
    public void createUserProfile(AppUser currentUser, AppUser newUser, OperationCallback callback) {
        if (callback == null) return;

        // Comprehensive boundary validation
        String validationError = UserManagementPolicy.validateNewUser(currentUser, newUser);
        if (validationError != null) {
            callback.onError(validationError);
            return;
        }

        if (firestore == null) {
            callback.onError("Firestore service không khả dụng.");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("uid", newUser.getUid());
        data.put("username", newUser.getUsername());
        data.put("name", newUser.getName());
        data.put("age", (long) newUser.getAge());
        data.put("phone", newUser.getPhone());
        data.put("role", newUser.getRole());
        data.put("status", newUser.getStatus());
        data.put("createdAt", FieldValue.serverTimestamp());
        if (newUser.getEmail() != null && !newUser.getEmail().trim().isEmpty()) {
            data.put("email", newUser.getEmail().trim());
        }

        DocumentReference docRef = firestore.collection("users").document(newUser.getUid());

        // Precondition create-only transaction: strictly prevents overwriting existing profiles
        firestore.runTransaction(transaction -> {
            DocumentSnapshot existingDoc = transaction.get(docRef);
            if (existingDoc.exists()) {
                throw new FirebaseFirestoreException(
                        "Người dùng với UID này đã tồn tại trên Firestore (trùng UID).",
                        FirebaseFirestoreException.Code.ALREADY_EXISTS
                );
            }
            transaction.set(docRef, data);
            return null;
        }).addOnSuccessListener(aVoid -> callback.onSuccess())
          .addOnFailureListener(e -> {
              if (e instanceof FirebaseFirestoreException
                      && ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.ALREADY_EXISTS) {
                  callback.onError("Người dùng với UID này đã tồn tại trên Firestore (trùng UID).");
              } else {
                  callback.onError("Lỗi khi tạo hồ sơ người dùng: " + e.getMessage());
              }
          });
    }

    @Override
    public void updateUserProfile(AppUser currentUser, String uid, String name, int age, String phone, String role, OperationCallback callback) {
        if (callback == null) return;

        if (!UserManagementPolicy.canManageUsers(currentUser)) {
            callback.onError("Không có quyền cập nhật người dùng.");
            return;
        }

        if (uid == null || uid.trim().isEmpty()) {
            callback.onError("UID không hợp lệ.");
            return;
        }

        String validationError = UserManagementPolicy.validateUserProfile(name, age, phone, role);
        if (validationError != null) {
            callback.onError(validationError);
            return;
        }

        if (firestore == null) {
            callback.onError("Firestore service không khả dụng.");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name.trim());
        updates.put("age", (long) age);
        updates.put("phone", phone.trim());
        updates.put("role", role);

        firestore.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Lỗi khi cập nhật người dùng: " + e.getMessage()));
    }

    @Override
    public void setUserLocked(AppUser currentUser, String uid, boolean locked, OperationCallback callback) {
        if (callback == null) return;

        if (!UserManagementPolicy.canLockUser(currentUser, uid)) {
            callback.onError("Không thể tự khóa tài khoản của chính mình hoặc không có quyền Admin.");
            return;
        }

        if (firestore == null) {
            callback.onError("Firestore service không khả dụng.");
            return;
        }

        String newStatus = locked ? AppUser.STATUS_LOCKED : AppUser.STATUS_NORMAL;

        firestore.collection("users").document(uid)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Lỗi khi thay đổi trạng thái tài khoản: " + e.getMessage()));
    }

    @Override
    public void deleteUserProfile(AppUser currentUser, String uid, OperationCallback callback) {
        if (callback == null) return;

        if (!UserManagementPolicy.canDeleteUser(currentUser, uid)) {
            callback.onError("Không thể tự xóa tài khoản của chính mình hoặc không có quyền Admin.");
            return;
        }

        if (firestore == null) {
            callback.onError("Firestore service không khả dụng.");
            return;
        }

        firestore.collection("users").document(uid)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Lỗi khi xóa hồ sơ người dùng: " + e.getMessage()));
    }
}
