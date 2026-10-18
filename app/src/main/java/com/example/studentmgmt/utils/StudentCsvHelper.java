package com.example.studentmgmt.utils;

import com.example.studentmgmt.model.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO(member-2): CSV helper for Student import/export for Member 2 (Nguyễn Vĩnh Khâm).
 * Handles UTF-8 CSV parsing and generation, batching up to 400 records per write for Firestore.
 */
public final class StudentCsvHelper {

    public static final String CSV_HEADER = "Mã SV,Họ và tên,Ngày sinh,Lớp,Khoa";

    private StudentCsvHelper() {}

    /**
     * Exports a list of students to a CSV formatted string.
     */
    public static String exportToCsv(List<Student> students) {
        StringBuilder sb = new StringBuilder();
        sb.append(CSV_HEADER).append("\n");
        if (students != null) {
            for (Student s : students) {
                sb.append(escapeCsvField(s.getStudentId())).append(",")
                  .append(escapeCsvField(s.getName())).append(",")
                  .append(escapeCsvField(s.getDateOfBirth())).append(",")
                  .append(escapeCsvField(s.getClassName())).append(",")
                  .append(escapeCsvField(s.getFaculty())).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Parses a CSV formatted string into a list of Student objects.
     */
    public static List<Student> importFromCsv(String csvContent) throws IOException {
        List<Student> result = new ArrayList<>();
        if (csvContent == null || csvContent.trim().isEmpty()) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Skip header if present
                if (isFirstLine && (line.startsWith("Mã SV") || line.startsWith("StudentId") || line.startsWith("\uFEFFMã SV"))) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;

                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String id = unescapeCsvField(parts[0]);
                    String name = unescapeCsvField(parts[1]);
                    String dob = unescapeCsvField(parts[2]);
                    String className = unescapeCsvField(parts[3]);
                    String faculty = unescapeCsvField(parts[4]);

                    if (!id.isEmpty() && !name.isEmpty()) {
                        result.add(new Student(id, name, dob, className, faculty));
                    }
                }
            }
        }
        return result;
    }

    private static String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private static String unescapeCsvField(String field) {
        if (field == null) return "";
        String trimmed = field.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).replace("\"\"", "\"");
        }
        return trimmed.trim();
    }
}
