package com.AutomationExercise.utils.logs;

import com.AutomationExercise.utils.report.AllureConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.nio.file.Path;

public class LogsManager {
    public static final Path LOGS_DIRECTORY = AllureConstants.LOGS_FOLDER;
    public static final Path LOGS_FILE = LOGS_DIRECTORY.resolve("logs.log");
    // Kept as String for compatibility with existing project references.
    public static final String LOGS_PATH = LOGS_DIRECTORY + File.separator;

    /** Closes the previous file appender before the output folder is cleaned. */
    public static void resetForNewExecution() {
        LogManager.shutdown();
    }

    /** Reopens the configured appenders after the output directories have been prepared. */
    public static void reconfigureAfterCleanup() {
        ((LoggerContext) LogManager.getContext(false)).reconfigure();
    }

    private static Logger logger() {
        return LogManager.getLogger(Thread.currentThread().getStackTrace()[3].getClassName());
    }

    public static void info(String... message) {
        logger().info(String.join(" ", message));
    }

    public static void warn(String... message) {
        logger().warn(String.join(" ", message));
    }

    public static void error(String... message) {
        logger().error(String.join(" ", message));
    }

    /** Logs a descriptive message together with the complete exception stack trace. */
    public static void error(String message, Throwable cause) {
        logger().error(message, cause);
    }

    public static void fatal(String... message) {
        logger().fatal(String.join(" ", message));
    }

    public static void debug(String... message) {
        logger().debug(String.join(" ", message));
    }
}
