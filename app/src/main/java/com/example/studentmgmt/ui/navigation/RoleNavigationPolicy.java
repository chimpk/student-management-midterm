package com.example.studentmgmt.ui.navigation;

import com.example.studentmgmt.model.AppUser;

/**
 * Pure Java navigation policy controlling role-based access to destinations and menu items.
 * Enforces docs/rbac-matrix.md requirements:
 * - Admin can access all destinations including User Management.
 * - Manager and Employee cannot access User Management.
 * - Session must be non-null, unlocked, with a valid role.
 */
public final class RoleNavigationPolicy {

    public static final int DEST_STUDENT_LIST = 1001;
    public static final int DEST_USER_MANAGEMENT = 1002;
    public static final int DEST_PROFILE = 1003;

    public static final int MENU_STUDENTS = 2001;
    public static final int MENU_USERS = 2002;
    public static final int MENU_PROFILE = 2003;
    public static final int MENU_LOGOUT = 2004;

    private RoleNavigationPolicy() {
        throw new AssertionError("Utility class");
    }

    /**
     * Checks if a role is authorized to open a specific destination.
     */
    public static boolean canAccess(String role, int destinationId) {
        if (role == null) {
            return false;
        }

        if (destinationId == DEST_USER_MANAGEMENT) {
            return AppUser.ROLE_ADMIN.equals(role);
        }

        if (destinationId == DEST_STUDENT_LIST || destinationId == DEST_PROFILE) {
            return AppUser.ROLE_ADMIN.equals(role)
                    || AppUser.ROLE_MANAGER.equals(role)
                    || AppUser.ROLE_EMPLOYEE.equals(role);
        }

        return false;
    }

    /**
     * Resolves the safe destination ID if direct navigation is attempted.
     * If user is unauthorized to access requested destination, redirects to fallback (student list).
     */
    public static int resolveSafeDestination(String role, int requestedDestinationId) {
        if (canAccess(role, requestedDestinationId)) {
            return requestedDestinationId;
        }
        return DEST_STUDENT_LIST;
    }

    /**
     * Checks if a menu item should be visible for a given role.
     */
    public static boolean shouldShowMenuItem(String role, int menuItemId) {
        if (role == null) {
            return false;
        }

        if (menuItemId == MENU_USERS) {
            return AppUser.ROLE_ADMIN.equals(role);
        }

        if (menuItemId == MENU_STUDENTS || menuItemId == MENU_PROFILE || menuItemId == MENU_LOGOUT) {
            return AppUser.ROLE_ADMIN.equals(role)
                    || AppUser.ROLE_MANAGER.equals(role)
                    || AppUser.ROLE_EMPLOYEE.equals(role);
        }

        return false;
    }

    /**
     * Validates whether an authenticated session user is valid to use MainActivity.
     */
    public static boolean isSessionValid(AppUser user) {
        if (user == null) {
            return false;
        }

        if (user.isLocked()) {
            return false;
        }

        String role = user.getRole();
        return AppUser.ROLE_ADMIN.equals(role)
                || AppUser.ROLE_MANAGER.equals(role)
                || AppUser.ROLE_EMPLOYEE.equals(role);
    }
}
