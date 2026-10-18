package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;

import com.example.studentmgmt.data.MockCertificateRepository;
import com.example.studentmgmt.data.MockStudentRepository;
import com.example.studentmgmt.model.Certificate;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.UiState;
import com.example.studentmgmt.ui.student.StudentDetailViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for StudentDetailViewModel (Member 2).
 */
public class StudentDetailViewModelTest {

    private MockStudentRepository studentRepo;
    private MockCertificateRepository certRepo;
    private StudentDetailViewModel viewModel;

    @Before
    public void setUp() {
        ArchTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
            @Override
            public void executeOnDiskIO(Runnable runnable) {
                runnable.run();
            }

            @Override
            public void postToMainThread(Runnable runnable) {
                runnable.run();
            }

            @Override
            public boolean isMainThread() {
                return true;
            }
        });

        studentRepo = new MockStudentRepository();
        certRepo = new MockCertificateRepository();
        viewModel = new StudentDetailViewModel(studentRepo, certRepo);
    }

    @After
    public void tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @Test
    public void loadStudent_loadsStudentInfoAndCertificates() {
        viewModel.loadStudent("524H0001");

        Student student = viewModel.getStudentData().getValue();
        assertNotNull(student);
        assertEquals("524H0001", student.getStudentId());
        assertEquals("Nguyễn Văn An", student.getName());

        UiState<List<Certificate>> certState = viewModel.getCertificateListState().getValue();
        assertNotNull(certState);
        assertTrue(certState.isContent());
        assertEquals(2, certState.getData().size());
    }

    @Test
    public void loadStudent_whenNoCertificates_emitsEmptyState() {
        // 524H0004 has 0 certificates in mock
        viewModel.loadStudent("524H0004");

        Student student = viewModel.getStudentData().getValue();
        assertNotNull(student);

        UiState<List<Certificate>> certState = viewModel.getCertificateListState().getValue();
        assertNotNull(certState);
        assertTrue("Student with 0 certs must emit EMPTY state", certState.isEmpty());
    }

    @Test
    public void addCertificate_updatesCertificatesList() {
        viewModel.loadStudent("524H0001");

        Certificate newCert = new Certificate(null, "JLPT N2", "2024-07-01", "Japan Foundation");
        viewModel.addCertificate(newCert);

        assertEquals("Thêm chứng chỉ thành công.", viewModel.getActionSuccessMessage().getValue());

        UiState<List<Certificate>> certState = viewModel.getCertificateListState().getValue();
        assertTrue(certState.isContent());
        assertEquals(3, certState.getData().size());
    }

    @Test
    public void deleteCertificate_removesCertAndEmitsMessage() {
        viewModel.loadStudent("524H0001");
        viewModel.deleteCertificate("c1");

        assertEquals("Xóa chứng chỉ thành công.", viewModel.getActionSuccessMessage().getValue());

        UiState<List<Certificate>> certState = viewModel.getCertificateListState().getValue();
        assertTrue(certState.isContent());
        assertEquals(1, certState.getData().size());
        assertEquals("c2", certState.getData().get(0).getCertId());
    }
}
