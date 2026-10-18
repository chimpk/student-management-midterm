package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;

/**
 * Authentication repository interface.
 */
public interface AuthRepository {

    String ERROR_INVALID_CREDENTIALS = "Tên đăng nhập hoặc mật khẩu không chính xác.";
    String ERROR_ACCOUNT_LOCKED = "Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.";
    String ERROR_NETWORK = "Lỗi kết nối mạng. Vui lòng thử lại sau.";
    String ERROR_UNKNOWN = "Đã xảy ra lỗi trong quá trình xác thực.";

    /**
     * Signs in with either username or email alias and password.
     *
     * @param identifier username or email
     * @param password password
     * @param callback callback to receive result
     */
    void signIn(String identifier, String password, AuthCallback callback);

    /**
     * Signs out current authenticated user.
     */
    void signOut();

    /**
     * Returns currently authenticated user, or null if not authenticated.
     */
    AppUser getCurrentUser();

    /**
     * Returns true if user is currently signed in.
     */
    boolean isSignedIn();
}
