package com.example.studentmgmt.ui.student;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studentmgmt.data.CertificateRepository;
import com.example.studentmgmt.data.CertificateRepositoryProvider;
import com.example.studentmgmt.data.StudentRepository;
import com.example.studentmgmt.data.StudentRepositoryProvider;
import com.example.studentmgmt.model.Certificate;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.UiState;

import java.util.List;

/**
 * TODO(member-2): ViewModel for Student Detail and Certificate management.
 */
public class StudentDetailViewModel extends ViewModel {

    private final StudentRepository studentRepository;
    private final CertificateRepository certificateRepository;

    private final MutableLiveData<Student> studentData = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Certificate>>> certificateListState = new MutableLiveData<>();
    private final MutableLiveData<String> actionErrorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> actionSuccessMessage = new MutableLiveData<>();

    private CertificateRepository.ListenerRegistrationHandle certListenerHandle;
    private int certGeneration = 0;
    private String currentStudentId;

    public StudentDetailViewModel() {
        this(StudentRepositoryProvider.getInstance(), CertificateRepositoryProvider.getInstance());
    }

    public StudentDetailViewModel(StudentRepository studentRepo, CertificateRepository certRepo) {
        this.studentRepository = studentRepo;
        this.certificateRepository = certRepo;
    }

    public LiveData<Student> getStudentData() {
        return studentData;
    }

    public LiveData<UiState<List<Certificate>>> getCertificateListState() {
        return certificateListState;
    }

    public LiveData<String> getActionErrorMessage() {
        return actionErrorMessage;
    }

    public LiveData<String> getActionSuccessMessage() {
        return actionSuccessMessage;
    }

    public void loadStudent(String studentId) {
        this.currentStudentId = studentId;
        studentRepository.getStudent(studentId, new StudentRepository.SingleStudentCallback() {
            @Override
            public void onSuccess(Student student) {
                studentData.setValue(student);
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
        loadCertificates(studentId);
    }

    public void loadCertificates(String studentId) {
        certificateListState.setValue(UiState.loading());
        final int gen = ++certGeneration;

        if (certListenerHandle != null) {
            certListenerHandle.remove();
            certListenerHandle = null;
        }

        certListenerHandle = certificateRepository.listenCertificates(studentId, new CertificateRepository.CertificateListCallback() {
            @Override
            public void onSuccess(List<Certificate> certificates) {
                if (gen != certGeneration) return;
                if (certificates == null || certificates.isEmpty()) {
                    certificateListState.setValue(UiState.empty());
                } else {
                    certificateListState.setValue(UiState.content(certificates));
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (gen != certGeneration) return;
                certificateListState.setValue(UiState.error(errorMessage));
            }
        });
    }

    public void addCertificate(Certificate cert) {
        if (currentStudentId == null) return;
        certificateRepository.addCertificate(currentStudentId, cert, new CertificateRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                actionSuccessMessage.setValue("Thêm chứng chỉ thành công.");
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
    }

    public void updateCertificate(Certificate cert) {
        if (currentStudentId == null) return;
        certificateRepository.updateCertificate(currentStudentId, cert, new CertificateRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                actionSuccessMessage.setValue("Cập nhật chứng chỉ thành công.");
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
    }

    public void deleteCertificate(String certId) {
        if (currentStudentId == null) return;
        certificateRepository.deleteCertificate(currentStudentId, certId, new CertificateRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                actionSuccessMessage.setValue("Xóa chứng chỉ thành công.");
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
    }

    public void retryCertificates() {
        if (currentStudentId != null) {
            loadCertificates(currentStudentId);
        }
    }

    public void updateStudent(Student student) {
        studentRepository.updateStudent(student, new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                studentData.setValue(student);
                actionSuccessMessage.setValue("Cập nhật thông tin sinh viên thành công.");
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
    }

    public void deleteStudent(String studentId, Runnable onComplete) {
        studentRepository.deleteStudent(studentId, new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                actionSuccessMessage.setValue("Đã xóa sinh viên.");
                if (onComplete != null) {
                    onComplete.run();
                }
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
    }

    public void clearActionMessages() {
        actionErrorMessage.setValue(null);
        actionSuccessMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (certListenerHandle != null) {
            certListenerHandle.remove();
            certListenerHandle = null;
        }
    }
}
