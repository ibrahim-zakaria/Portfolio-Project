package com.AutomationExercise.listeners;

import com.AutomationExercise.drivers.WebDriverProvider;
import com.AutomationExercise.media.ScreenRecordManager;
import com.AutomationExercise.media.ScreenshotsManager;
import com.AutomationExercise.utils.data.reader.PropertyReader;
import com.AutomationExercise.utils.file.FileUtils;
import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.utils.report.AllureAttachmentManager;
import com.AutomationExercise.utils.report.AllureConstants;
import com.AutomationExercise.utils.report.AllureEnvironmentManager;
import com.AutomationExercise.utils.report.AllureExecutorManager;
import com.AutomationExercise.utils.report.AllureReportGenerator;
import com.AutomationExercise.validations.Validation;
import org.openqa.selenium.WebDriver;
import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

/** Centralizes test lifecycle reporting, soft-assertion finalization, and artifact collection. */
public class TestNGListeners implements IExecutionListener, IInvokedMethodListener, ITestListener {

    private static final DateTimeFormatter EXECUTION_ID_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private String executionId;

    @Override
    public void onExecutionStart() {
        // Release the previous file appender before clearing the current execution log.
        LogsManager.resetForNewExecution();
        cleanTestOutputDirectories();
        createTestOutputDirectories();
        LogsManager.reconfigureAfterCleanup();

        executionId = LocalDateTime.now().format(EXECUTION_ID_FORMAT);
        System.setProperty("taf.execution.id", executionId);
        PropertyReader.loadProperties();

        LogsManager.info("============================================================");
        LogsManager.info("Test execution started. Execution ID:", executionId);
        LogsManager.info("Project directory:", System.getProperty("user.dir"));
        LogsManager.info("Artifact output directory:", AllureConstants.OUTPUT_FOLDER.toString());
        LogsManager.info("Selected browser parameter:", String.valueOf(System.getProperty("browser", "from properties")));
        LogsManager.info("Selected TestNG groups:", String.valueOf(System.getProperty("groups", "all discovered groups")));

        // Copy trend history after fresh results are cleaned and before the first test starts.
        AllureReportGenerator.copyHistory();
        AllureEnvironmentManager.setupAllureEnvironmentManager();
        AllureExecutorManager.setupExecutor();
        LogsManager.info("Allure environment and executor metadata setup completed.");
    }

    @Override
    public void onExecutionFinish() {
        boolean fullReportGenerated = false;
        boolean singleFileReportGenerated = false;
        try {
            fullReportGenerated = AllureReportGenerator.generateReports(false);
            singleFileReportGenerated = AllureReportGenerator.generateReports(true);
            archiveExecutionArtifacts(fullReportGenerated, singleFileReportGenerated);
            LogsManager.info("Test execution finished. Execution ID:", executionId);
        } catch (Exception e) {
            LogsManager.error("Test execution finalization failed:", e.getClass().getSimpleName(),
                    "-", String.valueOf(e.getMessage()));
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (!method.isTestMethod()) {
            return;
        }

        Validation.beginTest();
        ScreenRecordManager.setRecording();
        String className = testResult.getTestClass().getRealClass().getSimpleName();
        String groups = Arrays.stream(testResult.getMethod().getGroups())
                .collect(Collectors.joining(", "));
        LogsManager.info("Test invocation started:", className + "." + testResult.getName(),
                "| Groups:", groups.isBlank() ? "(none)" : groups,
                "| Thread:", String.valueOf(Thread.currentThread().getId()));
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (!method.isTestMethod()) {
            return;
        }

        // Flush all soft assertions once, after the Test Method, rather than inside Page Components.
        try {
            Validation.assertAll();
        } catch (AssertionError softAssertionFailure) {
            testResult.setStatus(ITestResult.FAILURE);
            Throwable originalFailure = testResult.getThrowable();
            if (originalFailure == null) {
                testResult.setThrowable(softAssertionFailure);
            } else if (originalFailure != softAssertionFailure) {
                originalFailure.addSuppressed(softAssertionFailure);
            }
            LogsManager.error("Soft validations failed for test:", testResult.getName(),
                    "| Summary:", String.valueOf(softAssertionFailure.getMessage()));
        } finally {
            // Validation.assertAll removes its ThreadLocal context; this is a safety net for all paths.
            Validation.clear();
        }

        ScreenRecordManager.stopRecording(testResult.getName());

        WebDriver webDriver = null;
        if (testResult.getInstance() instanceof WebDriverProvider provider) {
            try {
                webDriver = provider.getWebDriver();
            } catch (Exception e) {
                // A reporting listener must not replace the original test failure with a secondary NPE.
                LogsManager.warn("Could not retrieve WebDriver for screenshot capture. Test:",
                        testResult.getName(), "| Cause:", e.getClass().getSimpleName(), "-", e.getMessage());
            }
        } else {
            LogsManager.warn("Screenshot capture skipped because the test instance does not implement WebDriverProvider:",
                    testResult.getTestClass().getName());
        }

        if (webDriver == null) {
            LogsManager.warn("No active WebDriver was available for the final screenshot of:", testResult.getName());
        } else {
            String screenshotPrefix = switch (testResult.getStatus()) {
                case ITestResult.SUCCESS -> "passed-";
                case ITestResult.FAILURE -> "failed-";
                case ITestResult.SKIP -> "skipped-";
                default -> null;
            };
            if (screenshotPrefix != null) {
                ScreenshotsManager.takeFullPageScreenshot(webDriver, screenshotPrefix + testResult.getName());
            }
        }

        AllureAttachmentManager.attachLogs();
        AllureAttachmentManager.attachRecords(testResult.getName());
        LogsManager.info("Test invocation completed:", testResult.getTestClass().getRealClass().getSimpleName()
                + "." + testResult.getName(), "| Final status:", statusName(testResult.getStatus()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogsManager.info("Test passed:", result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable failure = result.getThrowable();
        LogsManager.error("Test failed:", result.getTestClass().getRealClass().getSimpleName()
                        + "." + result.getName(), "| Cause:",
                failure == null ? "No exception details were provided by TestNG." : failure.toString());
        if (failure != null) {
            LogsManager.error("Failure stack trace for test " + result.getName(), failure);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Validation.clear();
        LogsManager.warn("Test skipped:", result.getTestClass().getRealClass().getSimpleName()
                        + "." + result.getName(), "| Reason:",
                result.getThrowable() == null ? "No skip reason was supplied." : result.getThrowable().toString());
    }

    private String statusName(int status) {
        return switch (status) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            default -> "UNKNOWN(" + status + ")";
        };
    }

    private void cleanTestOutputDirectories() {
        FileUtils.cleanDir(AllureConstants.RESULTS_FOLDER.toFile());
        FileUtils.cleanDir(ScreenshotsManager.SCREENSHOTS_DIRECTORY.toFile());
        FileUtils.cleanDir(ScreenRecordManager.RECORDINGS_DIRECTORY.toFile());
        FileUtils.cleanDir(AllureConstants.LOGS_FOLDER.toFile());
    }

    private void createTestOutputDirectories() {
        FileUtils.createDir(AllureConstants.OUTPUT_FOLDER.toString());
        FileUtils.createDir(AllureConstants.RESULTS_FOLDER.toString());
        FileUtils.createDir(ScreenshotsManager.SCREENSHOTS_DIRECTORY.toString());
        FileUtils.createDir(ScreenRecordManager.RECORDINGS_DIRECTORY.toString());
        FileUtils.createDir(AllureConstants.LOGS_FOLDER.toString());
        FileUtils.createDir(AllureConstants.REPORT_PATH.toString());
        FileUtils.createDir(AllureConstants.FULL_REPORT_PATH.toString());
        FileUtils.createDir(AllureConstants.HISTORY_FOLDER.toString());
        FileUtils.createDir(AllureConstants.EXECUTIONS_FOLDER.toString());
    }

    private void archiveExecutionArtifacts(boolean fullReportGenerated, boolean singleFileReportGenerated) {
        if (executionId == null || executionId.isBlank()) {
            executionId = LocalDateTime.now().format(EXECUTION_ID_FORMAT);
        }
        Path executionFolder = AllureConstants.EXECUTIONS_FOLDER.resolve(executionId);
        try {
            Files.createDirectories(executionFolder);
            copyDirectoryIfPresent(AllureConstants.RESULTS_FOLDER, executionFolder.resolve("allure-results"));
            copyDirectoryIfPresent(ScreenshotsManager.SCREENSHOTS_DIRECTORY, executionFolder.resolve("screenshots"));
            copyDirectoryIfPresent(ScreenRecordManager.RECORDINGS_DIRECTORY, executionFolder.resolve("videos"));
            copyDirectoryIfPresent(AllureConstants.LOGS_FOLDER, executionFolder.resolve("logs"));
            if (fullReportGenerated) {
                copyDirectoryIfPresent(AllureConstants.FULL_REPORT_PATH, executionFolder.resolve("allure-report"));
            }

            Path singleFileReport = AllureConstants.REPORT_PATH.resolve(AllureConstants.INDEX_HTML);
            if (singleFileReportGenerated && Files.isRegularFile(singleFileReport)) {
                Path singleFileFolder = executionFolder.resolve("single-file-report");
                Files.createDirectories(singleFileFolder);
                Files.copy(singleFileReport, singleFileFolder.resolve(AllureConstants.INDEX_HTML),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            LogsManager.info("Execution artifacts archived successfully:", executionFolder.toString());
        } catch (Exception e) {
            LogsManager.error("Could not archive execution artifacts:", e.getClass().getSimpleName(),
                    "-", String.valueOf(e.getMessage()));
            throw new IllegalStateException("Unable to archive execution artifacts.", e);
        }
    }

    private void copyDirectoryIfPresent(Path source, Path destination) throws Exception {
        if (Files.isDirectory(source)) {
            org.apache.commons.io.FileUtils.copyDirectory(source.toFile(), destination.toFile());
        }
    }
}
