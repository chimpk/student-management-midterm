package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Student;

import java.util.List;

/**
 * TODO(member-2): Student repository interface for Member 2 (Nguyễn Vĩnh Khâm).
 */
public interface StudentRepository {

    interface StudentListCallback {
        void onSuccess(List<Student> students);
        void onError(String errorMessage);
    }

    interface SingleStudentCallback {
        void onSuccess(Student student);
        void onError(String errorMessage);
    }

    interface ActionCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    interface ListenerRegistrationHandle {
        void remove();
    }

    /**
     * Listens to real-time updates of the students collection.
     */
    ListenerRegistrationHandle listenStudents(StudentListCallback callback);

    /**
     * Gets a single student by ID.
     */
    void getStudent(String studentId, SingleStudentCallback callback);

    /**
     * Adds a new student document to Firestore.
     */
    void addStudent(Student student, ActionCallback callback);

    /**
     * Updates an existing student document.
     */
    void updateStudent(Student student, ActionCallback callback);

    /**
     * Deletes a student and associated certificates.
     */
    void deleteStudent(String studentId, ActionCallback callback);
}
