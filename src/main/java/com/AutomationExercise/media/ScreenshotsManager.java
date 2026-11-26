package com.AutomationExercise.media;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.utils.report.AllureAttachmentManager;
import com.AutomationExercise.utils.report.AllureConstants;
import com.AutomationExercise.utils.time.TimeManager;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ScreenshotsManager {
    public static final Path SCREENSHOTS_DIRECTORY = AllureConstants.SCREENSHOTS_FOLDER;
    // Kept as String for compatibility with TestNGListeners and existing callers.
    public static final String SCREENSHOTS_PATH = SCREENSHOTS_DIRECTORY.toString();

    public static void takeFullPageScreenshot(WebDriver driver, String screenshotName) {
        try {
            if (driver == null) {
                LogsManager.warn("Screenshot skipped: WebDriver is null for", screenshotName);
                return;
            }
            File screenshotSource = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path screenshotDestination = createScreenshotPath(screenshotName);
            Files.copy(screenshotSource.toPath(), screenshotDestination, StandardCopyOption.REPLACE_EXISTING);
            LogsManager.info("Full-page screenshot captured:", screenshotDestination.toAbsolutePath().toString());
            AllureAttachmentManager.attachScreenshot(screenshotName, screenshotDestination.toString());
        } catch (Exception e) {
            LogsManager.error("Failed to capture screenshot:", e.getMessage());
        }
    }

    public static void takeElementScreenshot(WebDriver driver, By locator) {
        try {
            WebElement element = driver.findElement(locator);
            File screenshotSource = element.getScreenshotAs(OutputType.FILE);
            String accessibleName = element.getAccessibleName();
            Path screenshotDestination = createScreenshotPath(
                    accessibleName == null || accessibleName.isBlank() ? "element" : accessibleName);
            Files.copy(screenshotSource.toPath(), screenshotDestination, StandardCopyOption.REPLACE_EXISTING);
            LogsManager.info("Element screenshot captured:", screenshotDestination.toAbsolutePath().toString(),
                    "| Locator:", locator.toString());
            AllureAttachmentManager.attachScreenshot("Element screenshot: " + accessibleName,
                    screenshotDestination.toString());
        } catch (Exception e) {
            LogsManager.error("Failed to capture element screenshot:", e.getMessage());
        }
    }

    private static Path createScreenshotPath(String name) throws java.io.IOException {
        Files.createDirectories(SCREENSHOTS_DIRECTORY);
        String safeName = name == null ? "screenshot" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (safeName.isBlank()) {
            safeName = "screenshot";
        }
        String uniqueSuffix = TimeManager.getSimpleTimestamp()
                + "-thread-" + Thread.currentThread().getId();
        return SCREENSHOTS_DIRECTORY.resolve(safeName + "-" + uniqueSuffix + ".png");
    }
}
