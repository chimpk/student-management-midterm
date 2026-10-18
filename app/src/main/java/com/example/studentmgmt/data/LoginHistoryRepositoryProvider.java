package com.example.studentmgmt.data;

/**
 * Service locator / provider for the shared LoginHistoryRepository instance.
 */
public final class LoginHistoryRepositoryProvider {

    private static LoginHistoryRepository instance;

    private LoginHistoryRepositoryProvider() {
        throw new AssertionError("Utility class");
    }

    public static synchronized LoginHistoryRepository getRepository() {
        if (instance == null) {
            instance = new FirebaseLoginHistoryRepository();
        }
        return instance;
    }

    public static synchronized void setRepository(LoginHistoryRepository repository) {
        instance = repository;
    }

    public static synchronized void reset() {
        instance = null;
    }
}
