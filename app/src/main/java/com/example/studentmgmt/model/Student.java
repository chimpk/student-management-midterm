package com.example.studentmgmt.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * TODO(member-2): Student data model for Member 2 (Nguyễn Vĩnh Khâm).
 * Represents a student document in Firestore collection "students/{studentId}".
 */
public class Student implements Serializable {

    private String studentId;
    private String name;
    private String dateOfBirth; // YYYY-MM-DD
    private String className;
    private String faculty;
    private long createdAt;
    private int certificateCount;

    // Default constructor required for Firestore serialization
    public Student() {
    }

    public Student(String studentId, String name, String dateOfBirth, String className, String faculty) {
        this(studentId, name, dateOfBirth, className, faculty, System.currentTimeMillis(), 0);
    }

    public Student(String studentId, String name, String dateOfBirth, String className, String faculty, long createdAt, int certificateCount) {
        this.studentId = studentId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.className = className;
        this.faculty = faculty;
        this.createdAt = createdAt;
        this.certificateCount = certificateCount;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getCertificateCount() {
        return certificateCount;
    }

    public void setCertificateCount(int certificateCount) {
        this.certificateCount = certificateCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", faculty='" + faculty + '\'' +
                '}';
    }
}
