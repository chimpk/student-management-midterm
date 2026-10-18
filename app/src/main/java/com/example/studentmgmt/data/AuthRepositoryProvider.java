package com.example.studentmgmt.data;

/**
 * Service locator / provider for the shared AuthRepository instance.
 */
public final class AuthRepositoryProvider {

    private static AuthRepository instance;

    private AuthRepositoryProvider() {
        throw new AssertionError("Utility class");
    }

    public static synchronized AuthRepository getRepository() {
        if (instance == null) {
            instance = new FirebaseAuthRepository();
        }
        return instance;
    }

    public static synchronized void setRepository(AuthRepository repository) {
        instance = repository;
    }

    public static synchronized void reset() {
        instance = null;
    }
}
