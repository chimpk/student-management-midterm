package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Student;

import java.util.ArrayList;
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

    final class ImportItem {
        public final int dataRow;
        public final Student student;
        public ImportItem(int dataRow, Student student) {
            this.dataRow = dataRow;
            this.student = student;
        }
    }

    final class ImportError {
        public final int dataRow;
        public final String studentId;
        public final String message;
        public ImportError(int dataRow, String studentId, String message) {
            this.dataRow = dataRow;
            this.studentId = studentId;
            this.message = message;
        }
    }

    interface ImportCallback {
        void onComplete(int importedCount, List<ImportError> errors);
        void onError(String errorMessage);
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

    /** Imports accepted CSV rows without overwriting existing IDs; implementations chunk writes to <=400. */
    void importStudents(List<ImportItem> items, ImportCallback callback);
}
