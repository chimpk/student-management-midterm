package com.example.studentmgmt.data;

/**
 * Strategy interface to map username or email alias to Firebase Auth identity.
 * Isolates identifier resolution without requiring live backend or hardcoded credentials.
 */
public interface UsernameAuthResolver {

    /**
     * Resolves identifier (username or email alias) to the email expected by Firebase Auth.
     *
     * @param identifier user-entered identifier (e.g., "admin" or "admin@studentmgmt.edu.vn")
     * @return Firebase Auth email string, or null if unresolvable
     */
    String resolveAuthEmail(String identifier);
}
