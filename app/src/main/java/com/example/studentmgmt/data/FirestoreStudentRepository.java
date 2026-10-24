package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.utils.StudentDataValidation;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Firestore repository for real-time student data and role-checked CRUD operations. */
public final class FirestoreStudentRepository implements StudentRepository {

    private static final int MAX_BATCH_WRITES = 400;

    private final FirebaseFirestore firestore;
    private final CollectionReference students;

    public FirestoreStudentRepository() {
        this(FirebaseFirestore.getInstance());
    }

    public FirestoreStudentRepository(FirebaseFirestore firestore) {
        if (firestore == null) {
            throw new IllegalArgumentException("Firestore không được null.");
        }
        this.firestore = firestore;
        this.students = firestore.collection("students");
    }

    @Override
    public ListenerRegistrationHandle listenStudents(StudentListCallback callback) {
        if (callback == null) {
            return () -> { };
        }

        List<Student> visibleStudents = new ArrayList<>();
        Map<String, ListenerRegistration> countListeners = new HashMap<>();
        // Keep counts separately from Student objects: each students snapshot creates new
        // Student instances, while the per-student certificate listeners remain registered.
        Map<String, Integer> certificateCounts = new HashMap<>();

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
                    certificateCounts.remove(existingId);
                }
            }

            for (Student student : result) {
                Integer count = certificateCounts.get(student.getStudentId());
                if (count != null) {
                    student.setCertificateCount(count);
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
                            certificateCounts.put(studentId, certificates.size());
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
        if (!canWrite()) {
            fail(callback, "Chỉ Admin hoặc Manager được quản lý sinh viên.");
            return;
        }
        String invalid = validate(student);
        if (invalid != null) {
            fail(callback, invalid);
            return;
        }

        DocumentReference studentRef = students.document(student.getStudentId().trim());
        firestore.runTransaction(transaction -> {
            if (transaction.get(studentRef).exists()) {
                throw new FirebaseFirestoreException("MSSV đã tồn tại.",
                        FirebaseFirestoreException.Code.ALREADY_EXISTS);
            }
            transaction.set(studentRef, studentFields(student, true));
            return null;
        }).addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
    }

    @Override
    public void updateStudent(Student student, ActionCallback callback) {
        if (!canWrite()) {
            fail(callback, "Chỉ Admin hoặc Manager được quản lý sinh viên.");
            return;
        }
        String invalid = validate(student);
        if (invalid != null) {
            fail(callback, invalid);
            return;
        }

        Map<String, Object> fields = studentFields(student, false);
        fields.remove("studentId");
        fields.remove("createdAt");
        students.document(student.getStudentId().trim()).update(fields)
                .addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
    }

    @Override
    public void deleteStudent(String studentId, ActionCallback callback) {
        if (!canWrite()) {
            fail(callback, "Chỉ Admin hoặc Manager được quản lý sinh viên.");
            return;
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            fail(callback, "MSSV không hợp lệ.");
            return;
        }

        DocumentReference studentRef = students.document(studentId.trim());
        studentRef.collection("certificates").get()
                .addOnSuccessListener(snapshot -> deleteCertificateBatches(
                        studentRef, snapshot, 0, callback))
                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
    }

    private void deleteCertificateBatches(DocumentReference studentRef, QuerySnapshot snapshot,
                                          int start, ActionCallback callback) {
        List<DocumentSnapshot> certificates = snapshot.getDocuments();
        if (start >= certificates.size()) {
            studentRef.delete()
                    .addOnSuccessListener(ignored -> succeed(callback))
                    .addOnFailureListener(error -> fail(callback, errorMessage(error)));
            return;
        }

        int end = Math.min(start + MAX_BATCH_WRITES, certificates.size());
        WriteBatch batch = firestore.batch();
        for (int index = start; index < end; index++) {
            batch.delete(certificates.get(index).getReference());
        }
        batch.commit()
                .addOnSuccessListener(ignored ->
                        deleteCertificateBatches(studentRef, snapshot, end, callback))
                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
    }

    private static Student toStudent(DocumentSnapshot document) {
        Object studentIdValue = document.get("studentId");
        Object nameValue = document.get("name");
        Object dateOfBirthValue = document.get("dateOfBirth");
        Object classNameValue = document.get("className");
        Object facultyValue = document.get("faculty");
        Object createdAtValue = document.get("createdAt",
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
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

    private static String validate(Student student) {
        if (student == null) {
            return "Dữ liệu sinh viên không hợp lệ.";
        }
        return StudentDataValidation.validateStudent(student.getStudentId(), student.getName(),
                student.getDateOfBirth(), student.getClassName(), student.getFaculty());
    }

    private static Map<String, Object> studentFields(Student student, boolean includeCreatedAt) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("studentId", student.getStudentId().trim());
        fields.put("name", student.getName().trim());
        fields.put("dateOfBirth", student.getDateOfBirth().trim());
        fields.put("className", student.getClassName().trim());
        fields.put("faculty", student.getFaculty().trim());
        if (includeCreatedAt) {
            fields.put("createdAt", FieldValue.serverTimestamp());
        }
        return fields;
    }

    private static boolean canWrite() {
        AppUser user = AuthRepositoryProvider.getRepository().getCurrentUser();
        return user != null && user.isNormal() && (user.isAdmin() || user.isManager());
    }

    private static String errorMessage(Exception error) {
        if (error instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException.Code code = ((FirebaseFirestoreException) error).getCode();
            if (code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                return "Bạn không có quyền thực hiện thao tác này.";
            }
            if (code == FirebaseFirestoreException.Code.ALREADY_EXISTS) {
                return "MSSV đã tồn tại trong hệ thống.";
            }
        }
        return error == null || error.getLocalizedMessage() == null
                ? "Không thể tải dữ liệu sinh viên từ Firestore."
                : error.getLocalizedMessage();
    }

    private static void succeed(ActionCallback callback) {
        if (callback != null) {
            callback.onSuccess();
        }
    }

    private static void fail(ActionCallback callback, String message) {
        if (callback != null) {
            callback.onError(message);
        }
    }
}
