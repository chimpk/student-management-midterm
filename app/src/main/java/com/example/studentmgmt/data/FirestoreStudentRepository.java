package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Student;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

/** Firestore-backed read repository for the Sprint 1 student list and detail screens. */
public final class FirestoreStudentRepository implements StudentRepository {

    private final CollectionReference students;

    public FirestoreStudentRepository() {
        this(FirebaseFirestore.getInstance());
    }

    public FirestoreStudentRepository(FirebaseFirestore firestore) {
        if (firestore == null) {
            throw new IllegalArgumentException("Firestore không được null.");
        }
        this.students = firestore.collection("students");
    }

    @Override
    public ListenerRegistrationHandle listenStudents(StudentListCallback callback) {
        if (callback == null) {
            return () -> { };
        }

        ListenerRegistration registration = students.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                callback.onError(errorMessage(error));
                return;
            }
            if (snapshot == null) {
                return;
            }

            List<Student> result = new ArrayList<>();
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                Student student = toStudent(document);
                if (student == null) {
                    callback.onError("Dữ liệu sinh viên không hợp lệ tại " + document.getId());
                    return;
                }
                result.add(student);
            }
            callback.onSuccess(result);
        });
        return registration::remove;
    }

    @Override
    public void getStudent(String studentId, SingleStudentCallback callback) {
        if (callback == null) {
            return;
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            callback.onError("MSSV không hợp lệ.");
            return;
        }

        students.document(studentId.trim()).get()
                .addOnSuccessListener(document -> {
                    Student student = document.exists() ? toStudent(document) : null;
                    if (student == null) {
                        callback.onError("Không tìm thấy sinh viên: " + studentId);
                    } else {
                        callback.onSuccess(student);
                    }
                })
                .addOnFailureListener(error -> callback.onError(errorMessage(error)));
    }

    @Override
    public void addStudent(Student student, ActionCallback callback) {
        reportWriteUnavailable(callback);
    }

    @Override
    public void updateStudent(Student student, ActionCallback callback) {
        reportWriteUnavailable(callback);
    }

    @Override
    public void deleteStudent(String studentId, ActionCallback callback) {
        reportWriteUnavailable(callback);
    }

    private static Student toStudent(DocumentSnapshot document) {
        String studentId = document.getString("studentId");
        if (studentId == null || studentId.trim().isEmpty()) {
            studentId = document.getId();
        }

        String name = document.getString("name");
        String dateOfBirth = document.getString("dateOfBirth");
        String className = document.getString("className");
        String faculty = document.getString("faculty");
        if (name == null || dateOfBirth == null || className == null || faculty == null) {
            return null;
        }

        Timestamp createdAt = document.getTimestamp("createdAt");
        long createdAtMillis = createdAt == null ? 0L : createdAt.toDate().getTime();
        return new Student(studentId, name, dateOfBirth, className, faculty, createdAtMillis, 0);
    }

    private static String errorMessage(Exception error) {
        return error == null || error.getLocalizedMessage() == null
                ? "Không thể tải dữ liệu sinh viên từ Firestore."
                : error.getLocalizedMessage();
    }

    private static void reportWriteUnavailable(ActionCallback callback) {
        if (callback != null) {
            callback.onError("Thêm, sửa và xóa sinh viên sẽ được triển khai ở Sprint 2.");
        }
    }
}
