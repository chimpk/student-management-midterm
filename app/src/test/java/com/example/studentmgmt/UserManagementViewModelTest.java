package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;

import com.example.studentmgmt.data.UserRepository;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.user.UserManagementViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for UserManagementViewModel.
 * Verifies user list loading, action handlers, self-lock/delete rejections, and listener cleanup.
 */
public class UserManagementViewModelTest {

    private FakeUserRepository fakeRepository;
    private AppUser adminUser;
    private AppUser employeeUser;
    private UserManagementViewModel viewModel;

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
        fakeRepository = new FakeUserRepository();

        adminUser = new AppUser(
                "admin-uid",
                "admin",
                "admin@tdtu.edu.vn",
                "Admin User",
                30,
                "0912345678",
                AppUser.STATUS_NORMAL,
                AppUser.ROLE_ADMIN,
                null
        );

        employeeUser = new AppUser(
                "emp-uid",
                "employee",
                "emp@tdtu.edu.vn",
                "Employee User",
                25,
                "0987654321",
                AppUser.STATUS_NORMAL,
                AppUser.ROLE_EMPLOYEE,
                null
        );

        viewModel = new UserManagementViewModel(fakeRepository, adminUser);
    }

    @After
    public void tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @Test
    public void startListening_loadsUsersSuccessfully() {
        List<AppUser> sampleUsers = new ArrayList<>();
        sampleUsers.add(adminUser);
        sampleUsers.add(employeeUser);
        fakeRepository.setUsersToReturn(sampleUsers);

        viewModel.startListening();

        assertNotNull(viewModel.getUsers().getValue());
        assertEquals(2, viewModel.getUsers().getValue().size());
        assertFalse(Boolean.TRUE.equals(viewModel.getLoading().getValue()));
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void lockUser_selfLockAttemptSetsErrorMessage() {
        viewModel.lockUser("admin-uid", true);

        assertNotNull(viewModel.getErrorMessage().getValue());
        assertTrue(viewModel.getErrorMessage().getValue().contains("Không thể tự khóa"));
    }

    @Test
    public void lockUser_validTargetSucceeds() {
        viewModel.lockUser("emp-uid", true);

        assertEquals("emp-uid", fakeRepository.lastLockedUid);
        assertTrue(fakeRepository.lastLockedStatus);
        assertNotNull(viewModel.getActionSuccessMessage().getValue());
    }

    @Test
    public void deleteUser_selfDeleteAttemptSetsErrorMessage() {
        viewModel.deleteUser("admin-uid");

        assertNotNull(viewModel.getErrorMessage().getValue());
        assertTrue(viewModel.getErrorMessage().getValue().contains("Không thể tự xóa"));
    }

    @Test
    public void deleteUser_validTargetSucceeds() {
        viewModel.deleteUser("emp-uid");

        assertEquals("emp-uid", fakeRepository.lastDeletedUid);
        assertNotNull(viewModel.getActionSuccessMessage().getValue());
    }

    @Test
    public void createUser_validUserSucceeds() {
        AppUser newUser = new AppUser(
                "new-uid", "new_user", "new@tdtu.edu.vn",
                "New User", 26, "0912345678", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null
        );
        viewModel.createUser(newUser);

        assertEquals("new-uid", fakeRepository.lastCreatedUser.getUid());
        assertNotNull(viewModel.getActionSuccessMessage().getValue());
    }

    @Test
    public void updateUser_validInputSucceeds() {
        viewModel.updateUser("emp-uid", "Updated Name", 28, "0988776655", AppUser.ROLE_MANAGER);

        assertEquals("emp-uid", fakeRepository.lastUpdatedUid);
        assertEquals("Updated Name", fakeRepository.lastUpdatedName);
        assertNotNull(viewModel.getActionSuccessMessage().getValue());
    }

    @Test
    public void onCleared_removesSnapshotListener() {
        viewModel.startListening();
        assertTrue(fakeRepository.isListening);

        viewModel.clearViewModel();
        assertFalse("Listener handle must be cleaned up on cleared", fakeRepository.isListening);
    }

    private static class FakeUserRepository implements UserRepository {
        private List<AppUser> usersToReturn = new ArrayList<>();
        private boolean isListening = false;
        private String lastLockedUid = null;
        private boolean lastLockedStatus = false;
        private String lastDeletedUid = null;
        private AppUser lastCreatedUser = null;
        private String lastUpdatedUid = null;
        private String lastUpdatedName = null;

        public void setUsersToReturn(List<AppUser> users) {
            this.usersToReturn = users;
        }

        @Override
        public ListenerHandle listenUsers(AppUser currentUser, UserListCallback callback) {
            isListening = true;
            callback.onUsersLoaded(usersToReturn);
            return () -> isListening = false;
        }

        @Override
        public ListenerHandle listenUser(String uid, UserCallback callback) {
            return () -> {};
        }

        @Override
        public void createUserProfile(AppUser currentUser, AppUser newUser, OperationCallback callback) {
            this.lastCreatedUser = newUser;
            callback.onSuccess();
        }

        @Override
        public void updateUserProfile(AppUser currentUser, String uid, String name, int age, String phone, String role, OperationCallback callback) {
            this.lastUpdatedUid = uid;
            this.lastUpdatedName = name;
            callback.onSuccess();
        }

        @Override
        public void setUserLocked(AppUser currentUser, String uid, boolean locked, OperationCallback callback) {
            this.lastLockedUid = uid;
            this.lastLockedStatus = locked;
            callback.onSuccess();
        }

        @Override
        public void deleteUserProfile(AppUser currentUser, String uid, OperationCallback callback) {
            this.lastDeletedUid = uid;
            callback.onSuccess();
        }
    }
}
