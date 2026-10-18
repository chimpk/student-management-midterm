package com.example.studentmgmt.ui.user;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studentmgmt.data.UserRepository;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.UiState;

import java.util.List;

/**
 * ViewModel for User Management screens.
 * Manages user list streaming, status changes (lock/unlock), updates, and user document deletion.
 * Uses canonical UiState for user list and separates actionInFlight from list loading.
 * Tracks listener generations to discard stale callbacks.
 * Cleans up listeners on onCleared() to avoid memory leaks.
 */
public class UserManagementViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final AppUser currentUser;

    private final MutableLiveData<List<AppUser>> users = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<AppUser>>> userListState = new MutableLiveData<>(UiState.loading());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> actionInFlight = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> actionErrorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> actionSuccessMessage = new MutableLiveData<>();

    private UserRepository.ListenerHandle listenerHandle;
    private long listenerGeneration = 0;

    public UserManagementViewModel(UserRepository userRepository, AppUser currentUser) {
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    public LiveData<List<AppUser>> getUsers() {
        return users;
    }

    public LiveData<UiState<List<AppUser>>> getUserListState() {
        return userListState;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<Boolean> getActionInFlight() {
        return actionInFlight;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getActionErrorMessage() {
        return actionErrorMessage;
    }

    public LiveData<String> getActionSuccessMessage() {
        return actionSuccessMessage;
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    public void clearActionError() {
        actionErrorMessage.setValue(null);
    }

    public void clearSuccess() {
        actionSuccessMessage.setValue(null);
    }

    /**
     * Starts listening to user collection updates in realtime.
     * Generational token ensures any stale callback from an older listener is safely discarded.
     */
    public void startListening() {
        final long token = ++listenerGeneration;
        if (listenerHandle != null) {
            listenerHandle.remove();
            listenerHandle = null;
        }

        loading.setValue(true);
        userListState.setValue(UiState.loading());
        listenerHandle = userRepository.listenUsers(currentUser, new UserRepository.UserListCallback() {
            @Override
            public void onUsersLoaded(List<AppUser> loadedUsers) {
                if (token != listenerGeneration) {
                    return; // Stale callback from previous listener generation, discard
                }
                loading.setValue(false);
                users.setValue(loadedUsers);
                if (loadedUsers == null || loadedUsers.isEmpty()) {
                    userListState.setValue(UiState.empty());
                } else {
                    userListState.setValue(UiState.content(loadedUsers));
                }
            }

            @Override
            public void onError(String error) {
                if (token != listenerGeneration) {
                    return; // Stale callback from previous listener generation, discard
                }
                loading.setValue(false);
                errorMessage.setValue(error);
                userListState.setValue(UiState.error(error));
            }
        });
    }

    /**
     * Retries loading users after an error.
     */
    public void retry() {
        startListening();
    }

    /**
     * Toggles locked status for target user.
     * actionInFlight prevents duplicate concurrent requests.
     */
    public void lockUser(String targetUid, boolean locked) {
        if (Boolean.TRUE.equals(actionInFlight.getValue())) {
            return;
        }
        if (!UserManagementPolicy.canLockUser(currentUser, targetUid)) {
            actionErrorMessage.setValue("Không thể tự khóa tài khoản của chính mình hoặc không có quyền Admin.");
            errorMessage.setValue("Không thể tự khóa tài khoản của chính mình hoặc không có quyền Admin.");
            return;
        }

        actionInFlight.setValue(true);
        userRepository.setUserLocked(currentUser, targetUid, locked, new UserRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                actionInFlight.setValue(false);
                actionSuccessMessage.setValue(locked ? "Đã khóa tài khoản thành công." : "Đã mở khóa tài khoản thành công.");
            }

            @Override
            public void onError(String error) {
                actionInFlight.setValue(false);
                actionErrorMessage.setValue(error);
                errorMessage.setValue(error);
            }
        });
    }

    /**
     * Deletes user document from Firestore.
     * actionInFlight prevents duplicate concurrent requests.
     */
    public void deleteUser(String targetUid) {
        if (Boolean.TRUE.equals(actionInFlight.getValue())) {
            return;
        }
        if (!UserManagementPolicy.canDeleteUser(currentUser, targetUid)) {
            actionErrorMessage.setValue("Không thể tự xóa tài khoản của chính mình hoặc không có quyền Admin.");
            errorMessage.setValue("Không thể tự xóa tài khoản của chính mình hoặc không có quyền Admin.");
            return;
        }

        actionInFlight.setValue(true);
        userRepository.deleteUserProfile(currentUser, targetUid, new UserRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                actionInFlight.setValue(false);
                actionSuccessMessage.setValue("Đã xóa hồ sơ người dùng thành công (Lưu ý: tài khoản Auth chưa bị xóa).");
            }

            @Override
            public void onError(String error) {
                actionInFlight.setValue(false);
                actionErrorMessage.setValue(error);
                errorMessage.setValue(error);
            }
        });
    }

    /**
     * Creates new user document in Firestore.
     * actionInFlight prevents duplicate concurrent requests.
     */
    public void createUser(AppUser newUser) {
        if (Boolean.TRUE.equals(actionInFlight.getValue())) {
            return;
        }
        actionInFlight.setValue(true);
        userRepository.createUserProfile(currentUser, newUser, new UserRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                actionInFlight.setValue(false);
                actionSuccessMessage.setValue("Đã tạo hồ sơ người dùng thành công.");
            }

            @Override
            public void onError(String error) {
                actionInFlight.setValue(false);
                actionErrorMessage.setValue(error);
                errorMessage.setValue(error);
            }
        });
    }

    /**
     * Updates existing user profile fields.
     * actionInFlight prevents duplicate concurrent requests.
     */
    public void updateUser(String uid, String name, int age, String phone, String role) {
        if (Boolean.TRUE.equals(actionInFlight.getValue())) {
            return;
        }
        actionInFlight.setValue(true);
        userRepository.updateUserProfile(currentUser, uid, name, age, phone, role, new UserRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                actionInFlight.setValue(false);
                actionSuccessMessage.setValue("Cập nhật thông tin người dùng thành công.");
            }

            @Override
            public void onError(String error) {
                actionInFlight.setValue(false);
                actionErrorMessage.setValue(error);
                errorMessage.setValue(error);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listenerHandle != null) {
            listenerHandle.remove();
            listenerHandle = null;
        }
    }

    @VisibleForTesting
    public void clearViewModel() {
        onCleared();
    }
}
