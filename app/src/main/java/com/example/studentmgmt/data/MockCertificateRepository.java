package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Certificate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TODO(member-2): Placeholder in-memory implementation for CertificateRepository.
 * Member 2 will replace or supplement this with FirestoreCertificateRepository for live backend sync.
 */
public class MockCertificateRepository implements CertificateRepository {

    private final Map<String, List<Certificate>> certMap = new ConcurrentHashMap<>();
    private final Map<String, List<CertificateListCallback>> listenerMap = new ConcurrentHashMap<>();

    public MockCertificateRepository() {
        seedSampleCertificates();
    }

    private void seedSampleCertificates() {
        List<Certificate> certs1 = new CopyOnWriteArrayList<>();
        certs1.add(new Certificate("c1", "TOEIC 750", "2024-05-10", "IIG Vietnam"));
        certs1.add(new Certificate("c2", "MOS Excel 2019", "2023-11-20", "Certiport"));
        certMap.put("524H0001", certs1);

        List<Certificate> certs2 = new CopyOnWriteArrayList<>();
        certs2.add(new Certificate("c3", "IELTS 6.5", "2024-03-15", "British Council"));
        certMap.put("524H0002", certs2);

        List<Certificate> certs3 = new CopyOnWriteArrayList<>();
        certs3.add(new Certificate("c4", "TOEIC 800", "2024-06-01", "IIG Vietnam"));
        certs3.add(new Certificate("c5", "AWS Cloud Practitioner", "2024-08-12", "Amazon Web Services"));
        certs3.add(new Certificate("c6", "MOS Word 2019", "2023-10-05", "Certiport"));
        certMap.put("524H0003", certs3);
    }

    @Override
    public ListenerRegistrationHandle listenCertificates(String studentId, CertificateListCallback callback) {
        if (studentId == null || callback == null) return () -> {};

        listenerMap.computeIfAbsent(studentId, k -> new CopyOnWriteArrayList<>()).add(callback);
        List<Certificate> list = certMap.computeIfAbsent(studentId, k -> new CopyOnWriteArrayList<>());
        callback.onSuccess(new ArrayList<>(list));

        return () -> {
            List<CertificateListCallback> listCallbacks = listenerMap.get(studentId);
            if (listCallbacks != null) {
                listCallbacks.remove(callback);
            }
        };
    }

    @Override
    public void addCertificate(String studentId, Certificate certificate, ActionCallback callback) {
        if (studentId == null || certificate == null) {
            if (callback != null) callback.onError("Dữ liệu chứng chỉ không hợp lệ.");
            return;
        }

        if (certificate.getCertId() == null || certificate.getCertId().trim().isEmpty()) {
            certificate.setCertId("cert_" + System.currentTimeMillis());
        }

        List<Certificate> list = certMap.computeIfAbsent(studentId, k -> new CopyOnWriteArrayList<>());
        list.add(certificate);
        notifyListeners(studentId);
        if (callback != null) callback.onSuccess();
    }

    @Override
    public void updateCertificate(String studentId, Certificate certificate, ActionCallback callback) {
        if (studentId == null || certificate == null || certificate.getCertId() == null) {
            if (callback != null) callback.onError("Dữ liệu cập nhật chứng chỉ không hợp lệ.");
            return;
        }

        List<Certificate> list = certMap.get(studentId);
        if (list == null) {
            if (callback != null) callback.onError("Không tìm thấy danh sách chứng chỉ.");
            return;
        }

        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCertId().equals(certificate.getCertId())) {
                list.set(i, certificate);
                found = true;
                break;
            }
        }

        if (found) {
            notifyListeners(studentId);
            if (callback != null) callback.onSuccess();
        } else {
            if (callback != null) callback.onError("Không tìm thấy chứng chỉ để cập nhật.");
        }
    }

    @Override
    public void deleteCertificate(String studentId, String certId, ActionCallback callback) {
        if (studentId == null || certId == null) {
            if (callback != null) callback.onError("Tham số không hợp lệ.");
            return;
        }

        List<Certificate> list = certMap.get(studentId);
        if (list != null) {
            boolean removed = list.removeIf(c -> c.getCertId().equals(certId));
            if (removed) {
                notifyListeners(studentId);
                if (callback != null) callback.onSuccess();
                return;
            }
        }
        if (callback != null) callback.onError("Không tìm thấy chứng chỉ cần xóa.");
    }

    @Override
    public void getCertificatesForStudents(List<String> studentIds, ExportCallback callback) {
        if (callback == null) return;
        List<ExportRow> result = new ArrayList<>();
        if (studentIds != null) for (String studentId : studentIds) {
            List<Certificate> certificates = certMap.get(studentId);
            if (certificates != null) for (Certificate certificate : certificates) {
                result.add(new ExportRow(studentId, certificate));
            }
        }
        callback.onSuccess(result);
    }

    @Override
    public void importCertificates(List<ImportItem> items, ImportCallback callback) {
        List<ImportError> errors = new ArrayList<>();
        int[] imported = {0};
        importNext(items == null ? new ArrayList<>() : items, 0, imported, errors, callback);
    }

    private void importNext(List<ImportItem> items, int index, int[] imported,
                            List<ImportError> errors, ImportCallback callback) {
        if (index >= items.size()) {
            if (callback != null) callback.onComplete(imported[0], errors);
            return;
        }
        ImportItem item = items.get(index);
        addCertificate(item.studentId, item.certificate, new ActionCallback() {
            @Override public void onSuccess() {
                imported[0]++;
                importNext(items, index + 1, imported, errors, callback);
            }
            @Override public void onError(String message) {
                errors.add(new ImportError(item.dataRow, item.studentId, message));
                importNext(items, index + 1, imported, errors, callback);
            }
        });
    }

    private void notifyListeners(String studentId) {
        List<CertificateListCallback> listCallbacks = listenerMap.get(studentId);
        if (listCallbacks != null) {
            List<Certificate> copy = new ArrayList<>(certMap.computeIfAbsent(studentId, k -> new CopyOnWriteArrayList<>()));
            for (CertificateListCallback cb : listCallbacks) {
                cb.onSuccess(copy);
            }
        }
    }
}
