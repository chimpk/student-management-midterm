package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.utils.ValidationUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Pure Java production validator for Firestore user document data.
 * Enforces fail-closed validation according to docs/firestore-schema.md and firestore.rules.
 * Exact case sensitivity and strict type checks are enforced to match Firestore security rules.
 */
public final class UserDocumentValidator {

    private static final Set<String> ALLOWED_ROLES = new HashSet<>(Arrays.asList(
            AppUser.ROLE_ADMIN,
            AppUser.ROLE_MANAGER,
            AppUser.ROLE_EMPLOYEE
    ));

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@ ]+@[^@ ]+[.][^@ ]+$");

    private UserDocumentValidator() {
        throw new AssertionError("Utility class");
    }

    public enum Status {
        VALID,
        LOCKED,
        INVALID
    }

    public static final class Result {
        private final Status status;
        private final AppUser user;
        private final String errorMessage;

        private Result(Status status, AppUser user, String errorMessage) {
            this.status = status;
            this.user = user;
            this.errorMessage = errorMessage;
        }

        public static Result valid(AppUser user) {
            return new Result(Status.VALID, user, null);
        }

        public static Result locked(String message) {
            return new Result(Status.LOCKED, null, message);
        }

        public static Result invalid(String message) {
            return new Result(Status.INVALID, null, message);
        }

        public boolean isValid() {
            return status == Status.VALID;
        }

        public boolean isLocked() {
            return status == Status.LOCKED;
        }

        public Status getStatus() {
            return status;
        }

        public AppUser getUser() {
            return user;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Validates and parses a raw Firestore user document data map.
     * Enforces fail-closed rules: exact casing (Normal/Locked, admin/manager/employee)
     * and strict integer types for age matching Firestore security rules.
     */
    public static Result validate(String uid, Map<String, Object> data) {
        if (uid == null || uid.trim().isEmpty()) {
            return Result.invalid("Thiếu UID hợp lệ.");
        }

        if (data == null) {
            return Result.invalid("Dữ liệu user document rỗng.");
        }

        // 1. Status check: must be strictly exact case "Normal" or "Locked"
        Object rawStatus = data.get("status");
        if (!(rawStatus instanceof String)) {
            return Result.invalid("Trường status bắt buộc và phải là String.");
        }
        String status = (String) rawStatus;
        if ("Locked".equals(status)) {
            return Result.locked("Tài khoản đã bị khóa.");
        }
        if (!"Normal".equals(status)) {
            return Result.invalid("Giá trị status không hợp lệ (yêu cầu chính xác 'Normal'): " + status);
        }

        // 2. Role check: must be strictly lowercase "admin", "manager", or "employee"
        Object rawRole = data.get("role");
        if (!(rawRole instanceof String)) {
            return Result.invalid("Trường role bắt buộc và phải là String.");
        }
        String role = (String) rawRole;
        if (!ALLOWED_ROLES.contains(role)) {
            return Result.invalid("Giá trị role không được phép (yêu cầu đúng case 'admin'|'manager'|'employee'): " + role);
        }

        // 3. Username check: must match username pattern (3-32 chars, starts with letter)
        Object rawUsername = data.get("username");
        if (!(rawUsername instanceof String)) {
            return Result.invalid("Trường username bắt buộc và phải là String.");
        }
        String username = (String) rawUsername;
        if (!ValidationUtils.isValidUsername(username)) {
            return Result.invalid("Username không đúng định dạng quy định: " + username);
        }

        // 4. Name check: string, length 2..100
        Object rawName = data.get("name");
        if (!(rawName instanceof String)) {
            return Result.invalid("Trường name bắt buộc và phải là String.");
        }
        String name = ((String) rawName).trim();
        if (name.length() < 2 || name.length() > 100) {
            return Result.invalid("Độ dài name phải từ 2 đến 100 ký tự.");
        }

        // 5. Age check: must strictly be an integer (Integer or Long from Firestore), never Double/Float
        Object rawAge = data.get("age");
        if (!(rawAge instanceof Integer || rawAge instanceof Long)) {
            return Result.invalid("Trường age bắt buộc và phải là số nguyên (Integer hoặc Long), không chấp nhận số thực.");
        }
        long ageLong = ((Number) rawAge).longValue();
        if (ageLong < 18 || ageLong > 60) {
            return Result.invalid("Tuổi phải nằm trong khoảng 18 đến 60.");
        }
        int age = (int) ageLong;

        // 6. Phone check: 10 digits starting with 0
        Object rawPhone = data.get("phone");
        if (!(rawPhone instanceof String)) {
            return Result.invalid("Trường phone bắt buộc và phải là String.");
        }
        String phone = (String) rawPhone;
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return Result.invalid("Số điện thoại không hợp lệ (cần đúng 10 số bắt đầu bằng 0).");
        }

        // 7. Email check (optional): if present, must match email pattern
        String email = null;
        if (data.containsKey("email") && data.get("email") != null) {
            Object rawEmail = data.get("email");
            if (!(rawEmail instanceof String) || !EMAIL_PATTERN.matcher((String) rawEmail).matches()) {
                return Result.invalid("Email không đúng định dạng.");
            }
            email = (String) rawEmail;
        }

        // 8. AvatarUrl check (optional): length <= 2048
        String avatarUrl = null;
        if (data.containsKey("avatarUrl") && data.get("avatarUrl") != null) {
            Object rawAvatar = data.get("avatarUrl");
            if (rawAvatar instanceof String) {
                String avStr = (String) rawAvatar;
                if (avStr.length() <= 2048) {
                    avatarUrl = avStr;
                }
            }
        }

        AppUser user = new AppUser(
                uid,
                username,
                email,
                name,
                age,
                phone,
                AppUser.STATUS_NORMAL,
                role,
                avatarUrl
        );

        return Result.valid(user);
    }

    /**
     * Parses a Firestore user document into an AppUser object for administration display.
     * Unlike validate() which fails closed during authentication when status is Locked,
     * parseAppUser() preserves both "Normal" and "Locked" statuses so administrators
     * can view, manage, and unlock accounts.
     * Returns null if any required schema field is missing, invalid, or corrupt.
     */
    public static AppUser parseAppUser(String uid, Map<String, Object> data) {
        if (uid == null || uid.trim().isEmpty() || data == null) {
            return null;
        }

        Object rawStatus = data.get("status");
        if (!(rawStatus instanceof String)) {
            return null;
        }
        String status = (String) rawStatus;
        if (!AppUser.STATUS_NORMAL.equals(status) && !AppUser.STATUS_LOCKED.equals(status)) {
            return null;
        }

        Object rawRole = data.get("role");
        if (!(rawRole instanceof String) || !ALLOWED_ROLES.contains(rawRole)) {
            return null;
        }
        String role = (String) rawRole;

        Object rawUsername = data.get("username");
        if (!(rawUsername instanceof String) || !ValidationUtils.isValidUsername((String) rawUsername)) {
            return null;
        }
        String username = (String) rawUsername;

        Object rawName = data.get("name");
        if (!(rawName instanceof String)) {
            return null;
        }
        String name = ((String) rawName).trim();
        if (name.length() < 2 || name.length() > 100) {
            return null;
        }

        Object rawAge = data.get("age");
        if (!(rawAge instanceof Integer || rawAge instanceof Long)) {
            return null;
        }
        long ageLong = ((Number) rawAge).longValue();
        if (ageLong < 18 || ageLong > 60) {
            return null;
        }
        int age = (int) ageLong;

        Object rawPhone = data.get("phone");
        if (!(rawPhone instanceof String) || !PHONE_PATTERN.matcher((String) rawPhone).matches()) {
            return null;
        }
        String phone = (String) rawPhone;

        String email = null;
        if (data.containsKey("email") && data.get("email") != null) {
            Object rawEmail = data.get("email");
            if (rawEmail instanceof String && EMAIL_PATTERN.matcher((String) rawEmail).matches()) {
                email = (String) rawEmail;
            }
        }

        String avatarUrl = null;
        if (data.containsKey("avatarUrl") && data.get("avatarUrl") != null) {
            Object rawAvatar = data.get("avatarUrl");
            if (rawAvatar instanceof String && ((String) rawAvatar).length() <= 2048) {
                avatarUrl = (String) rawAvatar;
            }
        }

        return new AppUser(
                uid,
                username,
                email,
                name,
                age,
                phone,
                status,
                role,
                avatarUrl
        );
    }
}

