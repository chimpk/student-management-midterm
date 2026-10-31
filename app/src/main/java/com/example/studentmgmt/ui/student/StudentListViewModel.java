package com.example.studentmgmt.ui.student;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studentmgmt.data.StudentRepository;
import com.example.studentmgmt.data.StudentRepositoryProvider;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.UiState;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel for Student List, search, filter, sort (S13), undo delete (S14), and CRUD operations.
 */
public class StudentListViewModel extends ViewModel {

    public enum SortCriteria {
        NAME_ASC,
        NAME_DESC,
        CLASS_ASC,
        BIRTH_ASC
    }

    private final StudentRepository studentRepository;
    private final MutableLiveData<UiState<List<Student>>> studentListState = new MutableLiveData<>();
    private final MutableLiveData<String> actionErrorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> actionSuccessMessage = new MutableLiveData<>();
    private final MutableLiveData<Student> lastDeletedStudentEvent = new MutableLiveData<>();

    private List<Student> cachedList = new ArrayList<>();
    private String currentSearchQuery = "";
    private String currentFacultyFilter = "";
    private String currentClassFilter = "";
    private SortCriteria currentSortCriteria = SortCriteria.NAME_ASC;

    private StudentRepository.ListenerRegistrationHandle listenerHandle;
    private int listenerGeneration = 0;
    private Student lastDeletedStudent;

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

    public LiveData<Student> getLastDeletedStudentEvent() {
        return lastDeletedStudentEvent;
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

    public String getSearchQuery() {
        return currentSearchQuery;
    }

    public void setFacultyFilter(String faculty) {
        this.currentFacultyFilter = faculty != null ? faculty.trim() : "";
        applyFilters();
    }

    public String getFacultyFilter() {
        return currentFacultyFilter;
    }

    public void setClassFilter(String className) {
        this.currentClassFilter = className != null ? className.trim() : "";
        applyFilters();
    }

    public String getClassFilter() {
        return currentClassFilter;
    }

    public void setSortCriteria(SortCriteria criteria) {
        if (criteria != null) {
            this.currentSortCriteria = criteria;
            applyFilters();
        }
    }

    public SortCriteria getSortCriteria() {
        return currentSortCriteria;
    }

    private void applyFilters() {
        if (cachedList.isEmpty()) {
            studentListState.setValue(UiState.empty());
            return;
        }

        List<Student> filtered = new ArrayList<>();
        String normalizedQuery = normalizeForSearch(currentSearchQuery);

        for (Student s : cachedList) {
            boolean matchesSearch = normalizedQuery.isEmpty()
                    || normalizeForSearch(s.getName()).contains(normalizedQuery)
                    || normalizeForSearch(s.getStudentId()).contains(normalizedQuery)
                    || normalizeForSearch(s.getClassName()).contains(normalizedQuery);

            boolean matchesFaculty = currentFacultyFilter.isEmpty()
                    || (s.getFaculty() != null && s.getFaculty().equalsIgnoreCase(currentFacultyFilter));

            boolean matchesClass = currentClassFilter.isEmpty()
                    || (s.getClassName() != null && s.getClassName().equalsIgnoreCase(currentClassFilter));

            if (matchesSearch && matchesFaculty && matchesClass) {
                filtered.add(s);
            }
        }

        // Apply Sorting (S13)
        sortList(filtered, currentSortCriteria);

        if (filtered.isEmpty()) {
            studentListState.setValue(UiState.empty());
        } else {
            studentListState.setValue(UiState.content(filtered));
        }
    }

    /** Removes Vietnamese tone marks and maps đ/Đ to d so search works with or without accents. */
    private static String normalizeForSearch(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        String lowerCase = value.toLowerCase(Locale.ROOT).replace('đ', 'd');
        return Normalizer.normalize(lowerCase, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }

    private void sortList(List<Student> list, SortCriteria criteria) {
        final Collator collator = Collator.getInstance(new Locale("vi", "VN"));
        collator.setStrength(Collator.PRIMARY);

        switch (criteria) {
            case NAME_ASC:
                Collections.sort(list, (a, b) -> {
                    String nameA = a.getName() != null ? a.getName() : "";
                    String nameB = b.getName() != null ? b.getName() : "";
                    return collator.compare(nameA, nameB);
                });
                break;
            case NAME_DESC:
                Collections.sort(list, (a, b) -> {
                    String nameA = a.getName() != null ? a.getName() : "";
                    String nameB = b.getName() != null ? b.getName() : "";
                    return collator.compare(nameB, nameA);
                });
                break;
            case CLASS_ASC:
                Collections.sort(list, (a, b) -> {
                    String classA = a.getClassName() != null ? a.getClassName() : "";
                    String classB = b.getClassName() != null ? b.getClassName() : "";
                    return classA.compareToIgnoreCase(classB);
                });
                break;
            case BIRTH_ASC:
                Collections.sort(list, (a, b) -> {
                    String dobA = a.getDateOfBirth() != null ? a.getDateOfBirth() : "";
                    String dobB = b.getDateOfBirth() != null ? b.getDateOfBirth() : "";
                    return dobA.compareToIgnoreCase(dobB);
                });
                break;
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
        // Track student before deleting to support undo
        for (Student s : cachedList) {
            if (s.getStudentId().equals(studentId)) {
                lastDeletedStudent = s;
                break;
            }
        }

        studentRepository.deleteStudent(studentId, new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                actionSuccessMessage.setValue("Xóa sinh viên thành công.");
                if (lastDeletedStudent != null) {
                    lastDeletedStudentEvent.setValue(lastDeletedStudent);
                }
            }

            @Override
            public void onError(String errorMessage) {
                actionErrorMessage.setValue(errorMessage);
            }
        });
    }

    public void undoDelete() {
        if (lastDeletedStudent != null) {
            final Student toRestore = lastDeletedStudent;
            lastDeletedStudent = null;
            addStudent(toRestore);
        }
    }

    public void retry() {
        loadStudents();
    }

    public void clearActionMessages() {
        actionErrorMessage.setValue(null);
        actionSuccessMessage.setValue(null);
        lastDeletedStudentEvent.setValue(null);
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
