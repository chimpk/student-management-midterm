package com.example.studentmgmt.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidationUtilsTest {

    @Test
    public void demoUsernames_areValid() {
        assertTrue(ValidationUtils.isValidUsername("admin"));
        assertTrue(ValidationUtils.isValidUsername("manager"));
        assertTrue(ValidationUtils.isValidUsername("employee"));
    }

    @Test
    public void invalidUsernames_areRejected() {
        assertFalse(ValidationUtils.isValidUsername(null));
        assertFalse(ValidationUtils.isValidUsername(""));
        assertFalse(ValidationUtils.isValidUsername("ab"));
        assertFalse(ValidationUtils.isValidUsername("Admin"));
        assertFalse(ValidationUtils.isValidUsername("user name"));
        assertFalse(ValidationUtils.isValidUsername("user@email.com"));
        assertFalse(ValidationUtils.isValidUsername("a23456789012345678901234567890123"));
    }

    @Test
    public void validStudentIds_followTdtuKyyTssssFormat() {
        assertTrue(ValidationUtils.isValidStudentId("524H0123"));
        assertTrue(ValidationUtils.isValidStudentId("52400001"));
        assertTrue(ValidationUtils.isValidStudentId("A24H0000"));
        assertTrue(ValidationUtils.isValidStudentId("H9909999"));
    }

    @Test
    public void invalidStudentIds_areRejected() {
        assertFalse(ValidationUtils.isValidStudentId(null));
        assertFalse(ValidationUtils.isValidStudentId(""));
        assertFalse(ValidationUtils.isValidStudentId("524H123"));
        assertFalse(ValidationUtils.isValidStudentId("524H01234"));
        assertFalse(ValidationUtils.isValidStudentId("I24H0123"));
        assertFalse(ValidationUtils.isValidStudentId("524K0123"));
        assertFalse(ValidationUtils.isValidStudentId("5A4H0123"));
        assertFalse(ValidationUtils.isValidStudentId("524H01A3"));
        assertFalse(ValidationUtils.isValidStudentId("524h0123"));
        assertFalse(ValidationUtils.isValidStudentId(" 524H0123"));
    }

    @Test
    public void classCode_requiresEightUppercaseAlphanumericCharacters() {
        assertTrue(ValidationUtils.isValidClassCode("21050201"));
        assertTrue(ValidationUtils.isValidClassCode("21H50301"));
        assertTrue(ValidationUtils.isValidClassCode("24K50402"));

        assertFalse(ValidationUtils.isValidClassCode(null));
        assertFalse(ValidationUtils.isValidClassCode("2105020"));
        assertFalse(ValidationUtils.isValidClassCode("210502011"));
        assertFalse(ValidationUtils.isValidClassCode("21h50301"));
        assertFalse(ValidationUtils.isValidClassCode("21-50301"));
        assertFalse(ValidationUtils.isValidClassCode(" 21050201"));
    }

    @Test
    public void knownItClassCodes_areRecognizedWithoutClaimingExhaustiveness() {
        assertTrue(ValidationUtils.isKnownItClassCode("21050201"));
        assertTrue(ValidationUtils.isKnownItClassCode("21050301"));
        assertTrue(ValidationUtils.isKnownItClassCode("21050401"));
        assertTrue(ValidationUtils.isKnownItClassCode("21H50301"));

        assertFalse(ValidationUtils.isKnownItClassCode("21050501"));
        assertFalse(ValidationUtils.isKnownItClassCode("24K50301"));
        assertFalse(ValidationUtils.isKnownItClassCode("2105030"));
    }
}
