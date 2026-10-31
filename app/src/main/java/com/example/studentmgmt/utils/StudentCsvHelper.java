package com.example.studentmgmt.utils;

import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.Certificate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * RFC 4180 compliant CSV helper for Student and Certificate import/export.
 * Supports escaping, quotes, commas within fields, UTF-8 BOM, and standard date normalization.
 */
public final class StudentCsvHelper {

    public static final String CSV_HEADER = "Mã SV,Họ và tên,Ngày sinh,Lớp,Khoa";
    public static final String CERTIFICATE_CSV_HEADER = "mssv,ten_chung_chi,ngay_cap,don_vi_cap";
    public static final String UTF8_BOM = "\uFEFF";

    private StudentCsvHelper() {}

    /**
     * Exports a list of students to a standard RFC 4180 CSV formatted string.
     */
    public static String exportToCsv(List<Student> students) {
        StringBuilder sb = new StringBuilder();
        sb.append(UTF8_BOM).append(CSV_HEADER).append("\n");
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

    /** Exports every certificate with its owning student ID for an Excel-friendly CSV file. */
    public static String exportCertificatesToCsv(List<Student> students,
                                                 Map<String, List<Certificate>> certificatesByStudent) {
        StringBuilder csv = new StringBuilder(UTF8_BOM).append(CERTIFICATE_CSV_HEADER).append("\n");
        if (students == null || certificatesByStudent == null) {
            return csv.toString();
        }

        for (Student student : students) {
            if (student == null || student.getStudentId() == null) {
                continue;
            }
            List<Certificate> certificates = certificatesByStudent.get(student.getStudentId());
            if (certificates == null) {
                continue;
            }
            for (Certificate certificate : certificates) {
                if (certificate == null) {
                    continue;
                }
                csv.append(escapeCsvField(student.getStudentId())).append(',')
                        .append(escapeCsvField(certificate.getCertName())).append(',')
                        .append(escapeCsvField(certificate.getIssueDate())).append(',')
                        .append(escapeCsvField(certificate.getIssuedBy())).append('\n');
            }
        }
        return csv.toString();
    }

    /**
     * Parses a CSV formatted string into a list of Student objects.
     */
    public static List<Student> importFromCsv(String csvContent) throws IOException {
        List<Student> result = new ArrayList<>();
        if (csvContent == null || csvContent.trim().isEmpty()) {
            return result;
        }

        List<List<String>> rows = parseCsvRows(new StringReader(csvContent));
        boolean isFirstLine = true;
        for (List<String> parts : rows) {
            if (parts.isEmpty()) continue;

            if (isFirstLine) {
                isFirstLine = false;
                String first = parts.get(0);
                if (first != null && (first.startsWith("Mã SV") || first.startsWith("StudentId") || first.startsWith("\uFEFFMã SV"))) {
                    continue;
                }
            }

            if (parts.size() >= 5) {
                String id = unescapeCsvField(parts.get(0));
                String name = unescapeCsvField(parts.get(1));
                String dob = normalizeDate(unescapeCsvField(parts.get(2)));
                String className = unescapeCsvField(parts.get(3));
                String faculty = unescapeCsvField(parts.get(4));

                if (!id.isEmpty() && !name.isEmpty()) {
                    result.add(new Student(id, name, dob, className, faculty));
                }
            }
        }
        return result;
    }

    /**
     * Parses an arbitrary CSV stream according to RFC 4180.
     * Handles quoted fields containing commas, double quotes (""), and line breaks.
     */
    public static List<List<String>> parseCsvRows(Reader reader) throws IOException {
        List<List<String>> records = new ArrayList<>();
        List<String> currentRecord = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        BufferedReader in = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);

        boolean inQuotes = false;
        int ch;
        boolean firstChar = true;

        while ((ch = in.read()) != -1) {
            char c = (char) ch;

            // Strip UTF-8 BOM if present at start
            if (firstChar) {
                firstChar = false;
                if (c == '\uFEFF') {
                    continue;
                }
            }

            if (inQuotes) {
                if (c == '"') {
                    in.mark(1);
                    int next = in.read();
                    if (next == '"') {
                        currentField.append('"');
                    } else {
                        if (next != -1) {
                            in.reset();
                        }
                        inQuotes = false;
                    }
                } else {
                    currentField.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    currentRecord.add(currentField.toString().trim());
                    currentField.setLength(0);
                } else if (c == '\r') {
                    in.mark(1);
                    int next = in.read();
                    if (next != '\n' && next != -1) {
                        in.reset();
                    }
                    currentRecord.add(currentField.toString().trim());
                    currentField.setLength(0);
                    if (!isEmptyRecord(currentRecord)) {
                        records.add(new ArrayList<>(currentRecord));
                    }
                    currentRecord.clear();
                } else if (c == '\n') {
                    currentRecord.add(currentField.toString().trim());
                    currentField.setLength(0);
                    if (!isEmptyRecord(currentRecord)) {
                        records.add(new ArrayList<>(currentRecord));
                    }
                    currentRecord.clear();
                } else {
                    currentField.append(c);
                }
            }
        }

        if (currentField.length() > 0 || !currentRecord.isEmpty()) {
            currentRecord.add(currentField.toString().trim());
            if (!isEmptyRecord(currentRecord)) {
                records.add(new ArrayList<>(currentRecord));
            }
        }

        return records;
    }

    private static boolean isEmptyRecord(List<String> record) {
        if (record == null || record.isEmpty()) return true;
        for (String field : record) {
            if (field != null && !field.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Normalizes dates from formats like dd/MM/yyyy to yyyy-MM-dd.
     */
    public static String normalizeDate(String rawDate) {
        if (rawDate == null) return "";
        String trimmed = rawDate.trim();
        // If already yyyy-MM-dd
        if (trimmed.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
            return trimmed;
        }
        // If dd/MM/yyyy or d/M/yyyy
        if (trimmed.matches("^[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}$")) {
            String[] parts = trimmed.split("/");
            if (parts.length == 3) {
                try {
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);
                    return String.format(Locale.US, "%04d-%02d-%02d", year, month, day);
                } catch (NumberFormatException ignored) {}
            }
        }
        return trimmed;
    }

    public static String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    public static String unescapeCsvField(String field) {
        if (field == null) return "";
        String trimmed = field.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).replace("\"\"", "\"");
        }
        return trimmed.trim();
    }
}
