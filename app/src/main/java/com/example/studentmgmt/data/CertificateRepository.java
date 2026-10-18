package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Certificate;

import java.util.List;

/**
 * TODO(member-2): Certificate repository interface for Member 2 (Nguyễn Vĩnh Khâm).
 */
public interface CertificateRepository {

    interface CertificateListCallback {
        void onSuccess(List<Certificate> certificates);
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
     * Listens to real-time certificates in subcollection "students/{studentId}/certificates".
     */
    ListenerRegistrationHandle listenCertificates(String studentId, CertificateListCallback callback);

    /**
     * Adds a certificate for the given student.
     */
    void addCertificate(String studentId, Certificate certificate, ActionCallback callback);

    /**
     * Updates an existing certificate.
     */
    void updateCertificate(String studentId, Certificate certificate, ActionCallback callback);

    /**
     * Deletes a certificate.
     */
    void deleteCertificate(String studentId, String certId, ActionCallback callback);
}
