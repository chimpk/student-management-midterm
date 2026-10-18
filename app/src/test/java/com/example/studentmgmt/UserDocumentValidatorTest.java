package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.studentmgmt.data.UserDocumentValidator;
import com.example.studentmgmt.model.AppUser;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDocumentValidatorTest {

    private Map<String, Object> validDoc;

    @Before
    public void setUp() {
        validDoc = new HashMap<>();
        validDoc.put("username", "manager");
        validDoc.put("name", "Nguyễn Văn A");
        validDoc.put("age", 28);
        validDoc.put("phone", "0912345678");
        validDoc.put("status", "Normal");
        validDoc.put("role", "manager");
        validDoc.put("email", "manager@student.app");
        validDoc.put("avatarUrl", "https://example.com/avatar.png");
    }

    @Test
    public void validDocument_parsesSuccessfully() {
        UserDocumentValidator.Result result = UserDocumentValidator.validate("uid_123", validDoc);
        assertTrue(result.isValid());
        assertFalse(result.isLocked());
        assertNotNull(result.getUser());

        AppUser user = result.getUser();
        assertEquals("uid_123", user.getUid());
        assertEquals("manager", user.getUsername());
        assertEquals("Nguyễn Văn A", user.getName());
        assertEquals(28, user.getAge());
        assertEquals("0912345678", user.getPhone());
        assertEquals("Normal", user.getStatus());
        assertEquals("manager", user.getRole());
        assertTrue(user.isManager());
    }

    @Test
    public void validDocument_withLongAge_parsesSuccessfully() {
        validDoc.put("age", 30L); // Firestore often returns Long
        UserDocumentValidator.Result result = UserDocumentValidator.validate("uid_123", validDoc);
        assertTrue(result.isValid());
        assertEquals(30, result.getUser().getAge());
    }

    @Test
    public void lockedStatus_returnsLockedResult() {
        validDoc.put("status", "Locked");
        UserDocumentValidator.Result result = UserDocumentValidator.validate("uid_123", validDoc);

        assertFalse(result.isValid());
        assertTrue(result.isLocked());
        assertEquals(UserDocumentValidator.Status.LOCKED, result.getStatus());
        assertNull(result.getUser());
    }

    @Test
    public void missingStatus_failsClosed() {
        validDoc.remove("status");
        UserDocumentValidator.Result result = UserDocumentValidator.validate("uid_123", validDoc);

        assertFalse(result.isValid());
        assertFalse(result.isLocked());
        assertEquals(UserDocumentValidator.Status.INVALID, result.getStatus());
    }

    @Test
    public void caseSensitiveStatus_rejectsWrongCase() {
        validDoc.put("status", "normal"); // lowercase
        assertFalse("Lowercase 'normal' must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.put("status", "NORMAL"); // all caps
        assertFalse("Uppercase 'NORMAL' must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.put("status", "locked"); // lowercase locked
        assertFalse("Lowercase 'locked' must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());
    }

    @Test
    public void unknownStatus_failsClosed() {
        validDoc.put("status", "Pending");
        UserDocumentValidator.Result result = UserDocumentValidator.validate("uid_123", validDoc);

        assertFalse(result.isValid());
        assertEquals(UserDocumentValidator.Status.INVALID, result.getStatus());
    }

    @Test
    public void missingRole_failsClosed() {
        validDoc.remove("role");
        UserDocumentValidator.Result result = UserDocumentValidator.validate("uid_123", validDoc);

        assertFalse(result.isValid());
        assertEquals(UserDocumentValidator.Status.INVALID, result.getStatus());
    }

    @Test
    public void caseSensitiveRole_rejectsWrongCase() {
        validDoc.put("role", "ADMIN"); // uppercase
        assertFalse("Uppercase 'ADMIN' must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.put("role", "Manager"); // Titlecase
        assertFalse("Titlecase 'Manager' must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.put("role", "Employee"); // Titlecase
        assertFalse("Titlecase 'Employee' must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());
    }

    @Test
    public void unknownRole_failsClosed() {
        validDoc.put("role", "superadmin");
        UserDocumentValidator.Result result = UserDocumentValidator.validate("uid_123", validDoc);

        assertFalse(result.isValid());
        assertEquals(UserDocumentValidator.Status.INVALID, result.getStatus());
    }

    @Test
    public void floatingPointAge_failsClosed() {
        validDoc.put("age", 18.9); // Double
        assertFalse("Double age must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.put("age", 25.5f); // Float
        assertFalse("Float age must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());
    }

    @Test
    public void overflowAge_failsClosed() {
        // 4294967314L overflows to 18 if intValue() is called before 64-bit range check
        validDoc.put("age", 4294967314L);
        assertFalse("Overflow age 4294967314L must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.put("age", Long.MAX_VALUE);
        assertFalse("Long.MAX_VALUE age must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.put("age", Long.MIN_VALUE);
        assertFalse("Long.MIN_VALUE age must be rejected",
                UserDocumentValidator.validate("uid_123", validDoc).isValid());
    }

    @Test
    public void invalidUsername_failsClosed() {
        validDoc.put("username", "12_invalid_start");
        UserDocumentValidator.Result result = UserDocumentValidator.validate("uid_123", validDoc);
        assertFalse(result.isValid());

        validDoc.put("username", "ab"); // too short
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.remove("username");
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());
    }

    @Test
    public void invalidName_failsClosed() {
        validDoc.put("name", "A"); // too short
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.remove("name");
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());
    }

    @Test
    public void invalidAge_failsClosed() {
        validDoc.put("age", 17); // under 18
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.put("age", 65); // over 60
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.remove("age");
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());
    }

    @Test
    public void invalidPhone_failsClosed() {
        validDoc.put("phone", "091234567"); // 9 digits
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.put("phone", "1912345678"); // does not start with 0
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());

        validDoc.remove("phone");
        assertFalse(UserDocumentValidator.validate("uid_123", validDoc).isValid());
    }

    @Test
    public void allowedRoles_adminAndEmployee_parseCorrectly() {
        validDoc.put("role", "admin");
        UserDocumentValidator.Result adminResult = UserDocumentValidator.validate("uid_1", validDoc);
        assertTrue(adminResult.isValid());
        assertTrue(adminResult.getUser().isAdmin());

        validDoc.put("role", "employee");
        UserDocumentValidator.Result employeeResult = UserDocumentValidator.validate("uid_2", validDoc);
        assertTrue(employeeResult.isValid());
        assertTrue(employeeResult.getUser().isEmployee());
    }
}
