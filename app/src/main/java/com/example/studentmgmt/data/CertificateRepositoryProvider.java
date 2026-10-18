package com.example.studentmgmt.data;

/**
 * TODO(member-2): Provider for CertificateRepository.
 */
public final class CertificateRepositoryProvider {

    private static volatile CertificateRepository instance;

    private CertificateRepositoryProvider() {}

    public static CertificateRepository getInstance() {
        if (instance == null) {
            synchronized (CertificateRepositoryProvider.class) {
                if (instance == null) {
                    instance = new MockCertificateRepository();
                }
            }
        }
        return instance;
    }

    public static void setInstance(CertificateRepository repository) {
        synchronized (CertificateRepositoryProvider.class) {
            instance = repository;
        }
    }
}
