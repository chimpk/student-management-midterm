package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.studentmgmt.model.Certificate;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.utils.StudentCsvHelper;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for Member 2 models and CSV utility.
 */
public class StudentModelAndCsvTest {

    @Test
    public void studentModel_propertiesAndEquality() {
        Student s1 = new Student("524H0001", "Nguyễn Văn An", "2003-01-15", "24H50301", "Công nghệ thông tin");
        Student s2 = new Student("524H0001", "Khác Tên", "2003-01-15", "24H50301", "Công nghệ thông tin");
        Student s3 = new Student("524H0002", "Trần Thị Mai", "2003-04-20", "24H50301", "Công nghệ thông tin");

        assertEquals("524H0001", s1.getStudentId());
        assertEquals("Nguyễn Văn An", s1.getName());
        assertEquals("2003-01-15", s1.getDateOfBirth());
        assertEquals("24H50301", s1.getClassName());
        assertEquals("Công nghệ thông tin", s1.getFaculty());

        assertEquals("Same studentId implies equality", s1, s2);
        assertEquals("Hashcode must match for same studentId", s1.hashCode(), s2.hashCode());
        assertFalse("Different studentId must not be equal", s1.equals(s3));
    }

    @Test
    public void certificateModel_propertiesAndEquality() {
        Certificate c1 = new Certificate("cert_1", "TOEIC 750", "2024-05-10", "IIG Vietnam");
        Certificate c2 = new Certificate("cert_1", "TOEIC 800", "2024-05-10", "IIG Vietnam");
        Certificate c3 = new Certificate("cert_2", "IELTS 6.5", "2024-03-15", "British Council");

        assertEquals("cert_1", c1.getCertId());
        assertEquals("TOEIC 750", c1.getCertName());
        assertEquals("2024-05-10", c1.getIssueDate());
        assertEquals("IIG Vietnam", c1.getIssuedBy());

        assertEquals(c1, c2);
        assertFalse(c1.equals(c3));
    }

    @Test
    public void csvHelper_exportAndImportRoundtrip() throws IOException {
        List<Student> original = new ArrayList<>();
        original.add(new Student("524H0001", "Nguyễn Văn An", "2003-01-15", "24H50301", "Công nghệ thông tin"));
        original.add(new Student("524H0002", "Trần Thị Mai", "2003-04-20", "24H50301", "Công nghệ thông tin"));

        String csv = StudentCsvHelper.exportToCsv(original);
        assertNotNull(csv);
        assertTrue(csv.contains("524H0001"));
        assertTrue(csv.contains("Nguyễn Văn An"));
        assertTrue(csv.contains("524H0002"));

        List<Student> imported = StudentCsvHelper.importFromCsv(csv);
        assertEquals(2, imported.size());
        assertEquals("524H0001", imported.get(0).getStudentId());
        assertEquals("Nguyễn Văn An", imported.get(0).getName());
        assertEquals("524H0002", imported.get(1).getStudentId());
        assertEquals("Trần Thị Mai", imported.get(1).getName());
    }

    @Test
    public void csvHelper_handlesEmptyAndNullGracefully() throws IOException {
        assertEquals(StudentCsvHelper.UTF8_BOM + StudentCsvHelper.CSV_HEADER + "\n",
                StudentCsvHelper.exportToCsv(null));
        assertEquals(StudentCsvHelper.UTF8_BOM + StudentCsvHelper.CSV_HEADER + "\n",
                StudentCsvHelper.exportToCsv(new ArrayList<>()));

        assertTrue(StudentCsvHelper.importFromCsv(null).isEmpty());
        assertTrue(StudentCsvHelper.importFromCsv("").isEmpty());
        assertTrue(StudentCsvHelper.importFromCsv("   \n\n  ").isEmpty());
    }

    @Test
    public void csvHelper_handlesEscapedCommasAndQuotesRfc4180() throws IOException {
        String csv = "Mã SV,Họ và tên,Ngày sinh,Lớp,Khoa\n"
                + "524H0099,\"Nguyễn, Văn \"\"A\"\"\",15/05/2003,24H50301,\"Khoa CNTT, ĐH Tôn Đức Thắng\"\n";

        List<Student> imported = StudentCsvHelper.importFromCsv(csv);
        assertEquals(1, imported.size());
        Student s = imported.get(0);
        assertEquals("524H0099", s.getStudentId());
        assertEquals("Nguyễn, Văn \"A\"", s.getName());
        assertEquals("2003-05-15", s.getDateOfBirth());
        assertEquals("24H50301", s.getClassName());
        assertEquals("Khoa CNTT, ĐH Tôn Đức Thắng", s.getFaculty());
    }
}
