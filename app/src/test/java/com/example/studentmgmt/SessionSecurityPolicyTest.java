package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.navigation.SessionSecurityPolicy;

import org.junit.Before;
import org.junit.Test;

/**
 * Pure Java unit tests for SessionSecurityPolicy.
 * Verifies that:
 * 1. Locked status terminates session.
 * 2. Missing/null observed user terminates session.
 * 3. Invalid schema terminates session.
 * 4. PERMISSION_DENIED terminates session (fail-closed for rules revocation).
 * 5. USER_NOT_FOUND terminates session.
 * 6. NETWORK_TEMPORARY does NOT terminate session (transient resilience).
 */
public class SessionSecurityPolicyTest {

    private AppUser validAdmin;
    private AppUser lockedAdmin;

    @Before
    public void setUp() {
        validAdmin = new AppUser(
                "uid-1",
                "admin",
                "admin@tdtu.edu.vn",
                "Admin User",
                30,
                "0912345678",
                AppUser.STATUS_NORMAL,
                AppUser.ROLE_ADMIN,
                null
        );

        lockedAdmin = new AppUser(
                "uid-1",
                "admin",
                "admin@tdtu.edu.vn",
                "Admin User",
                30,
                "0912345678",
                AppUser.STATUS_LOCKED,
                AppUser.ROLE_ADMIN,
                null
        );
    }

    @Test
    public void evaluateUserSnapshot_normalUserMaintainsSession() {
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateUserSnapshot(validAdmin, validAdmin);
        assertFalse(eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.MAINTAIN, eval.getDecision());
        assertNull(eval.getReason());
    }

    @Test
    public void evaluateUserSnapshot_lockedUserTerminatesSession() {
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateUserSnapshot(validAdmin, lockedAdmin);
        assertTrue(eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.TERMINATE, eval.getDecision());
        assertNotNull(eval.getReason());
        assertTrue(eval.getReason().contains("bị khóa"));
    }

    @Test
    public void evaluateUserSnapshot_nullObservedUserTerminatesSession() {
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateUserSnapshot(validAdmin, null);
        assertTrue(eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.TERMINATE, eval.getDecision());
        assertNotNull(eval.getReason());
    }

    @Test
    public void evaluateUserSnapshot_invalidRoleTerminatesSession() {
        AppUser corruptUser = new AppUser(
                "uid-1", "admin", null, "Corrupt", 30, "0912345678",
                AppUser.STATUS_NORMAL, "invalid_role", null
        );
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateUserSnapshot(validAdmin, corruptUser);
        assertTrue(eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.TERMINATE, eval.getDecision());
    }

    @Test
    public void evaluateError_permissionDeniedTerminatesSession() {
        // Enforces fail-closed behavior when rules revoke read access after admin lock
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateError(SessionSecurityPolicy.SessionErrorType.PERMISSION_DENIED);
        assertTrue(eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.TERMINATE, eval.getDecision());
        assertNotNull(eval.getReason());
    }

    @Test
    public void evaluateError_userNotFoundTerminatesSession() {
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateError(SessionSecurityPolicy.SessionErrorType.USER_NOT_FOUND);
        assertTrue(eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.TERMINATE, eval.getDecision());
        assertNotNull(eval.getReason());
    }

    @Test
    public void evaluateError_invalidDocumentTerminatesSession() {
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateError(SessionSecurityPolicy.SessionErrorType.INVALID_DOCUMENT);
        assertTrue(eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.TERMINATE, eval.getDecision());
    }

    @Test
    public void evaluateError_networkTemporaryMaintainsSession() {
        // Transient network failure MUST NOT kick user out!
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateError(SessionSecurityPolicy.SessionErrorType.NETWORK_TEMPORARY);
        assertFalse("Temporary network glitch must not terminate session", eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.MAINTAIN, eval.getDecision());
        assertNull(eval.getReason());
    }

    @Test
    public void evaluateError_unauthenticatedTerminatesSession() {
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateError(SessionSecurityPolicy.SessionErrorType.UNAUTHENTICATED);
        assertTrue("UNAUTHENTICATED must terminate session fail-closed", eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.TERMINATE, eval.getDecision());
        assertNotNull(eval.getReason());
    }

    @Test
    public void evaluateError_unknownErrorMaintainsSession() {
        SessionSecurityPolicy.Evaluation eval =
                SessionSecurityPolicy.evaluateError(SessionSecurityPolicy.SessionErrorType.UNKNOWN);
        assertFalse(eval.shouldTerminate());
        assertEquals(SessionSecurityPolicy.Decision.MAINTAIN, eval.getDecision());
    }
}
