package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Student;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        List<Student> visibleStudents = new ArrayList<>();
        Map<String, ListenerRegistration> countListeners = new HashMap<>();

        ListenerRegistration registration = students.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                callback.onError(errorMessage(error));
                return;
            }
            if (snapshot == null) {
                return;
            }

            List<Student> result = new ArrayList<>();
            Set<String> studentIds = new HashSet<>();
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                Student student = toStudent(document);
                if (student == null) {
                    callback.onError("Dữ liệu sinh viên không hợp lệ tại " + document.getId());
                    return;
                }
                result.add(student);
                studentIds.add(student.getStudentId());
            }

            for (String existingId : new HashSet<>(countListeners.keySet())) {
                if (!studentIds.contains(existingId)) {
                    countListeners.remove(existingId).remove();
                }
            }

            visibleStudents.clear();
            visibleStudents.addAll(result);
            callback.onSuccess(new ArrayList<>(visibleStudents));

            for (Student student : result) {
                String studentId = student.getStudentId();
                if (countListeners.containsKey(studentId)) {
                    continue;
                }
                ListenerRegistration countListener = students.document(studentId)
                        .collection("certificates")
                        .addSnapshotListener((certificates, countError) -> {
                            if (countError != null) {
                                callback.onError("Không thể tải số chứng chỉ của " + studentId
                                        + ": " + errorMessage(countError));
                                return;
                            }
                            if (certificates == null) {
                                return;
                            }
                            for (Student current : visibleStudents) {
                                if (studentId.equals(current.getStudentId())) {
                                    current.setCertificateCount(certificates.size());
                                    callback.onSuccess(new ArrayList<>(visibleStudents));
                                    break;
                                }
                            }
                        });
                countListeners.put(studentId, countListener);
            }
        });
        return () -> {
            registration.remove();
            for (ListenerRegistration countListener : countListeners.values()) {
                countListener.remove();
            }
            countListeners.clear();
            visibleStudents.clear();
        };
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
        Object studentIdValue = document.get("studentId");
        Object nameValue = document.get("name");
        Object dateOfBirthValue = document.get("dateOfBirth");
        Object classNameValue = document.get("className");
        Object facultyValue = document.get("faculty");
        Object createdAtValue = document.get("createdAt");
        if (!(studentIdValue instanceof String)
                || !(nameValue instanceof String)
                || !(dateOfBirthValue instanceof String)
                || !(classNameValue instanceof String)
                || !(facultyValue instanceof String)
                || !(createdAtValue instanceof Timestamp)) {
            return null;
        }

        String studentId = (String) studentIdValue;
        String name = (String) nameValue;
        String dateOfBirth = (String) dateOfBirthValue;
        String className = (String) classNameValue;
        String faculty = (String) facultyValue;
        Timestamp createdAt = (Timestamp) createdAtValue;
        if (studentId == null || studentId.trim().isEmpty()
                || !studentId.equals(document.getId())
                || name == null || name.trim().isEmpty()
                || dateOfBirth == null || dateOfBirth.trim().isEmpty()
                || className == null || className.trim().isEmpty()
                || faculty == null || faculty.trim().isEmpty()
                || createdAt == null) {
            return null;
        }

        long createdAtMillis = createdAt.toDate().getTime();
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
