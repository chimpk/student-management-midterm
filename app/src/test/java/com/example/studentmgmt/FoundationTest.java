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
}
