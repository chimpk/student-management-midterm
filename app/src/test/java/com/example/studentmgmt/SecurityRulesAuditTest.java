package com.example.studentmgmt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Static audit harness for firestore.rules and storage.rules.
 * Verifies security boundary constraints, authority sources,
 * privilege escalation blocks, fail-closed locked user checks, and storage ownership rules.
 */
public class SecurityRulesAuditTest {

    private File projectRoot;
    private String firestoreRules;
    private String storageRules;

    @Before
    public void setUp() throws IOException {
        File currentDir = new File(".").getCanonicalFile();
        if (currentDir.getName().equals("app")) {
            projectRoot = currentDir.getParentFile();
        } else {
            projectRoot = currentDir;
        }

        File fRulesFile = new File(projectRoot, "firestore.rules");
        File sRulesFile = new File(projectRoot, "storage.rules");

        assertTrue("firestore.rules must exist", fRulesFile.exists());
        assertTrue("storage.rules must exist", sRulesFile.exists());

        firestoreRules = new String(Files.readAllBytes(fRulesFile.toPath()), StandardCharsets.UTF_8);
        storageRules = new String(Files.readAllBytes(sRulesFile.toPath()), StandardCharsets.UTF_8);
    }

    @Test
    public void firestoreRules_locksMissingAndLockedUsers() {
        // Enforces that isActiveUser() checks document existence and status == 'Normal'
        assertTrue("isActiveUser must verify user document existence",
                firestoreRules.contains("function isActiveUser()"));
        assertTrue("isActiveUser must require status == 'Normal'",
                firestoreRules.contains("currentUser().data.status == 'Normal'"));
        assertTrue("isActiveUser must require document existence",
                firestoreRules.contains("userExists()"));

        // All main collections must check isActiveUser
        assertTrue("Users read must check isActiveUser",
                firestoreRules.contains("allow read: if isActiveUser()"));
        assertTrue("Students read must check isActiveUser",
                firestoreRules.contains("match /students/{studentId} {\n      allow read: if isActiveUser();")
                        || firestoreRules.contains("match /students/{studentId} {\r\n      allow read: if isActiveUser();")
                        || firestoreRules.contains("allow read: if isActiveUser();"));
    }

    @Test
    public void firestoreRules_authoritySourceDerivesFromStoredDocument() {
        // Enforces that role is read from existing currentUser() document, NEVER request.resource.data
        assertTrue("isAdmin must require isActiveUser and check currentUser().data.role == 'admin'",
                firestoreRules.contains("function isAdmin() {\n      return isActiveUser() && currentUser().data.role == 'admin';")
                        || firestoreRules.contains("function isAdmin() {\r\n      return isActiveUser() && currentUser().data.role == 'admin';")
                        || (firestoreRules.contains("function isAdmin()") && firestoreRules.contains("currentUser().data.role == 'admin'")));

        assertTrue("isManagerOrAdmin must check currentUser().data.role in ['admin', 'manager']",
                firestoreRules.contains("currentUser().data.role in ['admin', 'manager']"));

        assertFalse("Authority check must NEVER read role from request.resource.data",
                firestoreRules.contains("request.resource.data.role == 'admin'")
                        && !firestoreRules.contains("request.resource.data.role in ['admin', 'manager', 'employee']"));
    }

    @Test
    public void firestoreRules_selfProfileUpdateWhitelistEnforced() {
        assertTrue("Self profile update must allow only personal fields and avatarUrl",
                firestoreRules.contains("affectedKeys().hasOnly(['name', 'age', 'phone', 'avatarUrl'])"));
        assertTrue("Self profile update must satisfy the complete user schema",
                firestoreRules.contains("request.auth.uid == userId\n          && validUser(userId)"));
        assertTrue("Self profile update must bound avatarUrl length to <= 2048",
                firestoreRules.contains("request.resource.data.avatarUrl.size() <= 2048"));
    }

    @Test
    public void firestoreRules_createVsUpdateBypassBlocked() {
        // Users: createdAt and uid immutable on update
        assertTrue("Users update must lock createdAt",
                firestoreRules.contains("request.resource.data.createdAt == resource.data.createdAt"));
        assertTrue("Users update must lock uid",
                firestoreRules.contains("request.resource.data.uid == resource.data.uid"));

        // Students: createdAt and studentId immutable on update
        assertTrue("Students update must lock createdAt",
                firestoreRules.contains("request.resource.data.createdAt == resource.data.createdAt"));
        assertTrue("Students update must lock studentId",
                firestoreRules.contains("request.resource.data.studentId == resource.data.studentId"));
    }

    @Test
    public void firestoreRules_userManagementRestrictedToAdmin() {
        // Only Admin can create and delete users
        assertTrue("User create must require isAdmin()",
                firestoreRules.contains("allow create: if isAdmin()"));
        assertTrue("User delete must require isAdmin()",
                firestoreRules.contains("allow delete: if isAdmin();"));
    }

    @Test
    public void firestoreRules_studentsAndCertificatesRequireManagerOrAdmin() {
        // Write on students requires isManagerOrAdmin()
        assertTrue("Student create requires isManagerOrAdmin()",
                firestoreRules.contains("allow create: if isManagerOrAdmin()"));
        assertTrue("Student update requires isManagerOrAdmin()",
                firestoreRules.contains("allow update: if isManagerOrAdmin()"));
        assertTrue("Student delete requires isManagerOrAdmin()",
                firestoreRules.contains("allow delete: if isManagerOrAdmin();"));

        // Write on certificates requires isManagerOrAdmin()
        assertTrue("Certificate write requires isManagerOrAdmin()",
                firestoreRules.contains("allow create, update: if isManagerOrAdmin()"));
    }

    @Test
    public void storageRules_enforcesOwnershipMimeAndSize() {
        // Path matches avatars/{fileName}
        assertTrue("Storage rules must match /avatars/{fileName}",
                storageRules.contains("match /avatars/{fileName}"));

        // UID ownership check
        assertTrue("Storage filename must equal request.auth.uid + .jpg/.jpeg/.png",
                storageRules.contains("fileName == request.auth.uid + '.jpg'")
                        && storageRules.contains("fileName == request.auth.uid + '.jpeg'")
                        && storageRules.contains("fileName == request.auth.uid + '.png'"));

        // Max size under 5 MiB
        assertTrue("Storage size must be under 5 MiB",
                storageRules.contains("request.resource.size < 5 * 1024 * 1024"));

        // Content types restricted to image/jpeg and image/png
        assertTrue("Storage contentType must be image/jpeg or image/png",
                storageRules.contains("request.resource.contentType in ['image/jpeg', 'image/png']"));

        // Delete restricted to active user and owner
        assertTrue("Delete must require active user and owner",
                storageRules.contains("allow delete: if isActiveUser()")
                        && storageRules.contains("isAvatarOwner(fileName)"));
    }

    @Test
    public void storageRules_enforcesActiveUserAndCrossServiceLookup() {
        // Cross-service lookup to Cloud Firestore default database
        assertTrue("Storage rules must use rules_version = '2'",
                storageRules.contains("rules_version = '2';"));
        assertTrue("Storage rules must use firestore.get with default database",
                storageRules.contains("firestore.get(/databases/(default)/documents/users/$(request.auth.uid))"));
        assertTrue("Storage rules must use firestore.exists with default database",
                storageRules.contains("firestore.exists(/databases/(default)/documents/users/$(request.auth.uid))"));

        // Fail-closed isActiveUser() checking exists, status == 'Normal', and valid role
        assertTrue("Storage rules must declare isActiveUser()",
                storageRules.contains("function isActiveUser()"));
        assertTrue("Storage rules isActiveUser must check status == 'Normal'",
                storageRules.contains("userDoc().data.status == 'Normal'"));
        assertTrue("Storage rules isActiveUser must check role in admin, manager, employee",
                storageRules.contains("userDoc().data.role in ['admin', 'manager', 'employee']"));

        // All operations (read, create, update, delete) must be guarded by isActiveUser
        assertTrue("Storage read must require isActiveUser()",
                storageRules.contains("allow read: if isActiveUser();"));
        assertTrue("Storage create, update must require isActiveUser()",
                storageRules.contains("allow create, update: if isActiveUser()"));
    }

    @Test
    public void firestoreRules_loginHistoryIsCreateOnly_disallowsUpdateAndDelete() {
        // Find loginHistory subcollection block
        int historyIdx = firestoreRules.indexOf("match /loginHistory/{logId}");
        assertTrue("match /loginHistory/{logId} must exist", historyIdx >= 0);
        String historyBlock = firestoreRules.substring(historyIdx, firestoreRules.indexOf("}", historyIdx + 30) + 1);

        assertTrue("loginHistory must allow read", historyBlock.contains("allow read:"));
        assertTrue("loginHistory must allow create", historyBlock.contains("allow create:"));

        assertFalse("loginHistory must be immutable/create-only: NO allow update",
                historyBlock.contains("allow update:"));
        assertFalse("loginHistory must be append-only: NO allow delete",
                historyBlock.contains("allow delete:"));
    }
}
