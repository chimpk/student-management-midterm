package com.example.studentmgmt.data;

/**
 * Provides Firestore by default while allowing tests to inject a mock repository.
 */
public final class CertificateRepositoryProvider {

    private static volatile CertificateRepository instance;

    private CertificateRepositoryProvider() {}

    public static CertificateRepository getInstance() {
        if (instance == null) {
            synchronized (CertificateRepositoryProvider.class) {
                if (instance == null) {
                    instance = new FirestoreCertificateRepository();
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
