package com.example.studentmgmt.data;

/**
 * Service locator / provider for the shared UserRepository instance.
 */
public final class UserRepositoryProvider {

    private static UserRepository instance;

    private UserRepositoryProvider() {
        throw new AssertionError("Utility class");
    }

    public static synchronized UserRepository getRepository() {
        if (instance == null) {
            instance = new FirestoreUserRepository();
        }
        return instance;
    }

    public static synchronized void setRepository(UserRepository repository) {
        instance = repository;
    }

    public static synchronized void reset() {
        instance = null;
    }
}
