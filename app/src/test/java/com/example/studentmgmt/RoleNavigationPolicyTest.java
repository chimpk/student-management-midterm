package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.navigation.RoleNavigationPolicy;

import org.junit.Test;

public class RoleNavigationPolicyTest {

    @Test
    public void adminRole_hasAccessToAllDestinations() {
        assertTrue(RoleNavigationPolicy.canAccess(AppUser.ROLE_ADMIN, RoleNavigationPolicy.DEST_USER_MANAGEMENT));
        assertTrue(RoleNavigationPolicy.canAccess(AppUser.ROLE_ADMIN, RoleNavigationPolicy.DEST_STUDENT_LIST));
        assertTrue(RoleNavigationPolicy.canAccess(AppUser.ROLE_ADMIN, RoleNavigationPolicy.DEST_PROFILE));
    }

    @Test
    public void managerRole_cannotAccessUserManagement() {
        assertFalse("Manager must not access User Management",
                RoleNavigationPolicy.canAccess(AppUser.ROLE_MANAGER, RoleNavigationPolicy.DEST_USER_MANAGEMENT));
        assertTrue(RoleNavigationPolicy.canAccess(AppUser.ROLE_MANAGER, RoleNavigationPolicy.DEST_STUDENT_LIST));
        assertTrue(RoleNavigationPolicy.canAccess(AppUser.ROLE_MANAGER, RoleNavigationPolicy.DEST_PROFILE));
    }

    @Test
    public void employeeRole_cannotAccessUserManagement() {
        assertFalse("Employee must not access User Management",
                RoleNavigationPolicy.canAccess(AppUser.ROLE_EMPLOYEE, RoleNavigationPolicy.DEST_USER_MANAGEMENT));
        assertTrue(RoleNavigationPolicy.canAccess(AppUser.ROLE_EMPLOYEE, RoleNavigationPolicy.DEST_STUDENT_LIST));
        assertTrue(RoleNavigationPolicy.canAccess(AppUser.ROLE_EMPLOYEE, RoleNavigationPolicy.DEST_PROFILE));
    }

    @Test
    public void nullOrUnknownRole_deniedAllDestinations() {
        assertFalse(RoleNavigationPolicy.canAccess(null, RoleNavigationPolicy.DEST_USER_MANAGEMENT));
        assertFalse(RoleNavigationPolicy.canAccess("guest", RoleNavigationPolicy.DEST_STUDENT_LIST));
        assertFalse(RoleNavigationPolicy.canAccess("superadmin", RoleNavigationPolicy.DEST_PROFILE));
    }

    @Test
    public void directNavigationGuard_redirectsUnauthorizedDestinations() {
        // Admin gets requested destination
        assertEquals(RoleNavigationPolicy.DEST_USER_MANAGEMENT,
                RoleNavigationPolicy.resolveSafeDestination(AppUser.ROLE_ADMIN, RoleNavigationPolicy.DEST_USER_MANAGEMENT));

        // Manager / Employee redirected to Student List fallback
        assertEquals(RoleNavigationPolicy.DEST_STUDENT_LIST,
                RoleNavigationPolicy.resolveSafeDestination(AppUser.ROLE_MANAGER, RoleNavigationPolicy.DEST_USER_MANAGEMENT));
        assertEquals(RoleNavigationPolicy.DEST_STUDENT_LIST,
                RoleNavigationPolicy.resolveSafeDestination(AppUser.ROLE_EMPLOYEE, RoleNavigationPolicy.DEST_USER_MANAGEMENT));
        assertEquals(RoleNavigationPolicy.DEST_STUDENT_LIST,
                RoleNavigationPolicy.resolveSafeDestination("fake_role", RoleNavigationPolicy.DEST_USER_MANAGEMENT));
    }

    @Test
    public void menuVisibility_onlyAdminSeesUserManagementMenu() {
        assertTrue(RoleNavigationPolicy.shouldShowMenuItem(AppUser.ROLE_ADMIN, RoleNavigationPolicy.MENU_USERS));
        assertFalse(RoleNavigationPolicy.shouldShowMenuItem(AppUser.ROLE_MANAGER, RoleNavigationPolicy.MENU_USERS));
        assertFalse(RoleNavigationPolicy.shouldShowMenuItem(AppUser.ROLE_EMPLOYEE, RoleNavigationPolicy.MENU_USERS));
        assertFalse(RoleNavigationPolicy.shouldShowMenuItem(null, RoleNavigationPolicy.MENU_USERS));

        // Common menus are visible to all valid roles
        assertTrue(RoleNavigationPolicy.shouldShowMenuItem(AppUser.ROLE_ADMIN, RoleNavigationPolicy.MENU_STUDENTS));
        assertTrue(RoleNavigationPolicy.shouldShowMenuItem(AppUser.ROLE_MANAGER, RoleNavigationPolicy.MENU_STUDENTS));
        assertTrue(RoleNavigationPolicy.shouldShowMenuItem(AppUser.ROLE_EMPLOYEE, RoleNavigationPolicy.MENU_STUDENTS));
    }

    @Test
    public void sessionValidation_acceptsOnlyActiveRolesAndNonLockedUsers() {
        AppUser admin = new AppUser("1", "admin", null, "Admin User", 30, "0912345678", AppUser.STATUS_NORMAL, AppUser.ROLE_ADMIN, null);
        AppUser manager = new AppUser("2", "mgr", null, "Manager User", 28, "0912345678", AppUser.STATUS_NORMAL, AppUser.ROLE_MANAGER, null);
        AppUser employee = new AppUser("3", "emp", null, "Emp User", 22, "0912345678", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null);
        AppUser lockedUser = new AppUser("4", "lock", null, "Locked User", 25, "0912345678", AppUser.STATUS_LOCKED, AppUser.ROLE_ADMIN, null);
        AppUser unknownRoleUser = new AppUser("5", "usr", null, "User", 25, "0912345678", AppUser.STATUS_NORMAL, "guest", null);

        assertTrue(RoleNavigationPolicy.isSessionValid(admin));
        assertTrue(RoleNavigationPolicy.isSessionValid(manager));
        assertTrue(RoleNavigationPolicy.isSessionValid(employee));

        assertFalse("Locked user must be rejected", RoleNavigationPolicy.isSessionValid(lockedUser));
        assertFalse("Unknown role must be rejected", RoleNavigationPolicy.isSessionValid(unknownRoleUser));
        assertFalse("Null user must be rejected", RoleNavigationPolicy.isSessionValid(null));
    }
}
