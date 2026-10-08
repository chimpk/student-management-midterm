package com.example.studentmgmt.utils;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-z][a-z0-9._-]{2,31}$");
    private static final Pattern STUDENT_ID_PATTERN =
            Pattern.compile("^[0-9A-H][0-9]{2}[0H][0-9]{4}$");
    private static final Pattern CLASS_CODE_PATTERN =
            Pattern.compile("^[0-9A-Z]{8}$");
    private static final Pattern KNOWN_IT_CLASS_CODE_PATTERN =
            Pattern.compile("^[0-9]{2}(?:05|H5)(?:02|03|04)[0-9]{2}$");

    private ValidationUtils() {
        throw new AssertionError("Không được khởi tạo lớp tiện ích");
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId).matches();
    }

    public static boolean isValidClassCode(String classCode) {
        return classCode != null && CLASS_CODE_PATTERN.matcher(classCode).matches();
    }

    public static boolean isKnownItClassCode(String classCode) {
        return classCode != null && KNOWN_IT_CLASS_CODE_PATTERN.matcher(classCode).matches();
    }
}
