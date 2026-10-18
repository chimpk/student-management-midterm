package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.studentmgmt.data.AuthCallback;
import com.example.studentmgmt.data.AuthRepository;
import com.example.studentmgmt.data.DefaultUsernameAuthResolver;
import com.example.studentmgmt.data.UserDocumentValidator;
import com.example.studentmgmt.model.AppUser;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AuthRepositoryTest {

    /**
     * Fake repository that mirrors FirebaseAuthRepository by invoking the production UserDocumentValidator.
     */
    static class FakeAuthRepository implements AuthRepository {

        private final DefaultUsernameAuthResolver resolver = new DefaultUsernameAuthResolver();
        private final Map<String, String> credentials = new HashMap<>(); // authEmail -> password
        private final Map<String, Map<String, Object>> userDocuments = new HashMap<>(); // uid -> raw data map
        private final Map<String, String> emailToUid = new HashMap<>();
        private AppUser currentUser;

        void registerUser(String username, String password, String role, String status, String uid) {
            String authEmail = resolver.resolveAuthEmail(username);
            if (authEmail != null) {
                credentials.put(authEmail, password);
                emailToUid.put(authEmail, uid);
            }

            Map<String, Object> doc = new HashMap<>();
            doc.put("username", username);
            doc.put("name", "Nguyễn Văn " + username);
            doc.put("age", 25);
            doc.put("phone", "0901234567");
            if (status != null) {
                doc.put("status", status);
            }
            if (role != null) {
                doc.put("role", role);
            }
            userDocuments.put(uid, doc);
        }

        void registerUserWithoutDocument(String username, String password, String uid) {
            String authEmail = resolver.resolveAuthEmail(username);
            credentials.put(authEmail, password);
            emailToUid.put(authEmail, uid);
            // Document intentionally omitted
        }

        @Override
        public void signIn(String identifier, String password, AuthCallback callback) {
            if (identifier == null || password == null) {
                callback.onError(ERROR_INVALID_CREDENTIALS);
                return;
            }

            String authEmail = resolver.resolveAuthEmail(identifier);
            if (authEmail == null) {
                // Safe failure: do not reveal user existence
                callback.onError(ERROR_INVALID_CREDENTIALS);
                return;
            }

            String expectedPassword = credentials.get(authEmail);
            if (expectedPassword == null || !expectedPassword.equals(password)) {
                // Safe failure: invalid username or password
                callback.onError(ERROR_INVALID_CREDENTIALS);
                return;
            }

            String uid = emailToUid.get(authEmail);
            Map<String, Object> rawDoc = userDocuments.get(uid);

            if (rawDoc == null) {
                signOut();
                callback.onError(ERROR_INVALID_CREDENTIALS);
                return;
            }

            // Calls the exact production UserDocumentValidator
            UserDocumentValidator.Result result = UserDocumentValidator.validate(uid, rawDoc);

            if (result.isLocked()) {
                signOut();
                callback.onError(ERROR_ACCOUNT_LOCKED);
                return;
            }

            if (!result.isValid() || result.getUser() == null) {
                signOut();
                callback.onError(ERROR_INVALID_CREDENTIALS);
                return;
            }

            currentUser = result.getUser();
            callback.onSuccess(result.getUser());
        }

        @Override
        public void signOut() {
            currentUser = null;
        }

        @Override
        public AppUser getCurrentUser() {
            return currentUser;
        }

        @Override
        public boolean isSignedIn() {
            return currentUser != null;
        }
    }

    private FakeAuthRepository repo;

    @Before
    public void setUp() {
        repo = new FakeAuthRepository();
        repo.registerUser("admin", "admin@123456", AppUser.ROLE_ADMIN, AppUser.STATUS_NORMAL, "uid_admin");
        repo.registerUser("manager", "manager@123456", AppUser.ROLE_MANAGER, AppUser.STATUS_NORMAL, "uid_manager");
        repo.registerUser("employee_locked", "employee@123456", AppUser.ROLE_EMPLOYEE, AppUser.STATUS_LOCKED, "uid_locked");
        repo.registerUser("corrupt_user", "corrupt@123456", null, null, "uid_corrupt"); // missing role and status
        repo.registerUserWithoutDocument("missing_doc", "pass123456", "uid_missing");
    }

    @Test
    public void validUsername_signsInSuccessfully() {
        AtomicReference<AppUser> successUser = new AtomicReference<>();
        AtomicReference<String> errorRef = new AtomicReference<>();

        repo.signIn("admin", "admin@123456", new AuthCallback() {
            @Override
            public void onSuccess(AppUser user) {
                successUser.set(user);
            }

            @Override
            public void onError(String message) {
                errorRef.set(message);
            }
        });

        assertNotNull("Login should succeed", successUser.get());
        assertNull("There should be no error", errorRef.get());
        assertEquals("admin", successUser.get().getUsername());
        assertTrue("User should be admin", successUser.get().isAdmin());
        assertFalse("User should not be locked", successUser.get().isLocked());
        assertTrue(repo.isSignedIn());
    }

    @Test
    public void wrongPassword_failsSafelyWithoutExposingUserExistence() {
        AtomicBoolean failed = new AtomicBoolean(false);
        AtomicReference<String> errorRef = new AtomicReference<>();

        repo.signIn("admin", "wrong_password", new AuthCallback() {
            @Override
            public void onSuccess(AppUser user) {}

            @Override
            public void onError(String message) {
                failed.set(true);
                errorRef.set(message);
            }
        });

        assertTrue(failed.get());
        assertEquals(AuthRepository.ERROR_INVALID_CREDENTIALS, errorRef.get());
        assertFalse(repo.isSignedIn());
    }

    @Test
    public void nonExistentUser_failsSafelyWithIdenticalError() {
        AtomicBoolean failed = new AtomicBoolean(false);
        AtomicReference<String> errorRef = new AtomicReference<>();

        repo.signIn("non_existent_user", "any_password", new AuthCallback() {
            @Override
            public void onSuccess(AppUser user) {}

            @Override
            public void onError(String message) {
                failed.set(true);
                errorRef.set(message);
            }
        });

        assertTrue(failed.get());
        assertEquals(AuthRepository.ERROR_INVALID_CREDENTIALS, errorRef.get());
        assertFalse(repo.isSignedIn());
    }

    @Test
    public void missingUserDocument_rejectsAndSignsOut() {
        AtomicBoolean failed = new AtomicBoolean(false);
        AtomicReference<String> errorRef = new AtomicReference<>();

        repo.signIn("missing_doc", "pass123456", new AuthCallback() {
            @Override
            public void onSuccess(AppUser user) {}

            @Override
            public void onError(String message) {
                failed.set(true);
                errorRef.set(message);
            }
        });

        assertTrue(failed.get());
        assertEquals(AuthRepository.ERROR_INVALID_CREDENTIALS, errorRef.get());
        assertFalse("Missing document must ensure session is cleared", repo.isSignedIn());
        assertNull(repo.getCurrentUser());
    }

    @Test
    public void lockedUser_rejectsAndSignsOutWithLockedMessage() {
        AtomicBoolean failed = new AtomicBoolean(false);
        AtomicReference<String> errorRef = new AtomicReference<>();

        repo.signIn("employee_locked", "employee@123456", new AuthCallback() {
            @Override
            public void onSuccess(AppUser user) {}

            @Override
            public void onError(String message) {
                failed.set(true);
                errorRef.set(message);
            }
        });

        assertTrue(failed.get());
        assertEquals(AuthRepository.ERROR_ACCOUNT_LOCKED, errorRef.get());
        assertFalse("Locked user must be signed out immediately", repo.isSignedIn());
    }

    @Test
    public void corruptUserDocument_missingStatusOrRole_failsClosed() {
        AtomicBoolean failed = new AtomicBoolean(false);
        AtomicReference<String> errorRef = new AtomicReference<>();

        repo.signIn("corrupt_user", "corrupt@123456", new AuthCallback() {
            @Override
            public void onSuccess(AppUser user) {}

            @Override
            public void onError(String message) {
                failed.set(true);
                errorRef.set(message);
            }
        });

        assertTrue("Corrupt user document must fail login", failed.get());
        assertEquals(AuthRepository.ERROR_INVALID_CREDENTIALS, errorRef.get());
        assertFalse(repo.isSignedIn());
    }
}
