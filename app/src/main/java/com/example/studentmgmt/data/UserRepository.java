package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.ui.navigation.SessionSecurityPolicy;

import java.util.List;

/**
 * Repository interface for User Management operations.
 * Handles realtime user list streaming, profile updates, lock/unlock status toggling,
 * and user document deletion under strict RBAC checks.
 */
public interface UserRepository {

    interface ListenerHandle {
        void remove();
    }

    interface UserListCallback {
        void onUsersLoaded(List<AppUser> users);
        void onError(String errorMessage);
    }

    interface UserCallback {
        void onUserLoaded(AppUser user);
        void onError(SessionSecurityPolicy.SessionErrorType errorType, String errorMessage);
    }

    interface OperationCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    /**
     * Listens to the realtime collection of users.
     * Admin only. Non-admin results in immediate error.
     */
    ListenerHandle listenUsers(AppUser currentUser, UserListCallback callback);

    /**
     * Listens to a single user document in realtime (e.g. for session status monitoring).
     * Returns typed SessionErrorType so listeners can safely distinguish terminal revocations
     * from transient network glitches.
     */
    ListenerHandle listenUser(String uid, UserCallback callback);

    /**
     * Creates a new user document in Firestore for a pre-provisioned trusted UID.
     * Enforces create-only precondition to reject existing UIDs.
     * Note: Creating Firebase Auth credentials requires trusted backend/Admin SDK (not client).
     */
    void createUserProfile(AppUser currentUser, AppUser newUser, OperationCallback callback);

    /**
     * Updates an existing user's mutable profile fields (name, age, phone, role).
     */
    void updateUserProfile(AppUser currentUser, String uid, String name, int age, String phone, String role, OperationCallback callback);

    /**
     * Toggles a user's status between Normal and Locked.
     * Prevents self-locking.
     */
    void setUserLocked(AppUser currentUser, String uid, boolean locked, OperationCallback callback);

    /**
     * Deletes a user document from Firestore.
     * Note: Firebase Auth account is not deleted from client SDK.
     * Prevents self-deletion.
     */
    void deleteUserProfile(AppUser currentUser, String uid, OperationCallback callback);
}
