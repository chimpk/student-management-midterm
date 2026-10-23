package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.Certificate;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Firestore implementation for students/{studentId}/certificates. */
public final class FirestoreCertificateRepository implements CertificateRepository {

    private final FirebaseFirestore firestore;

    public FirestoreCertificateRepository() {
        this(FirebaseFirestore.getInstance());
    }

    public FirestoreCertificateRepository(FirebaseFirestore firestore) {
        if (firestore == null) {
            throw new IllegalArgumentException("Firestore không được null.");
        }
        this.firestore = firestore;
    }

    private CollectionReference certificates(String studentId) {
        return firestore.collection("students").document(studentId).collection("certificates");
    }

    @Override
    public ListenerRegistrationHandle listenCertificates(String studentId,
                                                          CertificateListCallback callback) {
        if (studentId == null || studentId.trim().isEmpty() || callback == null) {
            return () -> { };
        }

        ListenerRegistration registration = certificates(studentId.trim())
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        callback.onError(errorMessage(error));
                        return;
                    }
                    if (snapshot == null) {
                        return;
                    }

                    List<Certificate> result = new ArrayList<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        Object nameValue = document.get("certName");
                        Object issueDateValue = document.get("issueDate");
                        Object issuedByValue = document.get("issuedBy");
                        if (!(nameValue instanceof String)
                                || !(issueDateValue instanceof String)
                                || !(issuedByValue instanceof String)) {
                            callback.onError("Dữ liệu chứng chỉ sai kiểu trường: "
                                    + document.getId());
                            return;
                        }
                        String name = (String) nameValue;
                        String issueDate = (String) issueDateValue;
                        String issuedBy = (String) issuedByValue;
                        if (isBlank(name) || isBlank(issueDate) || isBlank(issuedBy)) {
                            callback.onError("Dữ liệu chứng chỉ thiếu trường bắt buộc: "
                                    + document.getId());
                            return;
                        }
                        result.add(new Certificate(document.getId(), name, issueDate, issuedBy));
                    }
                    Collections.sort(result, (left, right) ->
                            right.getIssueDate().compareTo(left.getIssueDate()));
                    callback.onSuccess(result);
                });
        return registration::remove;
    }

    @Override
    public void addCertificate(String studentId, Certificate certificate, ActionCallback callback) {
        if (!canWrite()) {
            fail(callback, "Chỉ Admin hoặc Manager được quản lý chứng chỉ.");
            return;
        }
        String validationError = validate(studentId, certificate);
        if (validationError != null) {
            fail(callback, validationError);
            return;
        }

        certificates(studentId.trim()).document().set(toFields(certificate))
                .addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
    }

    @Override
    public void updateCertificate(String studentId, Certificate certificate, ActionCallback callback) {
        if (!canWrite()) {
            fail(callback, "Chỉ Admin hoặc Manager được quản lý chứng chỉ.");
            return;
        }
        String validationError = validate(studentId, certificate);
        if (validationError != null) {
            fail(callback, validationError);
            return;
        }
        if (isBlank(certificate.getCertId())) {
            fail(callback, "Thiếu mã chứng chỉ để cập nhật.");
            return;
        }

        certificates(studentId.trim()).document(certificate.getCertId().trim())
                .update(toFields(certificate))
                .addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
    }

    @Override
    public void deleteCertificate(String studentId, String certId, ActionCallback callback) {
        if (!canWrite()) {
            fail(callback, "Chỉ Admin hoặc Manager được quản lý chứng chỉ.");
            return;
        }
        if (isBlank(studentId) || isBlank(certId)) {
            fail(callback, "Thông tin chứng chỉ không hợp lệ.");
            return;
        }

        certificates(studentId.trim()).document(certId.trim()).delete()
                .addOnSuccessListener(ignored -> succeed(callback))
                .addOnFailureListener(error -> fail(callback, errorMessage(error)));
    }

    private static String validate(String studentId, Certificate certificate) {
        if (isBlank(studentId)) {
            return "MSSV không hợp lệ.";
        }
        if (certificate == null || isBlank(certificate.getCertName())
                || isBlank(certificate.getIssueDate()) || isBlank(certificate.getIssuedBy())) {
            return "Tên, ngày cấp và đơn vị cấp chứng chỉ đều bắt buộc.";
        }
        String date = certificate.getIssueDate().trim();
        if (!date.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
            return "Ngày cấp phải theo định dạng YYYY-MM-DD.";
        }
        if (certificate.getCertName().trim().length() < 2
                || certificate.getCertName().trim().length() > 100
                || certificate.getIssuedBy().trim().length() < 2
                || certificate.getIssuedBy().trim().length() > 100) {
            return "Tên chứng chỉ và đơn vị cấp phải dài từ 2 đến 100 ký tự.";
        }
        return null;
    }

    private static Map<String, Object> toFields(Certificate certificate) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("certName", certificate.getCertName().trim());
        fields.put("issueDate", certificate.getIssueDate().trim());
        fields.put("issuedBy", certificate.getIssuedBy().trim());
        return fields;
    }

    private static boolean canWrite() {
        AppUser user = AuthRepositoryProvider.getRepository().getCurrentUser();
        return user != null && user.isNormal() && (user.isAdmin() || user.isManager());
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String errorMessage(Exception error) {
        if (error instanceof FirebaseFirestoreException
                && ((FirebaseFirestoreException) error).getCode()
                == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
            return "Bạn không có quyền đọc hoặc ghi chứng chỉ.";
        }
        return error == null || error.getLocalizedMessage() == null
                ? "Không thể hoàn tất thao tác chứng chỉ trên Firestore."
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
