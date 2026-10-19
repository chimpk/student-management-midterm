package com.example.studentmgmt.utils;

import com.example.studentmgmt.model.Certificate;
import com.example.studentmgmt.model.Student;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** CSV parsing/export helpers for the schema in docs/firestore-schema.md. */
public final class Member2CsvHelper {
    public static final String STUDENT_HEADER = "studentId,name,dateOfBirth,className,faculty";
    public static final String CERTIFICATE_HEADER = "studentId,certName,issueDate,issuedBy";
    private static final char BOM = '\uFEFF';

    private Member2CsvHelper() {}

    public static final class CsvError {
        public final int dataRow;
        public final String identifier;
        public final String message;

        private CsvError(int dataRow, String identifier, String message) {
            this.dataRow = dataRow;
            this.identifier = identifier;
            this.message = message;
        }
    }

    public static final class StudentResult {
        public final List<Student> students;
        public final List<StudentRow> rows;
        public final List<CsvError> errors;
        public final boolean validHeader;

        private StudentResult(List<Student> students, List<StudentRow> rows,
                              List<CsvError> errors, boolean validHeader) {
            this.students = students;
            this.rows = rows;
            this.errors = errors;
            this.validHeader = validHeader;
        }
    }

    public static final class StudentRow {
        public final int dataRow;
        public final Student student;
        private StudentRow(int dataRow, Student student) {
            this.dataRow = dataRow;
            this.student = student;
        }
    }

    public static final class CertificateRow {
        public final int dataRow;
        public final String studentId;
        public final Certificate certificate;

        public CertificateRow(int dataRow, String studentId, Certificate certificate) {
            this.dataRow = dataRow;
            this.studentId = studentId;
            this.certificate = certificate;
        }
    }

    public static final class CertificateResult {
        public final List<CertificateRow> certificates;
        public final List<CsvError> errors;
        public final boolean validHeader;

        private CertificateResult(List<CertificateRow> certificates, List<CsvError> errors,
                                  boolean validHeader) {
            this.certificates = certificates;
            this.errors = errors;
            this.validHeader = validHeader;
        }
    }

    /** Existing IDs are skipped and reported to avoid overwriting Firestore documents. */
    public static StudentResult parseStudents(Reader input, Set<String> existingIds,
                                              LocalDate today) throws IOException {
        List<String[]> rows = parseRecords(input);
        List<Student> students = new ArrayList<>();
        List<StudentRow> validRows = new ArrayList<>();
        List<CsvError> errors = new ArrayList<>();
        if (!hasHeader(rows, STUDENT_HEADER)) {
            errors.add(new CsvError(0, "", "Tiêu đề CSV phải là " + STUDENT_HEADER));
            return new StudentResult(students, validRows, errors, false);
        }
        Set<String> seen = new HashSet<>();
        if (existingIds != null) seen.addAll(existingIds);
        for (int index = 1; index < rows.size(); index++) {
            String[] row = rows.get(index);
            int dataRow = index;
            String id = value(row, 0);
            if (row.length != 5) {
                errors.add(new CsvError(dataRow, id, "Dòng phải có đúng 5 cột."));
                continue;
            }
            String name = value(row, 1);
            String dob = value(row, 2);
            String className = value(row, 3);
            String faculty = value(row, 4);
            String invalid = StudentDataValidation.validateStudent(id, name, dob, className, faculty, today);
            if (invalid == null && !seen.add(id)) invalid = "MSSV đã tồn tại trong file hoặc Firestore.";
            if (invalid != null) errors.add(new CsvError(dataRow, id, invalid));
            else {
                Student student = new Student(id, name, dob, className, faculty);
                students.add(student);
                validRows.add(new StudentRow(dataRow, student));
            }
        }
        return new StudentResult(students, validRows, errors, true);
    }

    /** knownStudentIds must contain IDs already in Firestore or accepted in a prior student import. */
    public static CertificateResult parseCertificates(Reader input, Set<String> knownStudentIds,
                                                       LocalDate today) throws IOException {
        List<String[]> rows = parseRecords(input);
        List<CertificateRow> certificates = new ArrayList<>();
        List<CsvError> errors = new ArrayList<>();
        if (!hasHeader(rows, CERTIFICATE_HEADER)) {
            errors.add(new CsvError(0, "", "Tiêu đề CSV phải là " + CERTIFICATE_HEADER));
            return new CertificateResult(certificates, errors, false);
        }
        Set<String> known = knownStudentIds == null ? new HashSet<>() : new HashSet<>(knownStudentIds);
        for (int index = 1; index < rows.size(); index++) {
            String[] row = rows.get(index);
            int dataRow = index;
            String studentId = value(row, 0);
            if (row.length != 4) {
                errors.add(new CsvError(dataRow, studentId, "Dòng phải có đúng 4 cột."));
                continue;
            }
            String certName = value(row, 1);
            String issueDate = value(row, 2);
            String issuer = value(row, 3);
            String invalid = null;
            if (!ValidationUtils.isValidStudentId(studentId)) invalid = "MSSV không đúng định dạng.";
            else if (!known.contains(studentId)) invalid = "Không tìm thấy sinh viên có MSSV này.";
            else invalid = StudentDataValidation.validateCertificate(certName, issueDate, issuer, today);
            if (invalid != null) errors.add(new CsvError(dataRow, studentId, invalid));
            else certificates.add(new CertificateRow(dataRow, studentId,
                    new Certificate(null, certName, issueDate, issuer)));
        }
        return new CertificateResult(certificates, errors, true);
    }

    /** Writer must use UTF-8 when created from a SAF output stream. */
    public static void writeStudents(Writer writer, List<Student> students) throws IOException {
        writer.write(BOM);
        writer.write(STUDENT_HEADER);
        writer.write("\r\n");
        if (students != null) for (Student s : students) {
            writer.write(formatRow(s.getStudentId(), s.getName(), s.getDateOfBirth(),
                    s.getClassName(), s.getFaculty()));
            writer.write("\r\n");
        }
        writer.flush();
    }

    /** Export rows should include all certificates and their owning student IDs. */
    public static void writeCertificates(Writer writer, List<CertificateRow> certificates)
            throws IOException {
        writer.write(BOM);
        writer.write(CERTIFICATE_HEADER);
        writer.write("\r\n");
        if (certificates != null) for (CertificateRow row : certificates) {
            Certificate c = row.certificate;
            writer.write(formatRow(row.studentId, c.getCertName(), c.getIssueDate(), c.getIssuedBy()));
            writer.write("\r\n");
        }
        writer.flush();
    }

    public static String formatRow(String... fields) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) result.append(',');
            String field = fields[i] == null ? "" : fields[i];
            boolean quote = field.indexOf(',') >= 0 || field.indexOf('"') >= 0
                    || field.indexOf('\n') >= 0 || field.indexOf('\r') >= 0
                    || !field.equals(field.trim());
            if (quote) result.append('"').append(field.replace("\"", "\"\"")).append('"');
            else result.append(field);
        }
        return result.toString();
    }

    private static boolean hasHeader(List<String[]> records, String header) {
        if (records.isEmpty()) return false;
        String[] expected = header.split(",", -1);
        String[] actual = records.get(0);
        if (actual.length != expected.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (!expected[i].equalsIgnoreCase(actual[i].trim())) return false;
        }
        return true;
    }

    /** RFC 4180 style quoting, including escaped quotes and line breaks inside quoted fields. */
    private static List<String[]> parseRecords(Reader reader) throws IOException {
        BufferedReader input = reader instanceof BufferedReader
                ? (BufferedReader) reader : new BufferedReader(reader);
        List<String[]> records = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean closedQuote = false;
        boolean firstCharacter = true;
        int read;
        while ((read = input.read()) != -1) {
            char c = (char) read;
            if (firstCharacter) {
                firstCharacter = false;
                if (c == BOM) continue;
            }
            if (quoted) {
                if (c == '"') {
                    input.mark(1);
                    int next = input.read();
                    if (next == '"') field.append('"');
                    else {
                        quoted = false;
                        closedQuote = true;
                        if (next != -1) input.reset();
                    }
                } else field.append(c);
            } else if (closedQuote) {
                if (c == ',') {
                    row.add(field.toString());
                    field.setLength(0);
                    closedQuote = false;
                } else if (c == '\n' || c == '\r') {
                    if (c == '\r') {
                        input.mark(1);
                        int next = input.read();
                        if (next != '\n' && next != -1) input.reset();
                    }
                    row.add(field.toString());
                    if (!isBlank(row)) records.add(row.toArray(new String[0]));
                    row = new ArrayList<>();
                    field.setLength(0);
                    closedQuote = false;
                } else {
                    throw new IOException("CSV có ký tự thừa sau dấu ngoặc kép đóng.");
                }
            } else if (c == '"' && field.length() == 0) {
                quoted = true;
            } else if (c == '"') {
                throw new IOException("CSV có dấu ngoặc kép không hợp lệ.");
            } else if (c == ',') {
                row.add(field.toString());
                field.setLength(0);
            } else if (c == '\n' || c == '\r') {
                if (c == '\r') {
                    input.mark(1);
                    int next = input.read();
                    if (next != '\n' && next != -1) input.reset();
                }
                row.add(field.toString());
                if (!isBlank(row)) records.add(row.toArray(new String[0]));
                row = new ArrayList<>();
                field.setLength(0);
            } else field.append(c);
        }
        if (quoted) throw new IOException("CSV có ô chưa đóng dấu ngoặc kép.");
        if (field.length() > 0 || !row.isEmpty()) {
            row.add(field.toString());
            if (!isBlank(row)) records.add(row.toArray(new String[0]));
        }
        return records;
    }

    private static boolean isBlank(List<String> values) {
        for (String value : values) if (!value.trim().isEmpty()) return false;
        return true;
    }

    private static String value(String[] row, int index) {
        return index < row.length && row[index] != null ? row[index].trim() : "";
    }
}
