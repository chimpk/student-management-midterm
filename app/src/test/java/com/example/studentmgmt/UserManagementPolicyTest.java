package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.user.UserManagementPolicy;

import org.junit.Before;
import org.junit.Test;

/**
 * Pure Java unit tests for UserManagementPolicy.
 * Enforces RBAC: only Admin can manage users, Manager and Employee denied.
 * Enforces self-lock and self-delete protection.
 * Enforces field validations according to SRS.
 */
public class UserManagementPolicyTest {

    private AppUser adminUser;
    private AppUser managerUser;
    private AppUser employeeUser;
    private AppUser lockedAdminUser;

    @Before
    public void setUp() {
        adminUser = new AppUser(
                "admin-uid-1",
                "admin_super",
                "admin@tdtu.edu.vn",
                "Nguyen Van Admin",
                30,
                "0912345678",
                AppUser.STATUS_NORMAL,
                AppUser.ROLE_ADMIN,
                null
        );

        managerUser = new AppUser(
                "mgr-uid-1",
                "manager_1",
                "mgr@tdtu.edu.vn",
                "Tran Thi Manager",
                28,
                "0987654321",
                AppUser.STATUS_NORMAL,
                AppUser.ROLE_MANAGER,
                null
        );

        employeeUser = new AppUser(
                "emp-uid-1",
                "employee_1",
                "emp@tdtu.edu.vn",
                "Le Van Employee",
                24,
                "0901234567",
                AppUser.STATUS_NORMAL,
                AppUser.ROLE_EMPLOYEE,
                null
        );

        lockedAdminUser = new AppUser(
                "admin-locked-uid",
                "admin_locked",
                "locked@tdtu.edu.vn",
                "Nguyen Locked Admin",
                35,
                "0911223344",
                AppUser.STATUS_LOCKED,
                AppUser.ROLE_ADMIN,
                null
        );
    }

    @Test
    public void canManageUsers_onlyActiveAdminAllowed() {
        assertTrue("Active admin must be allowed to manage users",
                UserManagementPolicy.canManageUsers(adminUser));

        assertFalse("Locked admin must be denied",
                UserManagementPolicy.canManageUsers(lockedAdminUser));

        assertFalse("Manager must be denied user management",
                UserManagementPolicy.canManageUsers(managerUser));

        assertFalse("Employee must be denied user management",
                UserManagementPolicy.canManageUsers(employeeUser));

        assertFalse("Null user must be denied",
                UserManagementPolicy.canManageUsers(null));
    }

    @Test
    public void canLockUser_preventsSelfLock() {
        assertTrue("Admin can lock another user",
                UserManagementPolicy.canLockUser(adminUser, "emp-uid-1"));

        assertFalse("Admin cannot lock self",
                UserManagementPolicy.canLockUser(adminUser, "admin-uid-1"));

        assertFalse("Manager cannot lock any user",
                UserManagementPolicy.canLockUser(managerUser, "emp-uid-1"));
    }

    @Test
    public void canDeleteUser_preventsSelfDelete() {
        assertTrue("Admin can delete another user",
                UserManagementPolicy.canDeleteUser(adminUser, "emp-uid-1"));

        assertFalse("Admin cannot delete self",
                UserManagementPolicy.canDeleteUser(adminUser, "admin-uid-1"));

        assertFalse("Employee cannot delete user",
                UserManagementPolicy.canDeleteUser(employeeUser, "mgr-uid-1"));
    }

    @Test
    public void validateUserProfile_validInputPasses() {
        assertNull(UserManagementPolicy.validateUserProfile(
                "Nguyen Van A",
                25,
                "0912345678",
                AppUser.ROLE_EMPLOYEE
        ));
    }

    @Test
    public void validateUserProfile_invalidNameRejected() {
        assertNotNull("Name too short",
                UserManagementPolicy.validateUserProfile("A", 25, "0912345678", AppUser.ROLE_EMPLOYEE));
        assertNotNull("Name null",
                UserManagementPolicy.validateUserProfile(null, 25, "0912345678", AppUser.ROLE_EMPLOYEE));
    }

    @Test
    public void validateUserProfile_invalidAgeRejected() {
        assertNotNull("Age < 18",
                UserManagementPolicy.validateUserProfile("Nguyen Van A", 17, "0912345678", AppUser.ROLE_EMPLOYEE));
        assertNotNull("Age > 60",
                UserManagementPolicy.validateUserProfile("Nguyen Van A", 61, "0912345678", AppUser.ROLE_EMPLOYEE));
    }

    @Test
    public void validateUserProfile_invalidPhoneRejected() {
        assertNotNull("Phone not 10 digits",
                UserManagementPolicy.validateUserProfile("Nguyen Van A", 25, "091234567", AppUser.ROLE_EMPLOYEE));
        assertNotNull("Phone not starting with 0",
                UserManagementPolicy.validateUserProfile("Nguyen Van A", 25, "1912345678", AppUser.ROLE_EMPLOYEE));
        assertNotNull("Phone containing letters",
                UserManagementPolicy.validateUserProfile("Nguyen Van A", 25, "091234567a", AppUser.ROLE_EMPLOYEE));
    }

    @Test
    public void validateUserProfile_invalidRoleRejected() {
        assertNotNull("Invalid role",
                UserManagementPolicy.validateUserProfile("Nguyen Van A", 25, "0912345678", "superuser"));
        assertNotNull("Role uppercase rejected",
                UserManagementPolicy.validateUserProfile("Nguyen Van A", 25, "0912345678", "ADMIN"));
    }

    @Test
    public void validateNewUser_validNewUserPasses() {
        AppUser validNewUser = new AppUser(
                "auth-uid-12345",
                "employee_new",
                "new_emp@tdtu.edu.vn",
                "Nguyen Van Moi",
                25,
                "0912345678",
                AppUser.STATUS_NORMAL,
                AppUser.ROLE_EMPLOYEE,
                null
        );
        assertNull(UserManagementPolicy.validateNewUser(adminUser, validNewUser));

        // Creating with status Locked is also valid per SRS
        AppUser validLockedNewUser = new AppUser(
                "auth-uid-12345",
                "employee_locked",
                null,
                "Nguyen Van Khoa",
                25,
                "0912345678",
                AppUser.STATUS_LOCKED,
                AppUser.ROLE_EMPLOYEE,
                null
        );
        assertNull(UserManagementPolicy.validateNewUser(adminUser, validLockedNewUser));
    }

    @Test
    public void validateNewUser_nonAdminCallerRejected() {
        AppUser validNewUser = new AppUser(
                "auth-uid-12345", "employee_new", null, "Nguyen Van Moi",
                25, "0912345678", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null
        );
        assertNotNull("Manager cannot create users",
                UserManagementPolicy.validateNewUser(managerUser, validNewUser));
        assertNotNull("Employee cannot create users",
                UserManagementPolicy.validateNewUser(employeeUser, validNewUser));
        assertNotNull("Null caller cannot create users",
                UserManagementPolicy.validateNewUser(null, validNewUser));
    }

    @Test
    public void validateNewUser_invalidUidRejected() {
        AppUser nullUid = new AppUser(
                "   ", "employee_new", null, "Nguyen Van Moi",
                25, "0912345678", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null
        );
        assertNotNull("Blank UID rejected", UserManagementPolicy.validateNewUser(adminUser, nullUid));

        AppUser slashUid = new AppUser(
                "uid/with/slash", "employee_new", null, "Nguyen Van Moi",
                25, "0912345678", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null
        );
        assertNotNull("UID with slash rejected", UserManagementPolicy.validateNewUser(adminUser, slashUid));
    }

    @Test
    public void validateNewUser_invalidUsernameRejected() {
        AppUser invalidUsername = new AppUser(
                "auth-uid-12345", "123_invalid", null, "Nguyen Van Moi",
                25, "0912345678", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null
        );
        assertNotNull("Username starting with digit rejected",
                UserManagementPolicy.validateNewUser(adminUser, invalidUsername));
    }

    @Test
    public void validateNewUser_invalidStatusRejected() {
        AppUser invalidStatus = new AppUser(
                "auth-uid-12345", "employee_new", null, "Nguyen Van Moi",
                25, "0912345678", "normal", AppUser.ROLE_EMPLOYEE, null
        );
        assertNotNull("Lowercase status rejected",
                UserManagementPolicy.validateNewUser(adminUser, invalidStatus));

        AppUser unknownStatus = new AppUser(
                "auth-uid-12345", "employee_new", null, "Nguyen Van Moi",
                25, "0912345678", "Pending", AppUser.ROLE_EMPLOYEE, null
        );
        assertNotNull("Unknown status rejected",
                UserManagementPolicy.validateNewUser(adminUser, unknownStatus));
    }

    @Test
    public void validateNewUser_invalidEmailRejected() {
        AppUser invalidEmail = new AppUser(
                "auth-uid-12345", "employee_new", "not-an-email", "Nguyen Van Moi",
                25, "0912345678", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null
        );
        assertNotNull("Invalid email format rejected",
                UserManagementPolicy.validateNewUser(adminUser, invalidEmail));
    }

    @Test
    public void canManageUsers_rejectsUppercaseOrMixedCaseAdmin() {
        AppUser uppercaseAdmin = new AppUser(
                "admin-uid", "admin", null, "Admin User", 30, "0912345678",
                AppUser.STATUS_NORMAL, "ADMIN", null
        );
        assertFalse("Role 'ADMIN' must be rejected (only exact 'admin' allowed)",
                UserManagementPolicy.canManageUsers(uppercaseAdmin));

        AppUser mixedCaseAdmin = new AppUser(
                "admin-uid", "admin", null, "Admin User", 30, "0912345678",
                AppUser.STATUS_NORMAL, "Admin", null
        );
        assertFalse("Role 'Admin' must be rejected (only exact 'admin' allowed)",
                UserManagementPolicy.canManageUsers(mixedCaseAdmin));
    }

    @Test
    public void validateNewUser_nullStatusRejected() {
        AppUser nullStatusUser = new AppUser(
                "auth-uid-12345", "employee_new", null, "Nguyen Van Moi",
                25, "0912345678", null, AppUser.ROLE_EMPLOYEE, null
        );
        assertNotNull("Null status must be rejected fail-closed",
                UserManagementPolicy.validateNewUser(adminUser, nullStatusUser));
    }

    @Test
    public void validateNewUser_nullRoleRejected() {
        AppUser nullRoleUser = new AppUser(
                "auth-uid-12345", "employee_new", null, "Nguyen Van Moi",
                25, "0912345678", AppUser.STATUS_NORMAL, null, null
        );
        assertNotNull("Null role must be rejected fail-closed",
                UserManagementPolicy.validateNewUser(adminUser, nullRoleUser));
    }
}
