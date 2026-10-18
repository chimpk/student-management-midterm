package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;

import com.example.studentmgmt.data.LoginHistoryRepository;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.LoginRecord;
import com.example.studentmgmt.ui.auth.LoginViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LoginViewModelTest {

    private AuthRepositoryTest.FakeAuthRepository fakeRepo;
    private FakeLoginHistoryRepository fakeHistoryRepo;
    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        ArchTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
            @Override
            public void executeOnDiskIO(Runnable runnable) {
                runnable.run();
            }

            @Override
            public void postToMainThread(Runnable runnable) {
                runnable.run();
            }

            @Override
            public boolean isMainThread() {
                return true;
            }
        });

        fakeRepo = new AuthRepositoryTest.FakeAuthRepository();
        fakeRepo.registerUser("admin", "admin@123456", AppUser.ROLE_ADMIN, AppUser.STATUS_NORMAL, "uid_1");
        fakeRepo.registerUser("locked_user", "locked@123456", AppUser.ROLE_EMPLOYEE, AppUser.STATUS_LOCKED, "uid_2");

        fakeHistoryRepo = new FakeLoginHistoryRepository();
        viewModel = new LoginViewModel(fakeRepo, fakeHistoryRepo, "Google Pixel 7");
    }

    @After
    public void tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @Test
    public void emptyIdentifier_setsValidationError() {
        viewModel.login("", "password123");
        assertEquals(LoginViewModel.ERROR_EMPTY_IDENTIFIER, viewModel.getErrorMessage().getValue());
        assertFalse(Boolean.TRUE.equals(viewModel.getIsLoading().getValue()));
        assertNull(viewModel.getLoginSuccess().getValue());
        assertEquals(0, fakeHistoryRepo.recordCount);
    }

    @Test
    public void emptyPassword_setsValidationError() {
        viewModel.login("admin", "");
        assertEquals(LoginViewModel.ERROR_EMPTY_PASSWORD, viewModel.getErrorMessage().getValue());
        assertFalse(Boolean.TRUE.equals(viewModel.getIsLoading().getValue()));
        assertNull(viewModel.getLoginSuccess().getValue());
        assertEquals(0, fakeHistoryRepo.recordCount);
    }

    @Test
    public void shortPassword_setsValidationError() {
        viewModel.login("admin", "12345");
        assertEquals(LoginViewModel.ERROR_PASSWORD_TOO_SHORT, viewModel.getErrorMessage().getValue());
        assertFalse(Boolean.TRUE.equals(viewModel.getIsLoading().getValue()));
        assertNull(viewModel.getLoginSuccess().getValue());
        assertEquals(0, fakeHistoryRepo.recordCount);
    }

    @Test
    public void validLogin_updatesLoginSuccessLiveData_andRecordsLoginHistory() {
        viewModel.login("admin", "admin@123456");
        assertNull(viewModel.getErrorMessage().getValue());
        assertFalse(Boolean.TRUE.equals(viewModel.getIsLoading().getValue()));
        assertNotNull(viewModel.getLoginSuccess().getValue());
        assertEquals("admin", viewModel.getLoginSuccess().getValue().getUsername());

        // Login history was recorded
        assertEquals(1, fakeHistoryRepo.recordCount);
        assertEquals("uid_1", fakeHistoryRepo.lastRecordedUid);
        assertEquals("Google Pixel 7", fakeHistoryRepo.lastRecordedDevice);
    }

    @Test
    public void lockedUser_setsAccountLockedError_andDoesNotRecordLoginHistory() {
        viewModel.login("locked_user", "locked@123456");
        assertEquals("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.", viewModel.getErrorMessage().getValue());
        assertFalse(Boolean.TRUE.equals(viewModel.getIsLoading().getValue()));
        assertNull(viewModel.getLoginSuccess().getValue());

        // Never records for locked account
        assertEquals(0, fakeHistoryRepo.recordCount);
    }

    @Test
    public void invalidPassword_doesNotRecordLoginHistory() {
        viewModel.login("admin", "wrong_password");
        assertNotNull(viewModel.getErrorMessage().getValue());
        assertNull(viewModel.getLoginSuccess().getValue());
        assertEquals(0, fakeHistoryRepo.recordCount);
    }

    @Test
    public void recordHistoryFailure_doesNotBreakValidSession_andWarningIsSurfaced() {
        fakeHistoryRepo.shouldFail = true;
        fakeHistoryRepo.failureMessage = "Network glitch writing history";

        viewModel.login("admin", "admin@123456");
        // Login session is still valid and successful!
        assertNotNull(viewModel.getLoginSuccess().getValue());
        assertEquals("admin", viewModel.getLoginSuccess().getValue().getUsername());
        assertNull(viewModel.getErrorMessage().getValue());

        // Warning must be explicitly surfaced to user/UI, not silently swallowed
        assertNotNull(viewModel.getWarningMessage().getValue());
        assertTrue(viewModel.getWarningMessage().getValue().contains("Network glitch writing history"));

        // Outcome is also combined with warning
        assertNotNull(viewModel.getLoginOutcome().getValue());
        assertTrue(viewModel.getLoginOutcome().getValue().hasWarning());
    }

    @Test
    public void asyncHistoryError_combinesOutcomeBeforeNavigation_andPreservesWarningForDestination() {
        fakeHistoryRepo.deferCallback = true;

        viewModel.login("admin", "admin@123456");

        // Auth succeeded, but loginOutcome is deferred until history coordination completes
        assertNull(viewModel.getLoginOutcome().getValue());
        assertNull(viewModel.getLoginSuccess().getValue());
        assertEquals(1, fakeHistoryRepo.recordCount);

        // Now trigger asynchronous history failure (callback comes later)
        fakeHistoryRepo.triggerDeferredError("Delayed Firestore write failure");

        // Outcome must be delivered combining valid user and destination-safe warning
        LoginViewModel.LoginOutcome outcome = viewModel.getLoginOutcome().getValue();
        assertNotNull(outcome);
        assertEquals("admin", outcome.getUser().getUsername());
        assertTrue(outcome.hasWarning());
        assertTrue(outcome.getWarningMessage().contains("Delayed Firestore write failure"));

        // Warning and success LiveData are also populated safely
        assertNotNull(viewModel.getLoginSuccess().getValue());
        assertNotNull(viewModel.getWarningMessage().getValue());
        assertNull(viewModel.getErrorMessage().getValue());
    }

    // --- Fake Test Implementation ---

    private static class FakeLoginHistoryRepository implements LoginHistoryRepository {
        int recordCount = 0;
        String lastRecordedUid;
        String lastRecordedDevice;
        boolean shouldFail = false;
        String failureMessage = "Record error";
        boolean deferCallback = false;
        RecordCallback deferredCallback;

        @Override
        public void recordLogin(String uid, String device, RecordCallback callback) {
            recordCount++;
            lastRecordedUid = uid;
            lastRecordedDevice = device;
            if (deferCallback) {
                this.deferredCallback = callback;
                return;
            }
            if (shouldFail) {
                if (callback != null) callback.onError(failureMessage);
            } else {
                if (callback != null) callback.onSuccess();
            }
        }

        void triggerDeferredError(String msg) {
            if (deferredCallback != null) {
                deferredCallback.onError(msg);
            }
        }

        void triggerDeferredSuccess() {
            if (deferredCallback != null) {
                deferredCallback.onSuccess();
            }
        }

        @Override
        public ListenerHandle listenLoginHistory(AppUser currentUser, String targetUid, HistoryCallback callback) {
            return () -> {};
        }
    }
}
