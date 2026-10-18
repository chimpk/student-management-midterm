package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.example.studentmgmt.data.MockStudentRepository;
import com.example.studentmgmt.data.StudentRepository;
import com.example.studentmgmt.model.Student;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit tests for StudentRepository (Mock implementation for Member 2).
 */
public class StudentRepositoryTest {

    private MockStudentRepository repository;

    @Before
    public void setUp() {
        repository = new MockStudentRepository();
    }

    @Test
    public void listenStudents_initiallyReturnsSampleData() {
        List<Student> loaded = new ArrayList<>();
        StudentRepository.ListenerRegistrationHandle handle = repository.listenStudents(new StudentRepository.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> students) {
                loaded.addAll(students);
            }

            @Override
            public void onError(String errorMessage) {
                fail("Should not fail: " + errorMessage);
            }
        });

        assertNotNull(handle);
        assertTrue("Should have initial sample students", loaded.size() >= 8);
        handle.remove();
    }

    @Test
    public void getStudent_returnsStudent_whenExists() {
        repository.getStudent("524H0001", new StudentRepository.SingleStudentCallback() {
            @Override
            public void onSuccess(Student student) {
                assertNotNull(student);
                assertEquals("524H0001", student.getStudentId());
                assertEquals("Nguyễn Văn An", student.getName());
            }

            @Override
            public void onError(String errorMessage) {
                fail("Student should exist: " + errorMessage);
            }
        });
    }

    @Test
    public void getStudent_returnsError_whenNotFound() {
        AtomicBoolean errorCalled = new AtomicBoolean(false);
        repository.getStudent("999H9999", new StudentRepository.SingleStudentCallback() {
            @Override
            public void onSuccess(Student student) {
                fail("Student should not exist");
            }

            @Override
            public void onError(String errorMessage) {
                errorCalled.set(true);
                assertTrue(errorMessage.contains("Không tìm thấy"));
            }
        });
        assertTrue(errorCalled.get());
    }

    @Test
    public void addStudent_success_andNotifiesListener() {
        List<List<Student>> emissions = new ArrayList<>();
        StudentRepository.ListenerRegistrationHandle handle = repository.listenStudents(new StudentRepository.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> students) {
                emissions.add(students);
            }

            @Override
            public void onError(String errorMessage) {}
        });

        int initialSize = emissions.get(0).size();
        Student newStudent = new Student("524H0999", "Lê Văn Mới", "2003-05-12", "24H50301", "Công nghệ thông tin");

        AtomicBoolean success = new AtomicBoolean(false);
        repository.addStudent(newStudent, new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                success.set(true);
            }

            @Override
            public void onError(String errorMessage) {
                fail("Add should succeed: " + errorMessage);
            }
        });

        assertTrue(success.get());
        assertTrue(emissions.size() >= 2);
        assertEquals(initialSize + 1, emissions.get(emissions.size() - 1).size());
        handle.remove();
    }

    @Test
    public void addStudent_rejectsDuplicateStudentId() {
        Student duplicate = new Student("524H0001", "Trùng Mã", "2003-01-01", "24H50301", "Công nghệ thông tin");
        AtomicBoolean errorCalled = new AtomicBoolean(false);

        repository.addStudent(duplicate, new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                fail("Duplicate student ID must be rejected");
            }

            @Override
            public void onError(String errorMessage) {
                errorCalled.set(true);
                assertTrue(errorMessage.contains("đã tồn tại"));
            }
        });
        assertTrue(errorCalled.get());
    }

    @Test
    public void updateStudent_success_whenExists() {
        Student updated = new Student("524H0001", "Nguyễn Văn An (Đã Đổi)", "2003-01-15", "24H50309", "Khoa học máy tính");
        AtomicBoolean success = new AtomicBoolean(false);

        repository.updateStudent(updated, new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                success.set(true);
            }

            @Override
            public void onError(String errorMessage) {
                fail("Update should succeed");
            }
        });

        assertTrue(success.get());

        repository.getStudent("524H0001", new StudentRepository.SingleStudentCallback() {
            @Override
            public void onSuccess(Student student) {
                assertEquals("Nguyễn Văn An (Đã Đổi)", student.getName());
                assertEquals("24H50309", student.getClassName());
            }

            @Override
            public void onError(String errorMessage) {
                fail("Student must exist");
            }
        });
    }

    @Test
    public void deleteStudent_removesStudent() {
        AtomicBoolean success = new AtomicBoolean(false);
        repository.deleteStudent("524H0002", new StudentRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                success.set(true);
            }

            @Override
            public void onError(String errorMessage) {
                fail("Delete should succeed");
            }
        });

        assertTrue(success.get());

        repository.getStudent("524H0002", new StudentRepository.SingleStudentCallback() {
            @Override
            public void onSuccess(Student student) {
                fail("Deleted student should not exist");
            }

            @Override
            public void onError(String errorMessage) {
                assertTrue(errorMessage.contains("Không tìm thấy"));
            }
        });
    }
}
