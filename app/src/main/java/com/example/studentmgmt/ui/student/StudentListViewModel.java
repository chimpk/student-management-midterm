package com.example.studentmgmt.ui.student;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studentmgmt.data.StudentRepository;
import com.example.studentmgmt.data.StudentRepositoryProvider;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.UiState;
import com.example.studentmgmt.utils.StudentSearchSortUtils;
import com.example.studentmgmt.utils.StudentSearchSortUtils.SortOption;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO(member-2): ViewModel for Student List, search, filter, and CRUD operations.
 */
public class StudentListViewModel extends ViewModel {

    private final StudentRepository studentRepository;
    private final MutableLiveData<UiState<List<Student>>> studentListState = new MutableLiveData<>();
    private final MutableLiveData<String> actionErrorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> actionSuccessMessage = new MutableLiveData<>();

    private List<Student> cachedList = new ArrayList<>();
    private String currentSearchQuery = "";
    private String currentFacultyFilter = "";
    private SortOption currentSortOption = SortOption.STUDENT_ID_ASC;
    private StudentRepository.ListenerRegistrationHandle listenerHandle;
    private int listenerGeneration = 0;

    public StudentListViewModel() {
        this(StudentRepositoryProvider.getInstance());
    }

    public StudentListViewModel(StudentRepository repository) {
        this.studentRepository = repository;
        loadStudents();
    }

    public LiveData<UiState<List<Student>>> getStudentListState() {
        return studentListState;
    }

    public LiveData<String> getActionErrorMessage() {
        return actionErrorMessage;
    }

    public LiveData<String> getActionSuccessMessage() {
        return actionSuccessMessage;
    }

    public void loadStudents() {
        studentListState.setValue(UiState.loading());
        final int gen = ++listenerGeneration;

        if (listenerHandle != null) {
            listenerHandle.remove();
            listenerHandle = null;
        }

        listenerHandle = studentRepository.listenStudents(new StudentRepository.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> students) {
                if (gen != listenerGeneration) return;
                cachedList = students != null ? new ArrayList<>(students) : new ArrayList<>();
                applyFilters();
            }

            @Override
            public void onError(String errorMessage) {
                if (gen != listenerGeneration) return;
                studentListState.setValue(UiState.error(errorMessage));
            }
        });
    }

    public void setSearchQuery(String query) {
        this.currentSearchQuery = query != null ? query.trim() : "";
        applyFilters();
    }

    public void setFacultyFilter(String faculty) {
        this.currentFacultyFilter = faculty != null ? faculty.trim() : "";
        applyFilters();
    }

    public void setSortOption(SortOption option) {
        currentSortOption = option != null ? option : SortOption.STUDENT_ID_ASC;
        applyFilters();
    }

    public SortOption getSortOption() {
        return currentSortOption;
    }

    private void applyFilters() {
        if (cachedList.isEmpty()) {
            studentListState.setValue(UiState.empty());
            return;
        }

        List<Student> filtered = StudentSearchSortUtils.filter(cachedList, currentSearchQuery);
        if (!currentFacultyFilter.isEmpty()) {
            filtered.removeIf(student -> student.getFaculty() == null
                    || !student.getFaculty().equalsIgnoreCase(currentFacultyFilter));
        }
        filtered.sort(StudentSearchSortUtils.comparator(currentSortOption));

        if (filtered.isEmpty()) {
            studentListState.setValue(UiState.empty());
        } else {
            studentListState.setValue(UiState.content(filtered));
        }
    }

    public void addStudent(Student student) {
        studentRepository.addStudent(student, new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                actionSuccessMessage.setValue("Thêm sinh viên thành công.");
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
    }

    public void updateStudent(Student student) {
        studentRepository.updateStudent(student, new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                actionSuccessMessage.setValue("Cập nhật sinh viên thành công.");
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
    }

    public void deleteStudent(String studentId) {
        studentRepository.deleteStudent(studentId, new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                actionSuccessMessage.setValue("Xóa sinh viên thành công.");
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
    }

    public void retry() {
        loadStudents();
    }

    public void clearActionMessages() {
        actionErrorMessage.setValue(null);
        actionSuccessMessage.setValue(null);
    }

    public List<Student> getAllCachedStudents() {
        return new ArrayList<>(cachedList);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listenerHandle != null) {
            listenerHandle.remove();
            listenerHandle = null;
        }
    }
}
