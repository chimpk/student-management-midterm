package com.example.studentmgmt.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Shared validation for student CRUD fields sent to Firestore. */
public final class StudentDataValidation {

    private StudentDataValidation() { }

    public static String validateStudent(String studentId, String name, String dateOfBirth,
                                         String className, String faculty) {
        if (!ValidationUtils.isValidStudentId(studentId)) {
            return "MSSV phải đúng 8 ký tự theo định dạng TDTU.";
        }
        if (!inRange(name, 2, 100)) {
            return "Họ tên phải có từ 2 đến 100 ký tự.";
        }
        if (!isValidDate(dateOfBirth)) {
            return "Ngày sinh phải đúng YYYY-MM-DD và không ở tương lai.";
        }
        if (!ValidationUtils.isValidClassCode(className)) {
            return "Mã lớp phải gồm đúng 8 chữ hoa hoặc chữ số.";
        }
        if (!inRange(faculty, 2, 100)) {
            return "Khoa phải có từ 2 đến 100 ký tự.";
        }
        return null;
    }

    private static boolean inRange(String value, int min, int max) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= min && length <= max;
    }

    private static boolean isValidDate(String value) {
        if (value == null || !value.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
            return false;
        }
        try {
            LocalDate date = LocalDate.parse(value);
            return date.getYear() >= 1900 && !date.isAfter(LocalDate.now());
        } catch (DateTimeParseException error) {
            return false;
        }
    }
}
