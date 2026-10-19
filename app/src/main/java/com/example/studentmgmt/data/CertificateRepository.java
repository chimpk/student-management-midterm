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

    final class ExportRow {
        public final String studentId;
        public final Certificate certificate;
        public ExportRow(String studentId, Certificate certificate) {
            this.studentId = studentId;
            this.certificate = certificate;
        }
    }

    final class ImportItem {
        public final int dataRow;
        public final String studentId;
        public final Certificate certificate;
        public ImportItem(int dataRow, String studentId, Certificate certificate) {
            this.dataRow = dataRow;
            this.studentId = studentId;
            this.certificate = certificate;
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

    interface ExportCallback {
        void onSuccess(List<ExportRow> rows);
        void onError(String errorMessage);
    }

    interface ImportCallback {
        void onComplete(int importedCount, List<ImportError> errors);
        void onError(String errorMessage);
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

    /** Retrieves a one-time export snapshot for the selected students. */
    void getCertificatesForStudents(List<String> studentIds, ExportCallback callback);

    /** Imports new certificates with row-level error reporting. */
    void importCertificates(List<ImportItem> items, ImportCallback callback);
}
