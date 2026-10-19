package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Certificate;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.utils.StudentDataValidation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** Firestore-backed implementation for students/{studentId}/certificates. */
public final class FirestoreCertificateRepository implements CertificateRepository {
    private final FirebaseFirestore db;

    public FirestoreCertificateRepository() {
        this(FirebaseFirestore.getInstance());
    }

    public FirestoreCertificateRepository(FirebaseFirestore db) {
        if (db == null) throw new IllegalArgumentException("Firestore không được null.");
        this.db = db;
    }

    private CollectionReference certificates(String studentId) {
        return db.collection("students").document(studentId).collection("certificates");
    }

    @Override
    public ListenerRegistrationHandle listenCertificates(String studentId,
                                                          CertificateListCallback callback) {
        if (studentId == null || studentId.trim().isEmpty() || callback == null) return () -> {};
        ListenerRegistration registration = certificates(studentId.trim())
                .orderBy("issueDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        callback.onError(message(error));
                    } else if (snapshot != null) {
                        List<Certificate> result = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            String name = doc.getString("certName");
                            String date = doc.getString("issueDate");
                            String issuer = doc.getString("issuedBy");
                            if (name == null || date == null || issuer == null) {
                                callback.onError("Dữ liệu chứng chỉ không hợp lệ: " + doc.getId());
                                return;
                            }
                            result.add(new Certificate(doc.getId(), name, date, issuer));
                        }
                        callback.onSuccess(result);
                    }
                });
        return registration::remove;
    }

    @Override
    public void addCertificate(String studentId, Certificate certificate, ActionCallback callback) {
        if (!canWrite()) { fail(callback, "Chỉ Admin hoặc Manager được quản lý chứng chỉ."); return; }
        String error = validate(studentId, certificate);
        if (error != null) { fail(callback, error); return; }
        certificates(studentId.trim()).document().set(fields(certificate))
                .addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(failure -> fail(callback, message(failure)));
    }

    @Override
    public void updateCertificate(String studentId, Certificate certificate, ActionCallback callback) {
        if (!canWrite()) { fail(callback, "Chỉ Admin hoặc Manager được quản lý chứng chỉ."); return; }
        String error = validate(studentId, certificate);
        if (error != null) { fail(callback, error); return; }
        if (certificate.getCertId() == null || certificate.getCertId().trim().isEmpty()) {
            fail(callback, "Thiếu mã chứng chỉ để cập nhật.");
            return;
        }
        certificates(studentId.trim()).document(certificate.getCertId().trim())
                .update(fields(certificate))
                .addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(failure -> fail(callback, message(failure)));
    }

    @Override
    public void deleteCertificate(String studentId, String certId, ActionCallback callback) {
        if (!canWrite()) { fail(callback, "Chỉ Admin hoặc Manager được quản lý chứng chỉ."); return; }
        if (studentId == null || studentId.trim().isEmpty()
                || certId == null || certId.trim().isEmpty()) {
            fail(callback, "Thông tin chứng chỉ không hợp lệ.");
            return;
        }
        certificates(studentId.trim()).document(certId.trim()).delete()
                .addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(failure -> fail(callback, message(failure)));
    }

    @Override
    public void getCertificatesForStudents(List<String> studentIds, ExportCallback callback) {
        if (callback == null) return;
        if (studentIds == null || studentIds.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }
        for (String studentId : studentIds) {
            if (studentId == null || studentId.trim().isEmpty()) {
                callback.onError("MSSV không hợp lệ khi xuất chứng chỉ.");
                return;
            }
        }
        List<String> uniqueIds = new ArrayList<>(new HashSet<>(studentIds));
        List<ExportRow> result = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger remaining = new AtomicInteger(uniqueIds.size());
        AtomicBoolean failed = new AtomicBoolean(false);
        for (String studentId : uniqueIds) {
            certificates(studentId.trim()).get().addOnSuccessListener(snapshot -> {
                if (failed.get()) return;
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    String name = doc.getString("certName");
                    String date = doc.getString("issueDate");
                    String issuer = doc.getString("issuedBy");
                    if (name == null || date == null || issuer == null) {
                        if (failed.compareAndSet(false, true)) callback.onError("Dữ liệu chứng chỉ không hợp lệ: " + doc.getId());
                        return;
                    }
                    result.add(new ExportRow(studentId, new Certificate(doc.getId(), name, date, issuer)));
                }
                if (remaining.decrementAndGet() == 0 && !failed.get()) callback.onSuccess(new ArrayList<>(result));
            }).addOnFailureListener(error -> {
                if (failed.compareAndSet(false, true)) callback.onError(message(error));
            });
        }
    }

    @Override
    public void importCertificates(List<ImportItem> items, ImportCallback callback) {
        if (!canWrite()) {
            if (callback != null) callback.onError("Chỉ Admin hoặc Manager được nhập chứng chỉ.");
            return;
        }
        List<ImportItem> source = items == null ? new ArrayList<>() : items;
        if (source.isEmpty()) {
            if (callback != null) callback.onComplete(0, new ArrayList<>());
            return;
        }
        db.collection("students").get().addOnSuccessListener(snapshot -> {
            Set<String> knownStudents = new HashSet<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) knownStudents.add(doc.getId());
            List<ImportItem> accepted = new ArrayList<>();
            List<ImportError> errors = new ArrayList<>();
            for (ImportItem item : source) {
                String invalid = item == null ? "Dòng không có dữ liệu chứng chỉ."
                        : validate(item.studentId, item.certificate);
                if (invalid == null && !knownStudents.contains(item.studentId.trim())) {
                    invalid = "Không tìm thấy sinh viên có MSSV này.";
                }
                if (invalid != null) {
                    errors.add(new ImportError(item == null ? 0 : item.dataRow,
                            item == null || item.studentId == null ? "" : item.studentId, invalid));
                } else accepted.add(item);
            }
            commitCertificateImportChunks(accepted, 0, 0, errors, callback);
        }).addOnFailureListener(error -> {
            if (callback != null) callback.onError(message(error));
        });
    }

    private void commitCertificateImportChunks(List<ImportItem> items, int start, int imported,
                                               List<ImportError> errors, ImportCallback callback) {
        if (start >= items.size()) {
            if (callback != null) callback.onComplete(imported, errors);
            return;
        }
        int end = Math.min(start + 400, items.size());
        List<ImportItem> chunk = new ArrayList<>(items.subList(start, end));
        com.google.firebase.firestore.WriteBatch batch = db.batch();
        for (ImportItem item : chunk) {
            batch.set(certificates(item.studentId.trim()).document(), fields(item.certificate));
        }
        int importedBeforeChunk = imported;
        batch.commit().addOnSuccessListener(ignored ->
                commitCertificateImportChunks(items, end, importedBeforeChunk + chunk.size(), errors, callback))
                .addOnFailureListener(error -> {
                    String reason = message(error);
                    for (ImportItem item : chunk) errors.add(new ImportError(item.dataRow, item.studentId, reason));
                    commitCertificateImportChunks(items, end, importedBeforeChunk, errors, callback);
                });
    }

    private static String validate(String studentId, Certificate certificate) {
        if (studentId == null || studentId.trim().isEmpty()) return "MSSV không hợp lệ.";
        if (certificate == null) return "Dữ liệu chứng chỉ không hợp lệ.";
        return StudentDataValidation.validateCertificate(certificate.getCertName(),
                certificate.getIssueDate(), certificate.getIssuedBy());
    }

    private static Map<String, Object> fields(Certificate certificate) {
        Map<String, Object> data = new HashMap<>();
        data.put("certName", certificate.getCertName().trim());
        data.put("issueDate", certificate.getIssueDate().trim());
        data.put("issuedBy", certificate.getIssuedBy().trim());
        return data;
    }

    private static String message(Exception error) {
        if (error instanceof FirebaseFirestoreException
                && ((FirebaseFirestoreException) error).getCode()
                == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
            return "Bạn không có quyền thực hiện thao tác này.";
        }
        return error == null || error.getLocalizedMessage() == null
                ? "Không thể hoàn tất thao tác chứng chỉ." : error.getLocalizedMessage();
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
