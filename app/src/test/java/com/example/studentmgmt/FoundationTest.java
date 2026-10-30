package com.example.studentmgmt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Foundation and resource verification tests (T00).
 * Verifies that root activity does not have fake back button,
 * launcher vector drawables have valid pathData starting with 'M',
 * dark mode text color is not hard-coded in root layout,
 * and Unix gradlew wrapper script exists.
 */
public class FoundationTest {

    private File projectRoot;

    @Before
    public void setUp() throws IOException {
        File currentDir = new File(".").getCanonicalFile();
        if (currentDir.getName().equals("app")) {
            projectRoot = currentDir.getParentFile();
        } else {
            projectRoot = currentDir;
        }
    }

    @Test
    public void unixGradlewWrapper_exists() {
        File gradlew = new File(projectRoot, "gradlew");
        assertTrue("Unix gradlew wrapper must exist", gradlew.exists() && gradlew.isFile());
    }

    @Test
    public void launcherVectorDrawables_haveValidPathDataStartingWithMove() throws IOException {
        File bgFile = new File(projectRoot, "app/src/main/res/drawable/ic_launcher_background.xml");
        File fgFile = new File(projectRoot, "app/src/main/res/drawable/ic_launcher_foreground.xml");

        assertTrue("ic_launcher_background.xml must exist", bgFile.exists());
        assertTrue("ic_launcher_foreground.xml must exist", fgFile.exists());

        String bgContent = new String(Files.readAllBytes(bgFile.toPath()), StandardCharsets.UTF_8);
        String fgContent = new String(Files.readAllBytes(fgFile.toPath()), StandardCharsets.UTF_8);

        Pattern pathDataPattern = Pattern.compile("android:pathData=\"([^\"]+)\"");

        Matcher bgMatcher = pathDataPattern.matcher(bgContent);
        assertTrue("Background drawable must contain pathData", bgMatcher.find());
        String bgPath = bgMatcher.group(1).trim();
        assertTrue("Background pathData must start with 'M' (move-to), got: " + bgPath,
                bgPath.startsWith("M") || bgPath.startsWith("m"));

        Matcher fgMatcher = pathDataPattern.matcher(fgContent);
        assertTrue("Foreground drawable must contain pathData", fgMatcher.find());
        String fgPath = fgMatcher.group(1).trim();
        assertTrue("Foreground pathData must start with 'M' (move-to), got: " + fgPath,
                fgPath.startsWith("M") || fgPath.startsWith("m"));
    }

    @Test
    public void activityMain_doesNotHardcodeBlackTextColor() throws IOException {
        File layoutFile = new File(projectRoot, "app/src/main/res/layout/activity_main.xml");
        assertTrue("activity_main.xml must exist", layoutFile.exists());

        String layoutContent = new String(Files.readAllBytes(layoutFile.toPath()), StandardCharsets.UTF_8);
        assertFalse("activity_main.xml should not hardcode android:textColor to @color/black",
                layoutContent.contains("android:textColor=\"@color/black\""));
    }

    @Test
    public void mainActivity_doesNotEnableFakeBackNavigationAtRoot() throws IOException {
        File mainActivityFile = new File(projectRoot, "app/src/main/java/com/example/studentmgmt/MainActivity.java");
        assertTrue("MainActivity.java must exist", mainActivityFile.exists());

        String code = new String(Files.readAllBytes(mainActivityFile.toPath()), StandardCharsets.UTF_8);
        assertFalse("MainActivity should not enable setDisplayHomeAsUpEnabled(true) at root",
                code.contains("setDisplayHomeAsUpEnabled(true)"));
    }

    @Test
    public void dashboardRoleLayouts_doNotUseAutoColumnsForVisibleCards() throws IOException {
        File dashboardFile = new File(projectRoot,
                "app/src/main/java/com/example/studentmgmt/ui/dashboard/DashboardFragment.java");
        assertTrue("DashboardFragment.java must exist", dashboardFile.exists());

        String code = new String(Files.readAllBytes(dashboardFile.toPath()), StandardCharsets.UTF_8);
        assertFalse("Role-specific dashboard cards need explicit columns so hidden cards leave no gaps",
                code.contains("GridLayout.spec(GridLayout.UNDEFINED"));
    }

    @Test
    public void employeeEmptyStates_hideTheActionContainerSoItsMarginCollapses() throws IOException {
        File studentListFile = new File(projectRoot,
                "app/src/main/java/com/example/studentmgmt/ui/student/StudentListFragment.java");
        File studentDetailFile = new File(projectRoot,
                "app/src/main/java/com/example/studentmgmt/ui/student/StudentDetailFragment.java");

        String studentListCode = new String(Files.readAllBytes(studentListFile.toPath()), StandardCharsets.UTF_8);
        String studentDetailCode = new String(Files.readAllBytes(studentDetailFile.toPath()), StandardCharsets.UTF_8);
        String collapseActions = "layoutEmptyActions.setVisibility(View.GONE)";

        assertTrue("Employee student empty state must collapse the whole action row",
                studentListCode.contains(collapseActions));
        assertTrue("Employee certificate empty state must collapse the whole action row",
                studentDetailCode.contains(collapseActions));
    }

    @Test
    public void studentListToolbar_appliesRolePolicyBeforeShowingUserManagement() throws IOException {
        File studentListFile = new File(projectRoot,
                "app/src/main/java/com/example/studentmgmt/ui/student/StudentListFragment.java");
        String code = new String(Files.readAllBytes(studentListFile.toPath()), StandardCharsets.UTF_8);

        assertTrue("Student toolbar must hide User Management for Manager and Employee",
                code.contains("RoleNavigationPolicy.shouldShowMenuItem"));
    }

    @Test
    public void profileScreen_exposesAValidatedPersonalInfoEditDialog() throws IOException {
        File profileLayout = new File(projectRoot, "app/src/main/res/layout/fragment_profile.xml");
        File editDialog = new File(projectRoot, "app/src/main/res/layout/dialog_profile_edit.xml");
        File profileFragment = new File(projectRoot,
                "app/src/main/java/com/example/studentmgmt/ui/profile/ProfileFragment.java");

        String layout = new String(Files.readAllBytes(profileLayout.toPath()), StandardCharsets.UTF_8);
        String fragment = new String(Files.readAllBytes(profileFragment.toPath()), StandardCharsets.UTF_8);
        assertTrue("Profile must expose an edit action", layout.contains("@+id/btn_edit_profile"));
        assertTrue("Profile edit dialog layout must exist", editDialog.isFile());
        assertTrue("Profile edit action must open the dialog", fragment.contains("showEditProfileDialog"));
    }
}
