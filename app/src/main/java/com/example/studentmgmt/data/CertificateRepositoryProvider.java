package com.example.studentmgmt.data;

/** Provides the Firestore certificate repository by default while allowing mock injection. */
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
