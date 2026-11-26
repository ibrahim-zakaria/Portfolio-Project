package com.AutomationExercise.utils.report;

import com.AutomationExercise.utils.logs.LogsManager;
import io.qameta.allure.Allure;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.AutomationExercise.utils.data.reader.PropertyReader.getProperty;

public class AllureAttachmentManager {
    public static void attachScreenshot(String name, String path) {
        try {
            Path screenshot = Path.of(path);
            if (Files.isRegularFile(screenshot)) {
                try (var input = Files.newInputStream(screenshot)) {
                    Allure.addAttachment(name, input);
                }
            } else {
                LogsManager.error("Screenshot not found:", screenshot.toString());
            }
        } catch (Exception e) {
            LogsManager.error("Error attaching screenshot:", e.getMessage());
        }
    }

    public static void attachLogs() {
        try {
            Path logFile = LogsManager.LOGS_FILE;
            if (Files.isRegularFile(logFile)) {
                Allure.attachment("Logs.log", Files.readString(logFile));
            } else {
                LogsManager.warn("Log file not found for Allure attachment:", logFile.toString());
            }
        } catch (Exception e) {
            LogsManager.error("Error attaching logs:", e.getMessage());
        }
    }

    public static void attachRecords(String testMethodName) {
        if (!Boolean.parseBoolean(getProperty("recordTests"))) {
            return;
        }
        try {
            Path record = AllureConstants.RECORDINGS_FOLDER.resolve(testMethodName + ".mp4");
            if (Files.isRegularFile(record)) {
                try (var input = Files.newInputStream(record)) {
                    Allure.addAttachment(testMethodName, "video/mp4", input, ".mp4");
                }
            } else {
                LogsManager.warn("Recording file not found:", record.toString());
            }
        } catch (Exception e) {
            LogsManager.error("Error attaching recording:", e.getMessage());
        }
    }
}
