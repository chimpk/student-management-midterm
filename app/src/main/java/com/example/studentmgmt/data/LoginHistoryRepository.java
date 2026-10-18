package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.LoginRecord;

import java.util.List;

/**
 * Repository interface for writing and listening to user login history.
 * Subcollection: users/{uid}/loginHistory/{logId}
 */
public interface LoginHistoryRepository {

    interface ListenerHandle {
        void remove();
    }

    interface RecordCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    interface HistoryCallback {
        void onHistoryLoaded(List<LoginRecord> records);
        void onError(String errorMessage);
    }

    /**
     * Records a login event with server timestamp and device model.
     */
    void recordLogin(String uid, String device, RecordCallback callback);

    /**
     * Streams login history records in realtime (ordered newest first).
     * Enforces RBAC permissions.
     */
    ListenerHandle listenLoginHistory(AppUser currentUser, String targetUid, HistoryCallback callback);
}
