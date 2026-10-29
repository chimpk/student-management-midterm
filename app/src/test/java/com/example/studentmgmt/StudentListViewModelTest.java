package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;

import com.example.studentmgmt.data.MockStudentRepository;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.UiState;
import com.example.studentmgmt.ui.student.StudentListViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for StudentListViewModel (Member 2).
 */
public class StudentListViewModelTest {

    private MockStudentRepository repository;
    private StudentListViewModel viewModel;

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

        repository = new MockStudentRepository();
        viewModel = new StudentListViewModel(repository);
    }

    @After
    public void tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @Test
    public void initialState_loadsSampleStudents_intoContentState() {
        UiState<List<Student>> state = viewModel.getStudentListState().getValue();
        assertNotNull(state);
        assertTrue("Initial state must be CONTENT", state.isContent());
        assertTrue(state.getData().size() >= 8);
    }

    @Test
    public void searchFilter_filtersByNameAndMSSV() {
        // Search by name "An" (normalized case-insensitive without accents)
        viewModel.setSearchQuery("An");
        UiState<List<Student>> state = viewModel.getStudentListState().getValue();
        assertTrue(state.isContent());
        for (Student s : state.getData()) {
            String norm = java.text.Normalizer.normalize(s.getName().toLowerCase().replace('đ', 'd'), java.text.Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
            String normId = s.getStudentId().toLowerCase();
            String normClass = s.getClassName().toLowerCase();
            assertTrue(norm.contains("an") || normId.contains("an") || normClass.contains("an"));
        }

        // Search by MSSV "524H0001"
        viewModel.setSearchQuery("524H0001");
        state = viewModel.getStudentListState().getValue();
        assertTrue(state.isContent());
        assertEquals(1, state.getData().size());
        assertEquals("524H0001", state.getData().get(0).getStudentId());

        // Search non-existent
        viewModel.setSearchQuery("KhongTonTai_9999");
        state = viewModel.getStudentListState().getValue();
        assertTrue("Non-matching search must produce EMPTY state", state.isEmpty());
    }

    @Test
    public void facultyFilter_filtersAccurately() {
        viewModel.setFacultyFilter("Khoa học máy tính");
        UiState<List<Student>> state = viewModel.getStudentListState().getValue();
        assertTrue(state.isContent());
        for (Student s : state.getData()) {
            assertEquals("Khoa học máy tính", s.getFaculty());
        }
    }

    @Test
    public void addStudent_updatesList_andEmitsSuccessMessage() {
        Student newStudent = new Student("524H0888", "Hoàng Văn Nam", "2003-09-09", "24H50301", "Công nghệ thông tin");
        viewModel.addStudent(newStudent);

        assertEquals("Thêm sinh viên thành công.", viewModel.getActionSuccessMessage().getValue());

        UiState<List<Student>> state = viewModel.getStudentListState().getValue();
        assertTrue(state.isContent());
        boolean found = false;
        for (Student s : state.getData()) {
            if ("524H0888".equals(s.getStudentId())) {
                found = true;
                break;
            }
        }
        assertTrue("Newly added student must be in content list", found);
    }

    @Test
    public void deleteStudent_removesFromList_andEmitsSuccessMessage() {
        viewModel.deleteStudent("524H0001");
        assertEquals("Xóa sinh viên thành công.", viewModel.getActionSuccessMessage().getValue());

        UiState<List<Student>> state = viewModel.getStudentListState().getValue();
        assertTrue(state.isContent());
        for (Student s : state.getData()) {
            assertFalse("Deleted student must not appear in list", "524H0001".equals(s.getStudentId()));
        }
    }
}
