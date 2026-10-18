package com.example.studentmgmt.ui.user;

import com.example.studentmgmt.model.AppUser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Pure Java RBAC and field validation policy for User Management.
 * Enforces admin authority, self-lock/self-delete restrictions,
 * and input validation matching docs/SRS.md and firestore.rules.
 */
public final class UserManagementPolicy {

    private static final Set<String> ALLOWED_ROLES = new HashSet<>(Arrays.asList(
            AppUser.ROLE_ADMIN,
            AppUser.ROLE_MANAGER,
            AppUser.ROLE_EMPLOYEE
    ));

    private static final Set<String> ALLOWED_STATUSES = new HashSet<>(Arrays.asList(
            AppUser.STATUS_NORMAL,
            AppUser.STATUS_LOCKED
    ));

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@ ]+@[^@ ]+[.][^@ ]+$");

    private UserManagementPolicy() {
        throw new AssertionError("Utility class");
    }

    /**
     * Checks if current user has permission to view or manage users list.
     * Enforces exact case sensitivity: role must strictly equal 'admin' and status 'Normal'.
     */
    public static boolean canManageUsers(AppUser currentUser) {
        return currentUser != null
                && AppUser.ROLE_ADMIN.equals(currentUser.getRole())
                && AppUser.STATUS_NORMAL.equals(currentUser.getStatus());
    }

    /**
     * Checks if current user can lock or unlock a target user.
     * Denies self-lock to prevent administrator lockout.
     */
    public static boolean canLockUser(AppUser currentUser, String targetUid) {
        if (!canManageUsers(currentUser) || targetUid == null) {
            return false;
        }
        return !currentUser.getUid().equals(targetUid);
    }

    /**
     * Checks if current user can delete a target user document.
     * Denies self-delete to prevent administrator self-removal.
     */
    public static boolean canDeleteUser(AppUser currentUser, String targetUid) {
        if (!canManageUsers(currentUser) || targetUid == null) {
            return false;
        }
        return !currentUser.getUid().equals(targetUid);
    }

    /**
     * Validates user profile fields (name, age, phone, role).
     *
     * @return null if valid, error message otherwise.
     */
    public static String validateUserProfile(String name, int age, String phone, String role) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 100) {
            return "Họ tên phải từ 2 đến 100 ký tự.";
        }

        if (age < 18 || age > 60) {
            return "Tuổi phải từ 18 đến 60.";
        }

        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            return "Số điện thoại phải gồm 10 chữ số bắt đầu bằng 0.";
        }

        if (role == null || !ALLOWED_ROLES.contains(role)) {
            return "Vai trò không hợp lệ (admin, manager, hoặc employee).";
        }

        return null;
    }

    /**
     * Comprehensive validation for creating a new user profile at repository boundary.
     * Enforces caller authority, UID sanity, username pattern, profile fields,
     * status allowlist, and optional email format/length.
     *
     * @return null if valid, error message otherwise.
     */
    public static String validateNewUser(AppUser currentUser, AppUser newUser) {
        if (!canManageUsers(currentUser)) {
            return "Chỉ Quản trị viên (Admin) đang hoạt động mới có quyền tạo hồ sơ người dùng.";
        }

        if (newUser == null) {
            return "Dữ liệu người dùng mới rỗng.";
        }

        String uid = newUser.getUid();
        if (uid == null || uid.trim().isEmpty() || uid.length() > 128 || uid.contains("/") || uid.contains(" ")) {
            return "UID người dùng không hợp lệ (yêu cầu không rỗng, tối đa 128 ký tự, không chứa khoảng trắng hoặc ký tự phân cách).";
        }

        String username = newUser.getUsername();
        if (username == null || !com.example.studentmgmt.utils.ValidationUtils.isValidUsername(username)) {
            return "Username không hợp lệ (3-32 ký tự chữ và số, bắt đầu bằng chữ cái).";
        }

        String profileError = validateUserProfile(newUser.getName(), newUser.getAge(), newUser.getPhone(), newUser.getRole());
        if (profileError != null) {
            return profileError;
        }

        String status = newUser.getStatus();
        if (status == null || !ALLOWED_STATUSES.contains(status)) {
            return "Trạng thái người dùng không hợp lệ (yêu cầu chính xác 'Normal' hoặc 'Locked').";
        }

        String email = newUser.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            if (email.length() > 254 || !EMAIL_PATTERN.matcher(email).matches()) {
                return "Email không đúng định dạng chuẩn.";
            }
        }

        return null;
    }
}
