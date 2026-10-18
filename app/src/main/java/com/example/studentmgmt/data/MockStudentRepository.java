package com.example.studentmgmt.data;

import com.example.studentmgmt.model.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TODO(member-2): Placeholder in-memory implementation for StudentRepository.
 * Member 2 will replace or supplement this with FirestoreStudentRepository for live backend sync.
 */
public class MockStudentRepository implements StudentRepository {

    private final List<Student> students = new CopyOnWriteArrayList<>();
    private final List<StudentListCallback> listeners = new CopyOnWriteArrayList<>();

    public MockStudentRepository() {
        seedSampleData();
    }

    private void seedSampleData() {
        students.add(new Student("524H0001", "Nguyễn Văn An", "2003-01-15", "24H50301", "Công nghệ thông tin", System.currentTimeMillis() - 1000000, 2));
        students.add(new Student("524H0002", "Trần Thị Mai", "2003-04-20", "24H50301", "Công nghệ thông tin", System.currentTimeMillis() - 900000, 1));
        students.add(new Student("524H0003", "Lê Hoàng Phúc", "2003-08-11", "24H50302", "Công nghệ thông tin", System.currentTimeMillis() - 800000, 3));
        students.add(new Student("524H0004", "Phạm Quốc Bảo", "2002-12-05", "23H50201", "Khoa học máy tính", System.currentTimeMillis() - 700000, 0));
        students.add(new Student("524H0005", "Võ Minh Trí", "2003-06-18", "24H50301", "Công nghệ thông tin", System.currentTimeMillis() - 600000, 1));
        students.add(new Student("524H0006", "Đặng Thùy Dung", "2003-09-25", "24H50303", "Kỹ thuật phần mềm", System.currentTimeMillis() - 500000, 2));
        students.add(new Student("524H0007", "Bùi Tấn Tài", "2002-11-30", "23H50202", "Khoa học máy tính", System.currentTimeMillis() - 400000, 1));
        students.add(new Student("524H0008", "Hoàng Kim Ngân", "2003-03-08", "24H50302", "Công nghệ thông tin", System.currentTimeMillis() - 300000, 0));
    }

    @Override
    public ListenerRegistrationHandle listenStudents(StudentListCallback callback) {
        if (callback != null) {
            listeners.add(callback);
            callback.onSuccess(new ArrayList<>(students));
        }
        return () -> {
            if (callback != null) {
                listeners.remove(callback);
            }
        };
    }

    @Override
    public void getStudent(String studentId, SingleStudentCallback callback) {
        if (callback == null) return;
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) {
                callback.onSuccess(s);
                return;
            }
        }
        callback.onError("Không tìm thấy sinh viên với mã: " + studentId);
    }

    @Override
    public void addStudent(Student student, ActionCallback callback) {
        if (student == null || student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            if (callback != null) callback.onError("Mã sinh viên không được để trống.");
            return;
        }

        for (Student existing : students) {
            if (existing.getStudentId().equalsIgnoreCase(student.getStudentId())) {
                if (callback != null) callback.onError("Mã sinh viên đã tồn tại trong hệ thống.");
                return;
            }
        }

        students.add(student);
        notifyListeners();
        if (callback != null) callback.onSuccess();
    }

    @Override
    public void updateStudent(Student student, ActionCallback callback) {
        if (student == null || student.getStudentId() == null) {
            if (callback != null) callback.onError("Dữ liệu cập nhật không hợp lệ.");
            return;
        }

        boolean found = false;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equalsIgnoreCase(student.getStudentId())) {
                students.set(i, student);
                found = true;
                break;
            }
        }

        if (found) {
            notifyListeners();
            if (callback != null) callback.onSuccess();
        } else {
            if (callback != null) callback.onError("Không tìm thấy sinh viên để cập nhật.");
        }
    }

    @Override
    public void deleteStudent(String studentId, ActionCallback callback) {
        if (studentId == null) {
            if (callback != null) callback.onError("Mã sinh viên không hợp lệ.");
            return;
        }

        boolean removed = students.removeIf(s -> s.getStudentId().equalsIgnoreCase(studentId));
        if (removed) {
            notifyListeners();
            if (callback != null) callback.onSuccess();
        } else {
            if (callback != null) callback.onError("Không tìm thấy sinh viên cần xóa.");
        }
    }

    private void notifyListeners() {
        List<Student> copy = new ArrayList<>(students);
        for (StudentListCallback cb : listeners) {
            cb.onSuccess(copy);
        }
    }
}
