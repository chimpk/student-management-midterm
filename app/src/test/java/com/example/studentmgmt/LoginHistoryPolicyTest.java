package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.LoginRecord;
import com.example.studentmgmt.ui.history.LoginHistoryPolicy;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for LoginHistoryPolicy.
 * Verifies recording triggers, ownership and RBAC view permissions,
 * device string sanitization, and timestamp ordering.
 */
public class LoginHistoryPolicyTest {

    private final AppUser adminUser = new AppUser(
            "admin-uid", "admin", "admin@studentmgmt.edu.vn",
            "Admin User", 30, "0912345678",
            AppUser.STATUS_NORMAL, AppUser.ROLE_ADMIN, null
    );

    private final AppUser managerUser = new AppUser(
            "mgr-uid", "manager", "mgr@studentmgmt.edu.vn",
            "Manager User", 28, "0912345679",
            AppUser.STATUS_NORMAL, AppUser.ROLE_MANAGER, null
    );

    private final AppUser employeeUser = new AppUser(
            "emp-uid", "employee", "emp@studentmgmt.edu.vn",
            "Employee User", 22, "0912345670",
            AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null
    );

    private final AppUser lockedUser = new AppUser(
            "locked-uid", "locked", "locked@studentmgmt.edu.vn",
            "Locked User", 25, "0912345671",
            AppUser.STATUS_LOCKED, AppUser.ROLE_EMPLOYEE, null
    );

    @Test
    public void shouldRecordLogin_onlyWhenAuthSuccessAndUserNormal() {
        assertTrue(LoginHistoryPolicy.shouldRecordLogin(true, adminUser));
        assertTrue(LoginHistoryPolicy.shouldRecordLogin(true, managerUser));
        assertTrue(LoginHistoryPolicy.shouldRecordLogin(true, employeeUser));
    }

    @Test
    public void shouldRecordLogin_rejectsFailedAuth_nullUser_orLockedUser() {
        assertFalse(LoginHistoryPolicy.shouldRecordLogin(false, adminUser));
        assertFalse(LoginHistoryPolicy.shouldRecordLogin(true, null));
        assertFalse(LoginHistoryPolicy.shouldRecordLogin(false, null));
        assertFalse(LoginHistoryPolicy.shouldRecordLogin(true, lockedUser));
    }

    @Test
    public void canViewLoginHistory_adminCanViewAnyUser() {
        assertTrue(LoginHistoryPolicy.canViewLoginHistory(adminUser, "admin-uid"));
        assertTrue(LoginHistoryPolicy.canViewLoginHistory(adminUser, "mgr-uid"));
        assertTrue(LoginHistoryPolicy.canViewLoginHistory(adminUser, "emp-uid"));
        assertTrue(LoginHistoryPolicy.canViewLoginHistory(adminUser, "any-other-uid"));
    }

    @Test
    public void canViewLoginHistory_nonAdminCanOnlyViewSelf() {
        // Manager can view self, but cannot view other users
        assertTrue(LoginHistoryPolicy.canViewLoginHistory(managerUser, "mgr-uid"));
        assertFalse(LoginHistoryPolicy.canViewLoginHistory(managerUser, "admin-uid"));
        assertFalse(LoginHistoryPolicy.canViewLoginHistory(managerUser, "emp-uid"));

        // Employee can view self, but cannot view other users
        assertTrue(LoginHistoryPolicy.canViewLoginHistory(employeeUser, "emp-uid"));
        assertFalse(LoginHistoryPolicy.canViewLoginHistory(employeeUser, "admin-uid"));
        assertFalse(LoginHistoryPolicy.canViewLoginHistory(employeeUser, "mgr-uid"));
    }

    @Test
    public void canViewLoginHistory_rejectsNullUserOrTarget() {
        assertFalse(LoginHistoryPolicy.canViewLoginHistory(null, "emp-uid"));
        assertFalse(LoginHistoryPolicy.canViewLoginHistory(adminUser, null));
        assertFalse(LoginHistoryPolicy.canViewLoginHistory(adminUser, ""));
    }

    @Test
    public void sanitizeDevice_limitsLengthAndHandlesNull() {
        assertEquals("Unknown Device", LoginHistoryPolicy.sanitizeDevice(null));
        assertEquals("Unknown Device", LoginHistoryPolicy.sanitizeDevice("   "));
        assertEquals("Pixel 7 Pro", LoginHistoryPolicy.sanitizeDevice("  Pixel 7 Pro  "));

        StringBuilder longDevice = new StringBuilder();
        for (int i = 0; i < 250; i++) {
            longDevice.append('A');
        }
        String sanitized = LoginHistoryPolicy.sanitizeDevice(longDevice.toString());
        assertEquals(200, sanitized.length());
    }

    @Test
    public void sortRecords_ordersDescendingByTimestamp() {
        List<LoginRecord> records = new ArrayList<>();
        records.add(new LoginRecord("1", 1000L, "Device A"));
        records.add(new LoginRecord("2", 3000L, "Device B"));
        records.add(new LoginRecord("3", 2000L, "Device C"));

        LoginHistoryPolicy.sortDescending(records);

        assertEquals("2", records.get(0).getId());
        assertEquals("3", records.get(1).getId());
        assertEquals("1", records.get(2).getId());
    }

    @Test
    public void pendingRecords_arePlacedFirstWithoutFakingClientTime() {
        LoginRecord pending = new LoginRecord("pending-1", 0L, "Pixel 8", true);
        assertTrue(pending.isPending());
        assertEquals(0L, pending.getTimestamp());
        assertEquals("Đang đồng bộ (chờ máy chủ)...", pending.getFormattedDateTime());

        LoginRecord oldCommitted = new LoginRecord("c-old", 1000L, "Device Old", false);
        LoginRecord recentCommitted = new LoginRecord("c-recent", 5000L, "Device Recent", false);

        List<LoginRecord> records = new ArrayList<>();
        records.add(oldCommitted);
        records.add(pending);
        records.add(recentCommitted);

        LoginHistoryPolicy.sortDescending(records);

        // Pending record must come first; committed records must follow newest server timestamp first
        assertEquals("pending-1", records.get(0).getId());
        assertEquals("c-recent", records.get(1).getId());
        assertEquals("c-old", records.get(2).getId());
    }
}
