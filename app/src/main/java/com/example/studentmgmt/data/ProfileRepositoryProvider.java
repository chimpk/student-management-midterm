package com.example.studentmgmt.data;

/**
 * Service locator / provider for the shared ProfileRepository instance.
 */
public final class ProfileRepositoryProvider {

    private static ProfileRepository instance;

    private ProfileRepositoryProvider() {
        throw new AssertionError("Utility class");
    }

    public static synchronized ProfileRepository getRepository() {
        if (instance == null) {
            instance = new FirebaseProfileRepository();
        }
        return instance;
    }

    public static synchronized void setRepository(ProfileRepository repository) {
        instance = repository;
    }

    public static synchronized void reset() {
        instance = null;
    }
}
