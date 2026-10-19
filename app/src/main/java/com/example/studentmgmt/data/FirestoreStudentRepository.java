package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.utils.StudentDataValidation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/** Firestore-backed implementation for students/{studentId}. */
public final class FirestoreStudentRepository implements StudentRepository {
    private static final int MAX_BATCH_WRITES = 400;
    private final FirebaseFirestore db;
    private final CollectionReference students;

    public FirestoreStudentRepository() {
        this(FirebaseFirestore.getInstance());
    }

    public FirestoreStudentRepository(FirebaseFirestore db) {
        if (db == null) throw new IllegalArgumentException("Firestore không được null.");
        this.db = db;
        this.students = db.collection("students");
    }

    @Override
    public ListenerRegistrationHandle listenStudents(StudentListCallback callback) {
        if (callback == null) return () -> {};
        ListenerRegistration registration = students.orderBy("studentId")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        callback.onError(message(error));
                    } else if (snapshot != null) {
                        List<Student> result = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Student student = fromDocument(doc);
                            if (student == null) {
                                callback.onError("Dữ liệu sinh viên không hợp lệ tại " + doc.getId());
                                return;
                            }
                            result.add(student);
                        }
                        callback.onSuccess(result);
                    }
                });
        return registration::remove;
    }

    @Override
    public void getStudent(String studentId, SingleStudentCallback callback) {
        if (callback == null) return;
        if (studentId == null || studentId.trim().isEmpty()) {
            callback.onError("MSSV không hợp lệ.");
            return;
        }
        students.document(studentId.trim()).get().addOnSuccessListener(doc -> {
            Student student = doc.exists() ? fromDocument(doc) : null;
            if (student == null) callback.onError("Không tìm thấy sinh viên: " + studentId);
            else callback.onSuccess(student);
        }).addOnFailureListener(error -> callback.onError(message(error)));
    }

    @Override
    public void addStudent(Student student, ActionCallback callback) {
        if (!canWrite()) { fail(callback, "Chỉ Admin hoặc Manager được quản lý sinh viên."); return; }
        String invalid = validate(student);
        if (invalid != null) { fail(callback, invalid); return; }
        DocumentReference ref = students.document(student.getStudentId().trim());
        db.runTransaction(transaction -> {
            if (transaction.get(ref).exists()) {
                throw new FirebaseFirestoreException("MSSV đã tồn tại.",
                        FirebaseFirestoreException.Code.ALREADY_EXISTS);
            }
            transaction.set(ref, studentFields(student, true));
            return null;
        }).addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(error -> fail(callback, message(error)));
    }

    @Override
    public void updateStudent(Student student, ActionCallback callback) {
        if (!canWrite()) { fail(callback, "Chỉ Admin hoặc Manager được quản lý sinh viên."); return; }
        String invalid = validate(student);
        if (invalid != null) { fail(callback, invalid); return; }
        Map<String, Object> fields = studentFields(student, false);
        fields.remove("studentId");
        fields.remove("createdAt");
        students.document(student.getStudentId().trim()).update(fields)
                .addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(error -> fail(callback, message(error)));
    }

    @Override
    public void deleteStudent(String studentId, ActionCallback callback) {
        if (!canWrite()) { fail(callback, "Chỉ Admin hoặc Manager được quản lý sinh viên."); return; }
        if (studentId == null || studentId.trim().isEmpty()) {
            fail(callback, "MSSV không hợp lệ.");
            return;
        }
        DocumentReference studentRef = students.document(studentId.trim());
        studentRef.collection("certificates").get().addOnSuccessListener(snapshot ->
                deleteCertificatesThenStudent(studentRef, snapshot, callback))
                .addOnFailureListener(error -> fail(callback, message(error)));
    }

    @Override
    public void importStudents(List<ImportItem> items, ImportCallback callback) {
        if (!canWrite()) {
            if (callback != null) callback.onError("Chỉ Admin hoặc Manager được nhập sinh viên.");
            return;
        }
        if (items == null || items.isEmpty()) {
            if (callback != null) callback.onComplete(0, new ArrayList<>());
            return;
        }

        List<ImportItem> candidates = new ArrayList<>();
        List<ImportError> errors = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (ImportItem item : items) {
            if (item == null || item.student == null) {
                errors.add(new ImportError(item == null ? 0 : item.dataRow, "", "Dòng không có dữ liệu sinh viên."));
                continue;
            }
            Student student = item.student;
            String invalid = validate(student);
            String id = student.getStudentId() == null ? "" : student.getStudentId().trim();
            if (invalid == null && !seen.add(id)) invalid = "MSSV bị lặp trong tệp.";
            if (invalid != null) errors.add(new ImportError(item.dataRow, id, invalid));
            else candidates.add(item);
        }

        students.get().addOnSuccessListener(snapshot -> {
            Set<String> existing = new HashSet<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) existing.add(doc.getId());
            List<ImportItem> accepted = new ArrayList<>();
            for (ImportItem item : candidates) {
                String id = item.student.getStudentId().trim();
                if (existing.contains(id)) {
                    errors.add(new ImportError(item.dataRow, id, "MSSV đã tồn tại trong Firestore."));
                } else {
                    existing.add(id);
                    accepted.add(item);
                }
            }
            commitStudentImportChunks(accepted, 0, 0, errors, callback);
        }).addOnFailureListener(error -> {
            if (callback != null) callback.onError(message(error));
        });
    }

    private void commitStudentImportChunks(List<ImportItem> items, int start, int imported,
                                           List<ImportError> errors, ImportCallback callback) {
        if (start >= items.size()) {
            if (callback != null) callback.onComplete(imported, errors);
            return;
        }
        int end = Math.min(start + MAX_BATCH_WRITES, items.size());
        List<ImportItem> chunk = new ArrayList<>(items.subList(start, end));
        WriteBatch batch = db.batch();
        for (ImportItem item : chunk) {
            Student student = item.student;
            batch.set(students.document(student.getStudentId().trim()), studentFields(student, true));
        }
        int importedBeforeChunk = imported;
        batch.commit().addOnSuccessListener(ignored ->
                commitStudentImportChunks(items, end, importedBeforeChunk + chunk.size(), errors, callback))
                .addOnFailureListener(error -> {
                    String reason = message(error);
                    for (ImportItem item : chunk) {
                        errors.add(new ImportError(item.dataRow, item.student.getStudentId(), reason));
                    }
                    commitStudentImportChunks(items, end, importedBeforeChunk, errors, callback);
                });
    }

    private void deleteCertificatesThenStudent(DocumentReference studentRef, QuerySnapshot snapshot,
                                               ActionCallback callback) {
        List<DocumentReference> refs = new ArrayList<>();
        snapshot.getDocuments().forEach(doc -> refs.add(doc.getReference()));
        Task<Void> chain = Tasks.forResult(null);
        for (int start = 0; start < refs.size(); start += MAX_BATCH_WRITES) {
            int end = Math.min(start + MAX_BATCH_WRITES, refs.size());
            List<DocumentReference> chunk = new ArrayList<>(refs.subList(start, end));
            chain = chain.continueWithTask(task -> {
                if (!task.isSuccessful()) return Tasks.forException(task.getException());
                WriteBatch batch = db.batch();
                for (DocumentReference ref : chunk) batch.delete(ref);
                return batch.commit();
            });
        }
        chain.continueWithTask(task -> {
            if (!task.isSuccessful()) return Tasks.forException(task.getException());
            return studentRef.delete();
        }).addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(error -> fail(callback, message(error)));
    }

    private static String validate(Student student) {
        if (student == null) return "Dữ liệu sinh viên không hợp lệ.";
        return StudentDataValidation.validateStudent(student.getStudentId(), student.getName(),
                student.getDateOfBirth(), student.getClassName(), student.getFaculty());
    }

    private static Map<String, Object> studentFields(Student student, boolean includeCreatedAt) {
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", student.getStudentId().trim());
        data.put("name", student.getName().trim());
        data.put("dateOfBirth", student.getDateOfBirth().trim());
        data.put("className", student.getClassName().trim());
        data.put("faculty", student.getFaculty().trim());
        if (includeCreatedAt) data.put("createdAt", FieldValue.serverTimestamp());
        return data;
    }

    private static Student fromDocument(DocumentSnapshot doc) {
        String id = doc.getString("studentId");
        if (id == null) id = doc.getId();
        String name = doc.getString("name");
        String dob = doc.getString("dateOfBirth");
        String className = doc.getString("className");
        String faculty = doc.getString("faculty");
        if (name == null || dob == null || className == null || faculty == null) return null;
        Timestamp createdAt = doc.getTimestamp("createdAt");
        return new Student(id, name, dob, className, faculty,
                createdAt == null ? 0L : createdAt.toDate().getTime(), 0);
    }

    private static String message(Exception error) {
        if (error instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException firestoreError = (FirebaseFirestoreException) error;
            if (firestoreError.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                return "Bạn không có quyền thực hiện thao tác này.";
            }
            if (firestoreError.getCode() == FirebaseFirestoreException.Code.ALREADY_EXISTS) {
                return "MSSV đã tồn tại trong hệ thống.";
            }
        }
        return error == null || error.getLocalizedMessage() == null
                ? "Không thể hoàn tất thao tác Firestore." : error.getLocalizedMessage();
    }

    private static void succeed(ActionCallback callback) {
        if (callback != null) callback.onSuccess();
    }

    private static void fail(ActionCallback callback, String error) {
        if (callback != null) callback.onError(error);
    }

    private static boolean canWrite() {
        AppUser user = AuthRepositoryProvider.getRepository().getCurrentUser();
        return user != null && (user.isAdmin() || user.isManager()) && user.isNormal();
    }
}
