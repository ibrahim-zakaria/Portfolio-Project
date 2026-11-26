package com.AutomationExercise.drivers;

import com.AutomationExercise.utils.actions.AlertActions;
import com.AutomationExercise.utils.actions.BrowserActions;
import com.AutomationExercise.utils.actions.ElementActions;
import com.AutomationExercise.utils.actions.FrameActions;
import com.AutomationExercise.utils.data.reader.PropertyReader;
import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.validations.Validation;
import com.AutomationExercise.validations.Verification;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ThreadGuard;

/**
 * Thread-aware facade around the WebDriver session owned by the current TestNG thread.
 * A new GUIDriver should be created from each test's @BeforeMethod.
 */
public class GUIDriver {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    public GUIDriver() {
        closeDanglingDriverForCurrentThread();

        String browserValue = firstNonBlank(
                System.getProperty("browser"),
                PropertyReader.getProperty("browserType"),
                "EDGE"
        );
        String executionType = firstNonBlank(
                System.getProperty("executionType"),
                PropertyReader.getProperty("executionType"),
                "Local"
        );

        final Browser browser;
        try {
            browser = Browser.valueOf(browserValue.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported browser '" + browserValue
                            + "'. Supported browsers are Chrome, Firefox, and Edge.", e);
        }

        LogsManager.info("Starting browser session. Browser:", browser.name(),
                "| Execution type:", executionType,
                "| Thread:", String.valueOf(Thread.currentThread().getId()));

        WebDriver createdDriver = null;
        try {
            AbstractDriver browserFactory = browser.getBrowserFactory();
            createdDriver = browserFactory.createDriver();
            if (createdDriver == null) {
                throw new IllegalStateException("The browser factory returned a null WebDriver instance.");
            }

            WebDriver guardedDriver = ThreadGuard.protect(createdDriver);
            DRIVER_THREAD_LOCAL.set(guardedDriver);
            LogsManager.info("Browser session initialized successfully. Browser:", browser.name(),
                    "| Session:", guardedDriver.toString());
        } catch (Exception e) {
            if (createdDriver != null) {
                try {
                    createdDriver.quit();
                } catch (Exception cleanupException) {
                    LogsManager.warn("Could not close a partially initialized browser session:",
                            cleanupException.getMessage());
                }
            }
            DRIVER_THREAD_LOCAL.remove();
            LogsManager.error("Browser session initialization failed. Browser:", browser.name(),
                    "| Cause:", e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
            throw new IllegalStateException("Unable to initialize " + browser.name() + " WebDriver.", e);
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static void closeDanglingDriverForCurrentThread() {
        WebDriver existingDriver = DRIVER_THREAD_LOCAL.get();
        if (existingDriver == null) {
            return;
        }

        LogsManager.warn("A WebDriver session was still attached to the current thread before setup."
                + " Closing it to prevent a leaked browser session. Thread:",
                String.valueOf(Thread.currentThread().getId()));
        try {
            existingDriver.quit();
        } catch (Exception e) {
            LogsManager.warn("Previous WebDriver session could not be closed cleanly:", e.getMessage());
        } finally {
            DRIVER_THREAD_LOCAL.remove();
        }
    }

    /** Returns the active WebDriver, or throws an actionable lifecycle error. */
    public WebDriver get() {
        WebDriver currentDriver = DRIVER_THREAD_LOCAL.get();
        if (currentDriver == null) {
            throw new IllegalStateException(
                    "No WebDriver session is initialized for thread " + Thread.currentThread().getId()
                            + ". Check that @BeforeMethod(alwaysRun = true) creates GUIDriver before the test.");
        }
        return currentDriver;
    }

    /** Returns the current thread's driver without throwing; intended for TestNG listeners. */
    public WebDriver getIfAvailable() {
        return DRIVER_THREAD_LOCAL.get();
    }

    /** Closes only the WebDriver session owned by the current TestNG thread. */
    public static void quitDriver() {
        WebDriver currentDriver = DRIVER_THREAD_LOCAL.get();
        if (currentDriver == null) {
            LogsManager.debug("No WebDriver session needs closing for thread:",
                    String.valueOf(Thread.currentThread().getId()));
            DRIVER_THREAD_LOCAL.remove();
            return;
        }

        try {
            LogsManager.info("Closing WebDriver session. Thread:",
                    String.valueOf(Thread.currentThread().getId()), "| Session:", currentDriver.toString());
            currentDriver.quit();
            LogsManager.info("WebDriver session closed successfully.");
        } catch (Exception e) {
            // Do not mask the test's original failure with a secondary browser-shutdown failure.
            LogsManager.error("WebDriver session shutdown failed:", e.getClass().getSimpleName(),
                    "-", String.valueOf(e.getMessage()));
        } finally {
            DRIVER_THREAD_LOCAL.remove();
        }
    }

    public ElementActions elementActions() {
        return new ElementActions(get());
    }

    public BrowserActions browserActions() {
        return new BrowserActions(get());
    }

    public AlertActions alertActions() {
        return new AlertActions(get());
    }

    public FrameActions frameActions() {
        return new FrameActions(get());
    }

    public Validation validation() {
        return new Validation(get());
    }

    public Verification verification() {
        return new Verification(get());
    }
}
