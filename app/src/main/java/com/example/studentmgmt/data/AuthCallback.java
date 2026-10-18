package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;

/**
 * Callback for asynchronous authentication operations.
 */
public interface AuthCallback {
    void onSuccess(AppUser user);
    void onError(String message);
}
