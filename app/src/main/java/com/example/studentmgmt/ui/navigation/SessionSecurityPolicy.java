package com.example.studentmgmt.ui.navigation;

import com.example.studentmgmt.model.AppUser;

/**
 * Pure Java decision policy for real-time user session continuity.
 * Evaluates whether an active user session must be maintained or terminated.
 * Fail-closed on:
 * - Observed user becoming Locked, missing status, or unknown role.
 * - Terminal Firestore errors: PERMISSION_DENIED (rules revoked read on Locked/deleted doc),
 *   USER_NOT_FOUND (document deleted), or INVALID_DOCUMENT (corrupted schema).
 * Transient network errors do NOT terminate the session.
 */
public final class SessionSecurityPolicy {

    public enum SessionErrorType {
        UNAUTHENTICATED,
        PERMISSION_DENIED,
        USER_NOT_FOUND,
        INVALID_DOCUMENT,
        NETWORK_TEMPORARY,
        UNKNOWN
    }

    public enum Decision {
        MAINTAIN,
        TERMINATE
    }

    public static final class Evaluation {
        private final Decision decision;
        private final String reason;

        public Evaluation(Decision decision, String reason) {
            this.decision = decision;
            this.reason = reason;
        }

        public Decision getDecision() {
            return decision;
        }

        public boolean shouldTerminate() {
            return decision == Decision.TERMINATE;
        }

        public String getReason() {
            return reason;
        }
    }

    private SessionSecurityPolicy() {
        throw new AssertionError("Utility class");
    }

    public static Evaluation evaluateUserSnapshot(AppUser currentUser, AppUser observedUser) {
        if (currentUser == null) {
            return new Evaluation(Decision.TERMINATE, "Phiên đăng nhập không tồn tại.");
        }

        if (observedUser == null) {
            return new Evaluation(Decision.TERMINATE, "Hồ sơ người dùng không tồn tại hoặc đã bị xóa.");
        }

        if (observedUser.isLocked() || !observedUser.isNormal()) {
            return new Evaluation(Decision.TERMINATE, "Tài khoản của bạn đã bị khóa bởi Quản trị viên.");
        }

        if (!RoleNavigationPolicy.isSessionValid(observedUser)) {
            return new Evaluation(Decision.TERMINATE, "Thông tin vai trò người dùng không hợp lệ hoặc đã bị thay đổi.");
        }

        return new Evaluation(Decision.MAINTAIN, null);
    }

    public static Evaluation evaluateError(SessionErrorType errorType) {
        if (errorType == null) {
            return new Evaluation(Decision.MAINTAIN, null);
        }

        switch (errorType) {
            case UNAUTHENTICATED:
                return new Evaluation(Decision.TERMINATE, "Phiên xác thực đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.");
            case PERMISSION_DENIED:
                // When status changes to Locked or document is deleted, Firestore rules deny read access.
                return new Evaluation(Decision.TERMINATE, "Quyền truy cập đã bị thu hồi hoặc tài khoản đã bị khóa.");
            case USER_NOT_FOUND:
                return new Evaluation(Decision.TERMINATE, "Tài khoản người dùng không còn tồn tại trên hệ thống.");
            case INVALID_DOCUMENT:
                return new Evaluation(Decision.TERMINATE, "Dữ liệu tài khoản trên hệ thống không hợp lệ.");
            case NETWORK_TEMPORARY:
                // Transient network issues must not terminate the session!
                return new Evaluation(Decision.MAINTAIN, null);
            case UNKNOWN:
            default:
                return new Evaluation(Decision.MAINTAIN, null);
        }
    }
}
