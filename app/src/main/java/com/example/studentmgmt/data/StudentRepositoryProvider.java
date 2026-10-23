package com.example.studentmgmt.data;

/**
 * TODO(member-2): Provider for StudentRepository.
 * Member 2 can inject FirestoreStudentRepository or switch implementation here.
 */
public final class StudentRepositoryProvider {

    private static volatile StudentRepository instance;

    private StudentRepositoryProvider() {}

    public static StudentRepository getInstance() {
        if (instance == null) {
            synchronized (StudentRepositoryProvider.class) {
                if (instance == null) {
                    instance = new MockStudentRepository();
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
