package com.AutomationExercise.utils.report;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Central location for all generated test artifacts.
 * All paths are absolute and rooted at the Maven project directory.
 */
public final class AllureConstants {
    private AllureConstants() {
    }

    public static final Path USER_DIR = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
    public static final Path USER_HOME = Paths.get(System.getProperty("user.home")).toAbsolutePath().normalize();

    public static final Path OUTPUT_FOLDER = USER_DIR.resolve("test-output");
    public static final Path RESULTS_FOLDER = OUTPUT_FOLDER.resolve("allure-results");
    public static final Path REPORT_PATH = OUTPUT_FOLDER.resolve("reports");
    public static final Path FULL_REPORT_PATH = OUTPUT_FOLDER.resolve("full-report");
    public static final Path HISTORY_FOLDER = OUTPUT_FOLDER.resolve("allure-history");
    public static final Path RESULTS_HISTORY_FOLDER = RESULTS_FOLDER.resolve("history");

    public static final Path LOGS_FOLDER = OUTPUT_FOLDER.resolve("Logs");
    public static final Path SCREENSHOTS_FOLDER = OUTPUT_FOLDER.resolve("screenshots");
    public static final Path RECORDINGS_FOLDER = OUTPUT_FOLDER.resolve("videos");
    public static final Path EXECUTIONS_FOLDER = OUTPUT_FOLDER.resolve("executions");

    public static final String INDEX_HTML = "index.html";
    public static final String REPORT_PREFIX = "AllureReport_";
    public static final String REPORT_EXTENSION = ".html";
    public static final String ALLURE_ZIP_BASE_URL = "https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/";
    public static final Path EXTRACTION_DIR = USER_HOME.resolve(".m2").resolve("repository").resolve("allure");
}
