package com.AutomationExercise.utils.report;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.utils.system.OSUtils;
import com.AutomationExercise.utils.system.TerminalUtils;
import com.AutomationExercise.utils.time.TimeManager;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.AutomationExercise.utils.data.reader.PropertyReader.getProperty;
import static com.AutomationExercise.utils.report.AllureConstants.HISTORY_FOLDER;
import static com.AutomationExercise.utils.report.AllureConstants.RESULTS_HISTORY_FOLDER;

public class AllureReportGenerator {
    public static boolean generateReports(boolean isSingleFile) {
        Path outputFolder = isSingleFile ? AllureConstants.REPORT_PATH : AllureConstants.FULL_REPORT_PATH;
        try {
            Files.createDirectories(AllureConstants.RESULTS_FOLDER);
            Files.createDirectories(outputFolder);

            List<String> command = new ArrayList<>();
            command.add(AllureBinaryManager.getExecutable().toString());
            command.add("generate");
            command.add(AllureConstants.RESULTS_FOLDER.toString());
            command.add("-o");
            command.add(outputFolder.toString());
            command.add("--clean");
            if (isSingleFile) {
                command.add("--single-file");
            }
            TerminalUtils.executeTerminalCommand(command.toArray(new String[0]));
            if (!isSingleFile) {
                persistGeneratedHistory();
            }
            return true;
        } catch (Exception e) {
            // Report generation must not hide the original test result; the raw results are archived later.
            LogsManager.error("Unable to generate Allure report:", e.getMessage());
            return false;
        }
    }

    // Open the generated single-file report in the local browser.
    public static void openReport(String reportFileName) {
        String executionType = getProperty("executionType");
        if (executionType == null || !executionType.toLowerCase().contains("local")) {
            return;
        }
        Path reportPath = AllureConstants.REPORT_PATH.resolve(reportFileName);
        switch (OSUtils.getCurrentOS()) {
            case WINDOWS -> TerminalUtils.executeTerminalCommand("cmd.exe", "/c", "start", "", reportPath.toString());
            case MAC -> TerminalUtils.executeTerminalCommand("open", reportPath.toString());
            case LINUX -> TerminalUtils.executeTerminalCommand("xdg-open", reportPath.toString());
            default -> LogsManager.warn("Opening Allure Report is not supported on this OS.");
        }
    }

    public static String renameReport() {
        String newFileName = AllureConstants.REPORT_PREFIX + TimeManager.getTimestamp()
                + AllureConstants.REPORT_EXTENSION;
        Path oldPath = AllureConstants.REPORT_PATH.resolve(AllureConstants.INDEX_HTML);
        Path newPath = AllureConstants.REPORT_PATH.resolve(newFileName);
        com.AutomationExercise.utils.file.FileUtils.renameFile(oldPath.toString(), newPath.toString());
        return newFileName;
    }

    /** Copy the previous report's history into the fresh results directory before the next run. */
    public static void copyHistory() {
        try {
            if (!Files.isRegularFile(HISTORY_FOLDER.resolve("history.json"))) {
                LogsManager.info("No previous Allure history found; this will be the first trend entry.");
                return;
            }
            Files.createDirectories(RESULTS_HISTORY_FOLDER);
            FileUtils.copyDirectory(HISTORY_FOLDER.toFile(), RESULTS_HISTORY_FOLDER.toFile());
            LogsManager.info("Previous Allure history copied to:", RESULTS_HISTORY_FOLDER.toString());
        } catch (Exception e) {
            LogsManager.error("Error copying Allure history:", e.getMessage());
        }
    }

    /** Persist the new report history outside the report folder that --clean replaces. */
    private static void persistGeneratedHistory() {
        Path generatedHistory = AllureConstants.FULL_REPORT_PATH.resolve("history");
        try {
            if (!Files.isDirectory(generatedHistory)) {
                LogsManager.warn("Allure generated report has no history directory yet.");
                return;
            }
            if (Files.exists(AllureConstants.HISTORY_FOLDER)) {
                FileUtils.cleanDirectory(AllureConstants.HISTORY_FOLDER.toFile());
            }
            Files.createDirectories(AllureConstants.HISTORY_FOLDER);
            FileUtils.copyDirectory(generatedHistory.toFile(), AllureConstants.HISTORY_FOLDER.toFile());
            LogsManager.info("Allure history persisted:", AllureConstants.HISTORY_FOLDER.toString());
        } catch (Exception e) {
            LogsManager.error("Unable to persist Allure history:", e.getMessage());
        }
    }
}
