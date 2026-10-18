package com.example.studentmgmt.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.LoginHistoryRepository;
import com.example.studentmgmt.data.LoginHistoryRepositoryProvider;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.LoginRecord;
import com.example.studentmgmt.model.UiState;

import java.util.List;

/**
 * ViewModel managing login history records for a target user.
 * Enforces RBAC checks: Admin can view any user's history, non-admin only their own.
 * Uses canonical UiState (LOADING, CONTENT, EMPTY, ERROR).
 * Discards stale callbacks from previous listener generations on retry.
 * Prevents listener duplication and removes listener on cleared.
 */
public class LoginHistoryViewModel extends ViewModel {

    private final LoginHistoryRepository repository;
    private final AppUser currentUser;
    private final String targetUid;

    private final MutableLiveData<List<LoginRecord>> records = new MutableLiveData<>(null);
    private final MutableLiveData<UiState<List<LoginRecord>>> historyState = new MutableLiveData<>(UiState.loading());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private LoginHistoryRepository.ListenerHandle listenerHandle;
    private long listenerGeneration = 0;

    public LoginHistoryViewModel() {
        this(LoginHistoryRepositoryProvider.getRepository(),
                AuthRepositoryProvider.getRepository().getCurrentUser(),
                AuthRepositoryProvider.getRepository().getCurrentUser() != null ?
                        AuthRepositoryProvider.getRepository().getCurrentUser().getUid() : null);
    }

    public LoginHistoryViewModel(LoginHistoryRepository repository, AppUser currentUser, String targetUid) {
        this.repository = repository;
        this.currentUser = currentUser;
        this.targetUid = targetUid != null ? targetUid : (currentUser != null ? currentUser.getUid() : null);
    }

    public LiveData<List<LoginRecord>> getRecords() {
        return records;
    }

    public LiveData<UiState<List<LoginRecord>>> getHistoryState() {
        return historyState;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public String getTargetUid() {
        return targetUid;
    }

    public boolean isSelfHistory() {
        return currentUser != null && currentUser.getUid().equals(targetUid);
    }

    public void startListening() {
        final long token = ++listenerGeneration;
        if (listenerHandle != null) {
            listenerHandle.remove();
            listenerHandle = null;
        }

        if (currentUser == null || targetUid == null) {
            errorMessage.setValue("Phiên làm việc hoặc tài khoản không hợp lệ.");
            historyState.setValue(UiState.error("Phiên làm việc hoặc tài khoản không hợp lệ."));
            return;
        }

        if (!LoginHistoryPolicy.canViewLoginHistory(currentUser, targetUid)) {
            errorMessage.setValue("Bạn không có quyền xem lịch sử đăng nhập của người dùng này.");
            historyState.setValue(UiState.error("Bạn không có quyền xem lịch sử đăng nhập của người dùng này."));
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);
        historyState.setValue(UiState.loading());

        listenerHandle = repository.listenLoginHistory(currentUser, targetUid, new LoginHistoryRepository.HistoryCallback() {
            @Override
            public void onHistoryLoaded(List<LoginRecord> loadedRecords) {
                if (token != listenerGeneration) {
                    return; // Stale callback from older listener generation, discard
                }
                isLoading.postValue(false);
                errorMessage.postValue(null);
                records.postValue(loadedRecords);
                if (loadedRecords == null || loadedRecords.isEmpty()) {
                    historyState.postValue(UiState.empty());
                } else {
                    historyState.postValue(UiState.content(loadedRecords));
                }
            }

            @Override
            public void onError(String error) {
                if (token != listenerGeneration) {
                    return; // Stale callback from older listener generation, discard
                }
                isLoading.postValue(false);
                errorMessage.postValue(error);
                historyState.postValue(UiState.error(error));
            }
        });
    }

    public void retry() {
        if (listenerHandle != null) {
            listenerHandle.remove();
            listenerHandle = null;
        }
        startListening();
    }

    @Override
    public void onCleared() {
        super.onCleared();
        if (listenerHandle != null) {
            listenerHandle.remove();
            listenerHandle = null;
        }
    }
}
