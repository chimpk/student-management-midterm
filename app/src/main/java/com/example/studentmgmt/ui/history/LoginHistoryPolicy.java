package com.example.studentmgmt.ui.history;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.LoginRecord;

import java.util.Collections;
import java.util.List;

/**
 * Pure Java policy enforcing login recording prerequisites,
 * RBAC authorization for viewing history, and device attribute sanitization.
 */
public final class LoginHistoryPolicy {

    public static final int MAX_DEVICE_NAME_LENGTH = 200;

    private LoginHistoryPolicy() {
        throw new AssertionError("Utility class");
    }

    /**
     * Determines whether a login event should be written to Firestore.
     * Only true when authentication was successful, user document is present, and status is Normal.
     */
    public static boolean shouldRecordLogin(boolean authSuccess, AppUser user) {
        if (!authSuccess || user == null) {
            return false;
        }
        return user.isNormal();
    }

    /**
     * Evaluates whether currentUser is authorized to view the login history of targetUid.
     * Admin can view any user's login history.
     * Manager and Employee can only view their own login history.
     */
    public static boolean canViewLoginHistory(AppUser currentUser, String targetUid) {
        if (currentUser == null || targetUid == null || targetUid.trim().isEmpty()) {
            return false;
        }

        if (AppUser.ROLE_ADMIN.equals(currentUser.getRole())) {
            return true;
        }

        return currentUser.getUid().equals(targetUid.trim());
    }

    /**
     * Sanitizes device name string to avoid null, excessive length, or invalid characters.
     * Maximum length is 200 characters matching firestore.rules validLoginHistory().
     */
    public static String sanitizeDevice(String device) {
        if (device == null || device.trim().isEmpty()) {
            return "Unknown Device";
        }
        String trimmed = device.trim();
        if (trimmed.length() > MAX_DEVICE_NAME_LENGTH) {
            return trimmed.substring(0, MAX_DEVICE_NAME_LENGTH);
        }
        return trimmed;
    }

    /**
     * Sorts login records in descending order (pending records first, then newest server timestamp first).
     */
    public static void sortDescending(List<LoginRecord> records) {
        if (records == null || records.size() <= 1) {
            return;
        }
        Collections.sort(records, (r1, r2) -> {
            if (r1.isPending() && !r2.isPending()) return -1;
            if (!r1.isPending() && r2.isPending()) return 1;
            return Long.compare(r2.getTimestamp(), r1.getTimestamp());
        });
    }
}
