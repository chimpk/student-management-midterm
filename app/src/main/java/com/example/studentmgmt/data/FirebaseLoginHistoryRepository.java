package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.LoginRecord;
import com.example.studentmgmt.ui.history.LoginHistoryPolicy;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of LoginHistoryRepository backed by Cloud Firestore subcollections.
 */
public class FirebaseLoginHistoryRepository implements LoginHistoryRepository {

    private final FirebaseFirestore firestore;

    public FirebaseLoginHistoryRepository() {
        FirebaseFirestore fs = null;
        try {
            fs = FirebaseFirestore.getInstance();
        } catch (Exception ignored) {
        }
        this.firestore = fs;
    }

    public FirebaseLoginHistoryRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void recordLogin(String uid, String device, RecordCallback callback) {
        if (uid == null || uid.trim().isEmpty()) {
            if (callback != null) {
                callback.onError("UID không hợp lệ.");
            }
            return;
        }

        if (firestore == null) {
            if (callback != null) {
                callback.onError("Firestore service không khả dụng.");
            }
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", FieldValue.serverTimestamp());
        data.put("device", LoginHistoryPolicy.sanitizeDevice(device));

        firestore.collection("users").document(uid.trim())
                .collection("loginHistory")
                .add(data)
                .addOnSuccessListener(docRef -> {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onError("Lỗi khi ghi lịch sử đăng nhập: " + e.getMessage());
                    }
                });
    }

    @Override
    public ListenerHandle listenLoginHistory(AppUser currentUser, String targetUid, HistoryCallback callback) {
        if (callback == null) {
            return () -> {};
        }

        if (!LoginHistoryPolicy.canViewLoginHistory(currentUser, targetUid)) {
            callback.onError("Bạn không có quyền xem lịch sử đăng nhập của người dùng này.");
            return () -> {};
        }

        if (firestore == null) {
            callback.onError("Firestore service không khả dụng.");
            return () -> {};
        }

        ListenerRegistration registration = firestore.collection("users")
                .document(targetUid.trim())
                .collection("loginHistory")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        callback.onError("Lỗi khi tải lịch sử đăng nhập: " + error.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        List<LoginRecord> records = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Timestamp ts = doc.getTimestamp("timestamp");
                            long epoch = 0L;
                            boolean isPending = true;
                            if (ts != null) {
                                epoch = ts.toDate().getTime();
                                isPending = false;
                            }
                            String device = doc.getString("device");
                            records.add(new LoginRecord(doc.getId(), epoch, device, isPending));
                        }
                        LoginHistoryPolicy.sortDescending(records);
                        callback.onHistoryLoaded(records);
                    }
                });

        return registration::remove;
    }
}
