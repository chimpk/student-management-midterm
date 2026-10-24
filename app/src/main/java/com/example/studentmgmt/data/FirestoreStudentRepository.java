package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.Student;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Firestore-backed repository for student management and certificate cascades. */
public final class FirestoreStudentRepository implements StudentRepository {

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
        if (!canWrite()) {
            fail(callback, "Chỉ Admin hoặc Manager được quản lý sinh viên.");
            return;
        }
        String validationError = validate(student);
        if (validationError != null) {
            fail(callback, validationError);
            return;
        }

        String studentId = student.getStudentId().trim();
        students.document(studentId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        fail(callback, "Sinh viên với MSSV " + studentId + " đã tồn tại.");
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("studentId", studentId);
                        data.put("name", student.getName().trim());
                        data.put("dateOfBirth", student.getDateOfBirth().trim());
                        data.put("className", student.getClassName().trim());
                        data.put("faculty", student.getFaculty().trim());
                        data.put("createdAt", FieldValue.serverTimestamp());

                        students.document(studentId).set(data)
                                .addOnSuccessListener(aVoid -> succeed(callback))
                                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
                    }
                })
                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
    }

    @Override
    public void updateStudent(Student student, ActionCallback callback) {
        if (!canWrite()) {
            fail(callback, "Chỉ Admin hoặc Manager được quản lý sinh viên.");
            return;
        }
        String validationError = validate(student);
        if (validationError != null) {
            fail(callback, validationError);
            return;
        }

        String studentId = student.getStudentId().trim();
        Map<String, Object> data = new HashMap<>();
        data.put("name", student.getName().trim());
        data.put("dateOfBirth", student.getDateOfBirth().trim());
        data.put("className", student.getClassName().trim());
        data.put("faculty", student.getFaculty().trim());

        students.document(studentId).update(data)
                .addOnSuccessListener(aVoid -> succeed(callback))
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

        final String cleanId = studentId.trim();
        students.document(cleanId).collection("certificates").get()
                .addOnSuccessListener(certsSnapshot -> {
                    WriteBatch batch = firestore.batch();
                    if (certsSnapshot != null) {
                        for (DocumentSnapshot certDoc : certsSnapshot.getDocuments()) {
                            batch.delete(certDoc.getReference());
                        }
                    }
                    batch.delete(students.document(cleanId));
                    batch.commit()
                            .addOnSuccessListener(aVoid -> succeed(callback))
                            .addOnFailureListener(error -> fail(callback, errorMessage(error)));
                })
                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
    }

    private static String validate(Student student) {
        if (student == null) {
            return "Thông tin sinh viên không được để trống.";
        }
        String studentId = student.getStudentId();
        if (studentId == null || studentId.trim().isEmpty() || studentId.trim().length() < 3 || studentId.trim().length() > 30) {
            return "MSSV không hợp lệ (độ dài từ 3 đến 30 ký tự).";
        }
        String name = student.getName();
        if (name == null || name.trim().length() < 2 || name.trim().length() > 100) {
            return "Họ và tên sinh viên phải dài từ 2 đến 100 ký tự.";
        }
        String dob = student.getDateOfBirth();
        if (dob == null || !dob.trim().matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
            return "Ngày sinh phải theo định dạng YYYY-MM-DD.";
        }
        String className = student.getClassName();
        if (className == null || className.trim().isEmpty() || className.trim().length() > 50) {
            return "Lớp sinh hoạt không hợp lệ.";
        }
        String faculty = student.getFaculty();
        if (faculty == null || faculty.trim().isEmpty() || faculty.trim().length() > 100) {
            return "Khoa không hợp lệ.";
        }
        return null;
    }

    private static boolean canWrite() {
        AppUser user = AuthRepositoryProvider.getRepository().getCurrentUser();
        return user != null && user.isNormal() && (user.isAdmin() || user.isManager());
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
        if (error instanceof FirebaseFirestoreException
                && ((FirebaseFirestoreException) error).getCode()
                == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
            return "Bạn không có quyền thực hiện thao tác sinh viên.";
        }
        return error == null || error.getLocalizedMessage() == null
                ? "Không thể hoàn tất thao tác sinh viên trên Firestore."
                : error.getLocalizedMessage();
    }
}
