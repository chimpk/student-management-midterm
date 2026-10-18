package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;

import com.example.studentmgmt.data.AuthCallback;
import com.example.studentmgmt.data.AuthRepository;
import com.example.studentmgmt.data.LoginHistoryRepository;
import com.example.studentmgmt.data.ProfileRepository;
import com.example.studentmgmt.data.UserRepository;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.LoginRecord;
import com.example.studentmgmt.model.UiState;
import com.example.studentmgmt.ui.auth.LoginViewModel;
import com.example.studentmgmt.ui.history.LoginHistoryViewModel;
import com.example.studentmgmt.ui.profile.AvatarUploadPolicy;
import com.example.studentmgmt.ui.profile.ProfileViewModel;
import com.example.studentmgmt.ui.user.UserManagementViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * TDD test suite for Task 8 (T15 + T16):
 * - Canonical 4-phase UI states: Loading, Content, Empty, Error
 * - Double submit prevention across Auth, User Management, Profile
 * - Offline failure & Retry mechanisms
 * - Stale callback guards and listener cleanup on lifecycle termination
 */
public class UiStateAndOfflinePolicyTest {

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
    }

    @After
    public void tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    // -------------------------------------------------------------
    // 1. Canonical UI States (Loading / Content / Empty / Error)
    // -------------------------------------------------------------

    @Test
    public void uiState_canonicalTransitions_workCorrectly() {
        UiState<List<String>> loading = UiState.loading();
        assertTrue(loading.isLoading());
        assertFalse(loading.isContent());
        assertFalse(loading.isEmpty());
        assertFalse(loading.isError());

        List<String> items = new ArrayList<>();
        items.add("Item 1");
        UiState<List<String>> content = UiState.content(items);
        assertTrue(content.isContent());
        assertEquals(1, content.getData().size());

        UiState<List<String>> empty = UiState.empty();
        assertTrue(empty.isEmpty());

        UiState<List<String>> error = UiState.error("Mất kết nối mạng");
        assertTrue(error.isError());
        assertEquals("Mất kết nối mạng", error.getErrorMessage());
    }

    // -------------------------------------------------------------
    // 2. Double Submit Guards (Auth / User Management / Profile)
    // -------------------------------------------------------------

    @Test
    public void loginViewModel_doubleSubmit_isIgnoredWhileLoading() {
        DefferableAuthRepo authRepo = new DefferableAuthRepo();
        LoginViewModel vm = new LoginViewModel(authRepo, null, "Pixel 7");

        vm.login("admin", "admin123");
        assertEquals(1, authRepo.signInCallCount);
        assertTrue(Boolean.TRUE.equals(vm.getIsLoading().getValue()));

        // Second click while first is still in-flight
        vm.login("admin", "admin123");
        assertEquals("Double submission must be ignored while in-flight", 1, authRepo.signInCallCount);

        // Resolve first request
        authRepo.resolveSuccess(new AppUser("uid1", "admin", "admin@tdtu.edu.vn", "Admin", 30, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_ADMIN, null));
        assertFalse(Boolean.TRUE.equals(vm.getIsLoading().getValue()));
    }

    @Test
    public void userManagementViewModel_doubleSubmit_isIgnoredWhileActionLoading() {
        DefferableUserRepo userRepo = new DefferableUserRepo();
        AppUser admin = new AppUser("admin-uid", "admin", "admin@tdtu.edu.vn", "Admin", 30, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_ADMIN, null);
        UserManagementViewModel vm = new UserManagementViewModel(userRepo, admin);

        vm.lockUser("user-2", true);
        assertEquals(1, userRepo.lockCallCount);
        assertTrue(Boolean.TRUE.equals(vm.getActionInFlight().getValue()));

        // Second rapid click on lock button while action is in-flight
        vm.lockUser("user-2", true);
        assertEquals("Double lock action must be ignored", 1, userRepo.lockCallCount);

        // Complete action
        userRepo.resolveLockSuccess();
        assertFalse(Boolean.TRUE.equals(vm.getActionInFlight().getValue()));
    }

    @Test
    public void profileViewModel_doubleSubmit_isIgnoredWhileUploading() {
        DefferableProfileRepo profileRepo = new DefferableProfileRepo();
        AppUser user = new AppUser("user-1", "user1", "user1@tdtu.edu.vn", "User One", 20, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null);
        FakeAuthRepo authRepo = new FakeAuthRepo(user);
        ProfileViewModel vm = new ProfileViewModel(profileRepo, authRepo);
        vm.start(user);

        byte[] jpegData = new byte[]{1, 2, 3, 4};
        vm.uploadAvatar(jpegData, "image/jpeg");
        assertEquals(1, profileRepo.uploadCallCount);
        assertEquals(AvatarUploadPolicy.UploadStep.UPLOADING, vm.getUploadState().getValue().getStep());

        // Subsequent click during active upload must be discarded
        vm.uploadAvatar(jpegData, "image/jpeg");
        assertEquals("Double upload submission must be prevented", 1, profileRepo.uploadCallCount);

        // Complete upload
        profileRepo.resolveUploadSuccess("https://storage/avatar.jpg");
        assertEquals(AvatarUploadPolicy.UploadStep.SUCCESS, vm.getUploadState().getValue().getStep());
    }

    // -------------------------------------------------------------
    // 3. Offline Error Handling & Retry Mechanics
    // -------------------------------------------------------------

    @Test
    public void loginHistoryViewModel_retry_restartsListenerAfterError() {
        ControllableHistoryRepo historyRepo = new ControllableHistoryRepo();
        AppUser user = new AppUser("u1", "u1", "u1@tdtu.edu.vn", "User", 25, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null);
        LoginHistoryViewModel vm = new LoginHistoryViewModel(historyRepo, user, "u1");

        vm.startListening();
        assertEquals(1, historyRepo.listenCallCount);

        // Simulate offline / network error from Firestore
        historyRepo.simulateError("Mất kết nối mạng (Unavailable)");
        assertNotNull(vm.getErrorMessage().getValue());
        assertEquals("Mất kết nối mạng (Unavailable)", vm.getErrorMessage().getValue());
        assertFalse(Boolean.TRUE.equals(vm.getIsLoading().getValue()));

        // User taps Retry button
        vm.retry();
        assertEquals(2, historyRepo.listenCallCount);
        assertTrue(Boolean.TRUE.equals(vm.getIsLoading().getValue()));

        // Network recovers
        List<LoginRecord> records = new ArrayList<>();
        records.add(new LoginRecord("rec1", 1000L, "Pixel 8", false));
        historyRepo.simulateSuccess(records);

        assertNull(vm.getErrorMessage().getValue());
        assertFalse(Boolean.TRUE.equals(vm.getIsLoading().getValue()));
        assertEquals(1, vm.getRecords().getValue().size());
    }

    @Test
    public void userManagementViewModel_listLoading_separatedFrom_actionInFlight_andInterleavingHandledCorrectly() {
        DefferableUserRepo userRepo = new DefferableUserRepo();
        AppUser admin = new AppUser("admin-uid", "admin", "admin@tdtu.edu.vn", "Admin", 30, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_ADMIN, null);
        UserManagementViewModel vm = new UserManagementViewModel(userRepo, admin);

        // Start listening (loads list)
        vm.startListening();
        assertTrue(Boolean.TRUE.equals(vm.getLoading().getValue()));
        assertFalse(Boolean.TRUE.equals(vm.getActionInFlight().getValue()));

        // Start mutation while list is loading
        vm.lockUser("user-2", true);
        assertEquals(1, userRepo.lockCallCount);
        assertTrue(Boolean.TRUE.equals(vm.getActionInFlight().getValue()));

        // Interleaving: user list finishes loading while mutation is STILL in-flight
        List<AppUser> loadedList = new ArrayList<>();
        loadedList.add(new AppUser("user-2", "user2", "u2@tdtu.edu.vn", "User Two", 22, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null));
        userRepo.simulateListenSuccess(loadedList);

        // List loading should be false, but actionInFlight MUST REMAIN TRUE!
        assertFalse("List loading flag should be false after list loads", Boolean.TRUE.equals(vm.getLoading().getValue()));
        assertTrue("actionInFlight must NOT be reset by list listener callback!", Boolean.TRUE.equals(vm.getActionInFlight().getValue()));

        // A second click on lock button while action is still in-flight must be rejected!
        vm.lockUser("user-2", true);
        assertEquals("Double submit must be rejected even after list listener succeeds", 1, userRepo.lockCallCount);

        // Now resolve mutation
        userRepo.resolveLockSuccess();
        assertFalse(Boolean.TRUE.equals(vm.getActionInFlight().getValue()));
    }

    @Test
    public void userManagementViewModel_staleListenerCallback_doesNotOverwriteFreshState() {
        DefferableUserRepo userRepo = new DefferableUserRepo();
        AppUser admin = new AppUser("admin-uid", "admin", "admin@tdtu.edu.vn", "Admin", 30, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_ADMIN, null);
        UserManagementViewModel vm = new UserManagementViewModel(userRepo, admin);

        // Generation 1: start listening
        vm.startListening();
        assertEquals(1, userRepo.listenCallCount);

        // User taps Retry -> Generation 2 starts listening
        vm.retry();
        assertEquals(2, userRepo.listenCallCount);

        // Generation 2 receives fresh data first
        List<AppUser> freshUsers = new ArrayList<>();
        freshUsers.add(new AppUser("fresh-1", "fresh", "fresh@tdtu.edu.vn", "Fresh User", 25, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null));
        userRepo.simulateListenSuccessAtIndex(1, freshUsers);

        assertTrue(vm.getUserListState().getValue().isContent());
        assertEquals("fresh-1", vm.getUserListState().getValue().getData().get(0).getUid());

        // Stale callback from Generation 1 arrives LATER with old or empty list
        List<AppUser> staleOldUsers = new ArrayList<>();
        userRepo.simulateListenSuccessAtIndex(0, staleOldUsers);

        // Verify stale callback was discarded and did NOT overwrite fresh state!
        assertTrue("Fresh state must be preserved", vm.getUserListState().getValue().isContent());
        assertEquals("Stale callback must be ignored", "fresh-1", vm.getUserListState().getValue().getData().get(0).getUid());

        // Stale error from Generation 1 also does NOT overwrite fresh content
        userRepo.simulateListenErrorAtIndex(0, "Stale Network Timeout");
        assertTrue("Stale error must not overwrite fresh state", vm.getUserListState().getValue().isContent());
    }

    @Test
    public void loginHistoryViewModel_staleListenerCallback_doesNotOverwriteFreshState() {
        ControllableHistoryRepo historyRepo = new ControllableHistoryRepo();
        AppUser user = new AppUser("u1", "u1", "u1@tdtu.edu.vn", "User", 25, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null);
        LoginHistoryViewModel vm = new LoginHistoryViewModel(historyRepo, user, "u1");

        // Generation 1
        vm.startListening();
        assertEquals(1, historyRepo.listenCallCount);

        // Generation 2 via retry
        vm.retry();
        assertEquals(2, historyRepo.listenCallCount);

        // Generation 2 succeeds
        List<LoginRecord> freshRecords = new ArrayList<>();
        freshRecords.add(new LoginRecord("rec-fresh", 2000L, "Pixel 8", false));
        historyRepo.simulateSuccessAtIndex(1, freshRecords);

        assertEquals("rec-fresh", vm.getHistoryState().getValue().getData().get(0).getId());

        // Stale Generation 1 arrives later with error
        historyRepo.simulateErrorAtIndex(0, "Stale Connection Closed");

        // Assert fresh records are preserved and not clobbered by stale error
        assertTrue(vm.getHistoryState().getValue().isContent());
        assertEquals("rec-fresh", vm.getHistoryState().getValue().getData().get(0).getId());
    }

    @Test
    public void loginViewModel_outcome_isOneShot_andSurvivesRecreationWithoutDuplicateNavigation() {
        DefferableAuthRepo authRepo = new DefferableAuthRepo();
        LoginViewModel vm = new LoginViewModel(authRepo, null, "Pixel 7");

        vm.login("admin", "admin123");
        AppUser loggedInUser = new AppUser("admin-uid", "admin", "admin@tdtu.edu.vn", "Admin", 30, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_ADMIN, null);
        authRepo.resolveSuccess(loggedInUser);

        LoginViewModel.LoginOutcome outcome = vm.getLoginOutcome().getValue();
        assertNotNull(outcome);
        assertFalse(outcome.isConsumed());

        // First read by Activity (before navigation)
        AppUser consumedFirst = outcome.consumeUser();
        assertNotNull("First consume must return user", consumedFirst);
        assertEquals("admin-uid", consumedFirst.getUid());
        assertTrue(outcome.isConsumed());

        // Simulated Activity recreation (e.g. screen rotation / new observer attach):
        // Re-reading the same outcome must return null so navigation is NOT triggered again!
        AppUser consumedOnRecreation = outcome.consumeUser();
        assertNull("Subsequent consume after recreation must return null", consumedOnRecreation);
    }

    @Test
    public void profileViewModel_offlineError_surfacesLoadError_andCanRetryWithoutLosingCachedProfile() {
        DefferableProfileRepo profileRepo = new DefferableProfileRepo();
        AppUser user = new AppUser("user-1", "user1", "user1@tdtu.edu.vn", "User One", 20, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, "https://old/avatar.jpg");
        FakeAuthRepo authRepo = new FakeAuthRepo(user);
        ProfileViewModel vm = new ProfileViewModel(profileRepo, authRepo);

        vm.start(user);
        assertEquals(user, vm.getCurrentUser().getValue());
        assertNull(vm.getProfileLoadError().getValue());

        // Profile sync encounters offline network failure
        profileRepo.simulateProfileError("Mất kết nối mạng tới Firestore");

        // Cached profile must NOT be lost!
        assertEquals("Cached user must be retained on offline error", user, vm.getCurrentUser().getValue());
        assertEquals("Mất kết nối mạng tới Firestore", vm.getProfileLoadError().getValue());

        // Retry profile loading
        vm.retryProfileLoad();
        assertNull(vm.getProfileLoadError().getValue());

        // Reconnection succeeds with updated profile
        AppUser updated = new AppUser("user-1", "user1", "user1@tdtu.edu.vn", "User One Updated", 21, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, "https://old/avatar.jpg");
        profileRepo.simulateProfileSuccess(updated);

        assertEquals("User One Updated", vm.getCurrentUser().getValue().getName());
        assertNull(vm.getProfileLoadError().getValue());
    }

    @Test
    public void userManagementViewModel_userListState_4PhaseTransitions_workCorrectly() {
        DefferableUserRepo userRepo = new DefferableUserRepo();
        AppUser admin = new AppUser("admin-uid", "admin", "admin@tdtu.edu.vn", "Admin", 30, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_ADMIN, null);
        UserManagementViewModel vm = new UserManagementViewModel(userRepo, admin);

        // Phase 1: Loading
        vm.startListening();
        assertTrue(vm.getUserListState().getValue().isLoading());

        // Phase 2: Error
        userRepo.simulateListenError("Lỗi kết nối mạng");
        assertTrue(vm.getUserListState().getValue().isError());
        assertEquals("Lỗi kết nối mạng", vm.getUserListState().getValue().getErrorMessage());

        // Mutation error does NOT overwrite userListState error or content
        vm.lockUser("admin-uid", true); // Self-lock denied
        assertNotNull(vm.getActionErrorMessage().getValue());
        assertEquals("Lỗi kết nối mạng", vm.getUserListState().getValue().getErrorMessage());

        // Phase 3: Retry -> Empty
        vm.retry();
        assertTrue(vm.getUserListState().getValue().isLoading());
        userRepo.simulateListenSuccess(new ArrayList<>());
        assertTrue(vm.getUserListState().getValue().isEmpty());

        // Phase 4: Content
        List<AppUser> list = new ArrayList<>();
        list.add(admin);
        userRepo.simulateListenSuccess(list);
        assertTrue(vm.getUserListState().getValue().isContent());
        assertEquals(1, vm.getUserListState().getValue().getData().size());
    }

    // -------------------------------------------------------------
    // 4. Activity Recreation & Listener Cleanup
    // -------------------------------------------------------------

    @Test
    public void loginHistoryViewModel_onCleared_removesListenerCleanly() {
        ControllableHistoryRepo historyRepo = new ControllableHistoryRepo();
        AppUser user = new AppUser("u1", "u1", "u1@tdtu.edu.vn", "User", 25, "0901234567", AppUser.STATUS_NORMAL, AppUser.ROLE_EMPLOYEE, null);
        LoginHistoryViewModel vm = new LoginHistoryViewModel(historyRepo, user, "u1");

        vm.startListening();
        assertTrue(historyRepo.isListening);

        vm.onCleared();
        assertFalse("Listener must be removed when ViewModel is cleared", historyRepo.isListening);
    }

    // -------------------------------------------------------------
    // Fakes for testing
    // -------------------------------------------------------------

    static class DefferableAuthRepo implements AuthRepository {
        int signInCallCount = 0;
        AuthCallback pendingCallback;

        @Override
        public void signIn(String identifier, String password, AuthCallback callback) {
            signInCallCount++;
            this.pendingCallback = callback;
        }

        void resolveSuccess(AppUser user) {
            if (pendingCallback != null) pendingCallback.onSuccess(user);
        }

        void resolveError(String error) {
            if (pendingCallback != null) pendingCallback.onError(error);
        }

        @Override
        public AppUser getCurrentUser() { return null; }
        @Override
        public boolean isSignedIn() { return false; }
        @Override
        public void signOut() {}
    }

    static class DefferableUserRepo implements UserRepository {
        int lockCallCount = 0;
        int listenCallCount = 0;
        OperationCallback pendingLockCallback;
        final List<UserListCallback> allListCallbacks = new ArrayList<>();
        boolean isListening = false;

        @Override
        public ListenerHandle listenUsers(AppUser currentUser, UserListCallback callback) {
            listenCallCount++;
            isListening = true;
            this.allListCallbacks.add(callback);
            return () -> isListening = false;
        }

        void simulateListenError(String error) {
            if (!allListCallbacks.isEmpty()) {
                allListCallbacks.get(allListCallbacks.size() - 1).onError(error);
            }
        }

        void simulateListenSuccess(List<AppUser> users) {
            if (!allListCallbacks.isEmpty()) {
                allListCallbacks.get(allListCallbacks.size() - 1).onUsersLoaded(users);
            }
        }

        void simulateListenSuccessAtIndex(int index, List<AppUser> users) {
            allListCallbacks.get(index).onUsersLoaded(users);
        }

        void simulateListenErrorAtIndex(int index, String error) {
            allListCallbacks.get(index).onError(error);
        }

        @Override
        public void setUserLocked(AppUser currentUser, String uid, boolean locked, OperationCallback callback) {
            lockCallCount++;
            this.pendingLockCallback = callback;
        }

        void resolveLockSuccess() {
            if (pendingLockCallback != null) pendingLockCallback.onSuccess();
        }

        @Override
        public ListenerHandle listenUser(String uid, UserCallback callback) { return () -> {}; }
        @Override
        public void createUserProfile(AppUser currentUser, AppUser newUser, OperationCallback callback) {}
        @Override
        public void updateUserProfile(AppUser currentUser, String uid, String name, int age, String phone, String role, OperationCallback callback) {}
        @Override
        public void deleteUserProfile(AppUser currentUser, String uid, OperationCallback callback) {}
    }

    static class DefferableProfileRepo implements ProfileRepository {
        int uploadCallCount = 0;
        UploadCallback pendingUploadCallback;
        final List<ProfileCallback> allProfileCallbacks = new ArrayList<>();

        @Override
        public void uploadAvatar(String uid, byte[] imageData, String mimeType, UploadCallback callback) {
            uploadCallCount++;
            this.pendingUploadCallback = callback;
        }

        void resolveUploadSuccess(String url) {
            if (pendingUploadCallback != null) pendingUploadCallback.onSuccess(url);
        }

        @Override
        public ListenerHandle listenProfile(String uid, ProfileCallback callback) {
            allProfileCallbacks.add(callback);
            return () -> {};
        }

        void simulateProfileError(String error) {
            if (!allProfileCallbacks.isEmpty()) {
                allProfileCallbacks.get(allProfileCallbacks.size() - 1).onError(error);
            }
        }

        void simulateProfileSuccess(AppUser user) {
            if (!allProfileCallbacks.isEmpty()) {
                allProfileCallbacks.get(allProfileCallbacks.size() - 1).onProfileLoaded(user);
            }
        }

        @Override
        public void updateAvatarUrl(String uid, String avatarUrl, UpdateCallback callback) {
            if (callback != null) callback.onSuccess();
        }
    }

    static class FakeAuthRepo implements AuthRepository {
        private final AppUser user;
        FakeAuthRepo(AppUser user) { this.user = user; }
        @Override public void signIn(String id, String pwd, AuthCallback cb) {}
        @Override public AppUser getCurrentUser() { return user; }
        @Override public boolean isSignedIn() { return user != null; }
        @Override public void signOut() {}
    }

    static class ControllableHistoryRepo implements LoginHistoryRepository {
        int listenCallCount = 0;
        boolean isListening = false;
        final List<HistoryCallback> allCallbacks = new ArrayList<>();

        @Override
        public ListenerHandle listenLoginHistory(AppUser currentUser, String targetUid, HistoryCallback callback) {
            listenCallCount++;
            isListening = true;
            this.allCallbacks.add(callback);
            return () -> isListening = false;
        }

        void simulateError(String error) {
            if (!allCallbacks.isEmpty()) {
                allCallbacks.get(allCallbacks.size() - 1).onError(error);
            }
        }

        void simulateSuccess(List<LoginRecord> records) {
            if (!allCallbacks.isEmpty()) {
                allCallbacks.get(allCallbacks.size() - 1).onHistoryLoaded(records);
            }
        }

        void simulateSuccessAtIndex(int index, List<LoginRecord> records) {
            allCallbacks.get(index).onHistoryLoaded(records);
        }

        void simulateErrorAtIndex(int index, String error) {
            allCallbacks.get(index).onError(error);
        }

        @Override
        public void recordLogin(String uid, String device, RecordCallback callback) {}
    }
}
