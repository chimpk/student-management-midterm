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
import com.example.studentmgmt.ui.history.LoginHistoryViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for LoginHistoryViewModel.
 * Verifies loading records, RBAC view restrictions, error emissions,
 * and listener lifecycle management without duplicate registrations.
 */
public class LoginHistoryViewModelTest {

    private FakeLoginHistoryRepository fakeRepository;
    private AppUser adminUser;
    private AppUser employeeUser;

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

        adminUser = new AppUser(
                "admin-uid", "admin", "admin@studentmgmt.edu.vn",
                "Admin User", 30, "0912345678",
                AppUser.STATUS_NORMAL, AppUser.ROLE_ADMIN, null
        );

        employeeUser = new AppUser(
                "emp-uid", "employee", "emp@studentmgmt.edu.vn",
                "Employee User", 22, "0912345670",
                AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null
        );

        fakeRepository = new FakeLoginHistoryRepository();
    }

    @After
    public void tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @Test
    public void startListening_adminCanViewAnyTarget_loadsRecordsSuccessfully() {
        LoginHistoryViewModel viewModel = new LoginHistoryViewModel(fakeRepository, adminUser, "emp-uid");
        viewModel.startListening();

        assertNotNull(viewModel.getRecords().getValue());
        assertEquals(2, viewModel.getRecords().getValue().size());
        assertEquals("log_2", viewModel.getRecords().getValue().get(0).getId());
        assertFalse(Boolean.TRUE.equals(viewModel.getIsLoading().getValue()));
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void startListening_nonAdminViewsSelf_loadsRecordsSuccessfully() {
        LoginHistoryViewModel viewModel = new LoginHistoryViewModel(fakeRepository, employeeUser, "emp-uid");
        viewModel.startListening();

        assertNotNull(viewModel.getRecords().getValue());
        assertEquals(2, viewModel.getRecords().getValue().size());
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void startListening_nonAdminViewsOther_emitsPermissionDeniedError() {
        LoginHistoryViewModel viewModel = new LoginHistoryViewModel(fakeRepository, employeeUser, "admin-uid");
        viewModel.startListening();

        assertNull(viewModel.getRecords().getValue());
        assertNotNull(viewModel.getErrorMessage().getValue());
        assertTrue(viewModel.getErrorMessage().getValue().contains("không có quyền"));
        assertFalse(Boolean.TRUE.equals(viewModel.getIsLoading().getValue()));
    }

    @Test
    public void startListening_errorFromRepository_emitsErrorMessage() {
        fakeRepository.shouldFail = true;
        fakeRepository.failureMessage = "Lỗi kết nối Firestore";

        LoginHistoryViewModel viewModel = new LoginHistoryViewModel(fakeRepository, adminUser, "admin-uid");
        viewModel.startListening();

        assertNotNull(viewModel.getErrorMessage().getValue());
        assertEquals("Lỗi kết nối Firestore", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void onCleared_removesListener() {
        LoginHistoryViewModel viewModel = new LoginHistoryViewModel(fakeRepository, adminUser, "admin-uid");
        viewModel.startListening();
        assertFalse(fakeRepository.listenerRemoved);

        viewModel.onCleared();
        assertTrue(fakeRepository.listenerRemoved);
    }

    @Test
    public void startListening_calledMultipleTimes_doesNotDuplicateListeners() {
        LoginHistoryViewModel viewModel = new LoginHistoryViewModel(fakeRepository, adminUser, "admin-uid");
        viewModel.startListening();
        viewModel.startListening();
        viewModel.startListening();

        assertEquals(1, fakeRepository.activeListenerCount);
    }

    // --- Fake Test Implementation ---

    private static class FakeLoginHistoryRepository implements LoginHistoryRepository {
        boolean shouldFail = false;
        String failureMessage = "Repository error";
        boolean listenerRemoved = false;
        int activeListenerCount = 0;

        @Override
        public void recordLogin(String uid, String device, RecordCallback callback) {}

        @Override
        public ListenerHandle listenLoginHistory(AppUser currentUser, String targetUid, HistoryCallback callback) {
            activeListenerCount++;
            if (shouldFail) {
                callback.onError(failureMessage);
            } else {
                List<LoginRecord> sample = new ArrayList<>();
                sample.add(new LoginRecord("log_2", 2000L, "Pixel 7"));
                sample.add(new LoginRecord("log_1", 1000L, "Pixel 6"));
                callback.onHistoryLoaded(sample);
            }

            return () -> {
                listenerRemoved = true;
                activeListenerCount--;
            };
        }
    }
}
