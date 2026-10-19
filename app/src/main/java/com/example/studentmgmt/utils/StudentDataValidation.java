package com.example.studentmgmt.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Validation shared by Firestore CRUD and CSV import for Member 2 data. */
public final class StudentDataValidation {
    private StudentDataValidation() {}

    public static String validateStudent(String studentId, String name, String dateOfBirth,
                                         String className, String faculty) {
        return validateStudent(studentId, name, dateOfBirth, className, faculty, LocalDate.now());
    }

    public static String validateStudent(String studentId, String name, String dateOfBirth,
                                         String className, String faculty, LocalDate today) {
        if (!ValidationUtils.isValidStudentId(studentId)) return "MSSV không đúng định dạng.";
        if (!inRange(name, 2, 100)) return "Họ tên phải có từ 2 đến 100 ký tự.";
        if (!isValidDate(dateOfBirth, today)) return "Ngày sinh phải đúng YYYY-MM-DD và không ở tương lai.";
        if (!ValidationUtils.isValidClassCode(className)) return "Mã lớp phải gồm 8 chữ hoa hoặc chữ số.";
        if (!inRange(faculty, 2, 100)) return "Khoa phải có từ 2 đến 100 ký tự.";
        return null;
    }

    public static String validateCertificate(String name, String issueDate, String issuer) {
        return validateCertificate(name, issueDate, issuer, LocalDate.now());
    }

    public static String validateCertificate(String name, String issueDate, String issuer,
                                             LocalDate today) {
        if (!inRange(name, 2, 100)) return "Tên chứng chỉ phải có từ 2 đến 100 ký tự.";
        if (!isValidDate(issueDate, today)) return "Ngày cấp phải đúng YYYY-MM-DD và không ở tương lai.";
        if (!inRange(issuer, 2, 100)) return "Đơn vị cấp phải có từ 2 đến 100 ký tự.";
        return null;
    }

    private static boolean inRange(String value, int min, int max) {
        if (value == null) return false;
        int length = value.trim().length();
        return length >= min && length <= max;
    }

    private static boolean isValidDate(String value, LocalDate today) {
        if (value == null || !value.matches("\\d{4}-\\d{2}-\\d{2}")) return false;
        if (today == null) return false;
        try {
            LocalDate date = LocalDate.parse(value);
            return date.getYear() >= 1900 && !date.isAfter(today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
