package com.example.studentmgmt.ui.auth;

import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studentmgmt.data.AuthCallback;
import com.example.studentmgmt.data.AuthRepository;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.LoginHistoryRepository;
import com.example.studentmgmt.data.LoginHistoryRepositoryProvider;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.history.LoginHistoryPolicy;

/**
 * ViewModel handling authentication logic for LoginActivity.
 * Automatically records login history upon successful authentication of active users.
 */
public class LoginViewModel extends ViewModel {

    // TODO(auth-backend): Re-enable email alias prompt once trusted resolver backend is connected.
    public static final String ERROR_EMPTY_IDENTIFIER = "Vui lòng nhập tên đăng nhập.";
    public static final String ERROR_EMPTY_PASSWORD = "Vui lòng nhập mật khẩu.";
    public static final String ERROR_PASSWORD_TOO_SHORT = "Mật khẩu phải có ít nhất 6 ký tự.";

    public static final class LoginOutcome {
        private final AppUser user;
        private final String warningMessage;
        private boolean isConsumed = false;

        public LoginOutcome(AppUser user, String warningMessage) {
            this.user = user;
            this.warningMessage = warningMessage;
        }

        public AppUser getUser() {
            return user;
        }

        public synchronized AppUser consumeUser() {
            if (isConsumed) {
                return null;
            }
            isConsumed = true;
            return user;
        }

        public synchronized boolean isConsumed() {
            return isConsumed;
        }

        public String getWarningMessage() {
            return warningMessage;
        }

        public boolean hasWarning() {
            return warningMessage != null && !warningMessage.trim().isEmpty();
        }
    }

    private final AuthRepository authRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private String deviceName;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<String> warningMessage = new MutableLiveData<>(null);
    private final MutableLiveData<AppUser> loginSuccess = new MutableLiveData<>(null);
    private final MutableLiveData<LoginOutcome> loginOutcome = new MutableLiveData<>(null);

    public LoginViewModel() {
        this(AuthRepositoryProvider.getRepository(), LoginHistoryRepositoryProvider.getRepository());
    }

    public LoginViewModel(AuthRepository authRepository) {
        this(authRepository, LoginHistoryRepositoryProvider.getRepository());
    }

    public LoginViewModel(AuthRepository authRepository, LoginHistoryRepository loginHistoryRepository) {
        this(authRepository, loginHistoryRepository, resolveDefaultDeviceName());
    }

    public LoginViewModel(AuthRepository authRepository, LoginHistoryRepository loginHistoryRepository, String deviceName) {
        this.authRepository = authRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.deviceName = deviceName;
    }

    private static String resolveDefaultDeviceName() {
        try {
            String manufacturer = Build.MANUFACTURER != null ? Build.MANUFACTURER : "";
            String model = Build.MODEL != null ? Build.MODEL : "";
            String full = (manufacturer + " " + model).trim();
            return full.isEmpty() ? "Android Device" : full;
        } catch (Exception e) {
            return "Android Device";
        }
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getWarningMessage() {
        return warningMessage;
    }

    public LiveData<AppUser> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<LoginOutcome> getLoginOutcome() {
        return loginOutcome;
    }

    public void clearError() {
        errorMessage.setValue(null);
        warningMessage.setValue(null);
        loginSuccess.setValue(null);
        loginOutcome.setValue(null);
    }

    private void finalizeLogin(AppUser user, String warning) {
        if (warning != null) {
            warningMessage.setValue(warning);
        }
        isLoading.setValue(false);
        loginSuccess.setValue(user);
        loginOutcome.setValue(new LoginOutcome(user, warning));
    }

    public void login(String identifier, String password) {
        if (Boolean.TRUE.equals(isLoading.getValue())) {
            // Guard against duplicate authentication requests while in flight
            return;
        }

        clearError();

        if (identifier == null || identifier.trim().isEmpty()) {
            errorMessage.setValue(ERROR_EMPTY_IDENTIFIER);
            return;
        }

        if (password == null || password.isEmpty()) {
            errorMessage.setValue(ERROR_EMPTY_PASSWORD);
            return;
        }

        if (password.length() < 6) {
            errorMessage.setValue(ERROR_PASSWORD_TOO_SHORT);
            return;
        }

        isLoading.setValue(true);

        authRepository.signIn(identifier.trim(), password, new AuthCallback() {
            @Override
            public void onSuccess(AppUser user) {
                // Record login history asynchronously if allowed by policy
                if (LoginHistoryPolicy.shouldRecordLogin(true, user)) {
                    if (loginHistoryRepository != null) {
                        loginHistoryRepository.recordLogin(user.getUid(), deviceName, new LoginHistoryRepository.RecordCallback() {
                            @Override
                            public void onSuccess() {
                                finalizeLogin(user, null);
                            }

                            @Override
                            public void onError(String message) {
                                // Non-fatal: do NOT invalidate a valid user session due to history recording error,
                                // but surface a distinct warning so it is combined with the outcome before navigation.
                                String warning = "Không thể lưu lịch sử đăng nhập: " + (message != null ? message : "Lỗi không xác định");
                                finalizeLogin(user, warning);
                            }
                        });
                        return;
                    }
                }

                finalizeLogin(user, null);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message != null ? message : AuthRepository.ERROR_INVALID_CREDENTIALS);
            }
        });
    }
}
