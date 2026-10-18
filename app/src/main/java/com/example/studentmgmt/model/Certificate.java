package com.example.studentmgmt.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * TODO(member-2): Certificate data model for Member 2 (Nguyễn Vĩnh Khâm).
 * Represents a document in Firestore subcollection "students/{studentId}/certificates/{certId}".
 */
public class Certificate implements Serializable {

    private String certId;
    private String certName;
    private String issueDate; // YYYY-MM-DD
    private String issuedBy;

    // Default constructor required for Firestore
    public Certificate() {
    }

    public Certificate(String certId, String certName, String issueDate, String issuedBy) {
        this.certId = certId;
        this.certName = certName;
        this.issueDate = issueDate;
        this.issuedBy = issuedBy;
    }

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Certificate that = (Certificate) o;
        return Objects.equals(certId, that.certId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certId);
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "certId='" + certId + '\'' +
                ", certName='" + certName + '\'' +
                ", issuedBy='" + issuedBy + '\'' +
                '}';
    }
}
