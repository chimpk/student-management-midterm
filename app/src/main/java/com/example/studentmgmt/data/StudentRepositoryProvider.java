package com.example.studentmgmt.data;

/** Provides the Firestore repository by default while allowing tests to inject a mock. */
public final class StudentRepositoryProvider {

    private static volatile StudentRepository instance;

    private StudentRepositoryProvider() {}

    public static StudentRepository getInstance() {
        if (instance == null) {
            synchronized (StudentRepositoryProvider.class) {
                if (instance == null) {
                    instance = new FirestoreStudentRepository();
                }
            }
        }
        return instance;
    }

    public static void setInstance(StudentRepository repository) {
        synchronized (StudentRepositoryProvider.class) {
            instance = repository;
        }
    }
}
