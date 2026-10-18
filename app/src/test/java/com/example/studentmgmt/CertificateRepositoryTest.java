package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.example.studentmgmt.data.CertificateRepository;
import com.example.studentmgmt.data.MockCertificateRepository;
import com.example.studentmgmt.model.Certificate;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit tests for CertificateRepository (Mock implementation for Member 2).
 */
public class CertificateRepositoryTest {

    private MockCertificateRepository repository;

    @Before
    public void setUp() {
        repository = new MockCertificateRepository();
    }

    @Test
    public void listenCertificates_returnsCertificatesForStudent() {
        List<Certificate> loaded = new ArrayList<>();
        CertificateRepository.ListenerRegistrationHandle handle = repository.listenCertificates("524H0001", new CertificateRepository.CertificateListCallback() {
            @Override
            public void onSuccess(List<Certificate> certificates) {
                loaded.addAll(certificates);
            }

            @Override
            public void onError(String errorMessage) {
                fail("Should not fail");
            }
        });

        assertNotNull(handle);
        assertEquals(2, loaded.size());
        handle.remove();
    }

    @Test
    public void addCertificate_addsCertificateAndAutoGeneratesId() {
        Certificate cert = new Certificate(null, "JLPT N3", "2024-07-07", "Japan Foundation");
        AtomicBoolean success = new AtomicBoolean(false);

        repository.addCertificate("524H0001", cert, new CertificateRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                success.set(true);
            }

            @Override
            public void onError(String errorMessage) {
                fail("Add cert should succeed");
            }
        });

        assertTrue(success.get());
        assertNotNull("certId must be auto-generated when null", cert.getCertId());

        repository.listenCertificates("524H0001", new CertificateRepository.CertificateListCallback() {
            @Override
            public void onSuccess(List<Certificate> certificates) {
                assertEquals(3, certificates.size());
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    @Test
    public void updateCertificate_updatesExistingCertificate() {
        Certificate updated = new Certificate("c1", "TOEIC 850 (Đã thi lại)", "2024-09-01", "IIG Vietnam");
        AtomicBoolean success = new AtomicBoolean(false);

        repository.updateCertificate("524H0001", updated, new CertificateRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                success.set(true);
            }

            @Override
            public void onError(String errorMessage) {
                fail("Update cert should succeed");
            }
        });

        assertTrue(success.get());
    }

    @Test
    public void deleteCertificate_removesCertificate() {
        AtomicBoolean success = new AtomicBoolean(false);
        repository.deleteCertificate("524H0001", "c1", new CertificateRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                success.set(true);
            }

            @Override
            public void onError(String errorMessage) {
                fail("Delete cert should succeed");
            }
        });

        assertTrue(success.get());

        repository.listenCertificates("524H0001", new CertificateRepository.CertificateListCallback() {
            @Override
            public void onSuccess(List<Certificate> certificates) {
                assertEquals(1, certificates.size());
                assertEquals("c2", certificates.get(0).getCertId());
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }
}
