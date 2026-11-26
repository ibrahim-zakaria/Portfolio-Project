package com.AutomationExercise.validations;

import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

/**
 * Thread-isolated soft validation facade.
 * All Validation instances used by one test thread contribute to the same
 * SoftAssert context; different test threads never share assertion state.
 */
public class Validation extends BaseAssertion {

    private static final ThreadLocal<ValidationContext> CONTEXT =
            ThreadLocal.withInitial(ValidationContext::new);

    private static final class ValidationContext {
        private final SoftAssert softAssert = new SoftAssert();
        private int assertionCount;
    }

    public Validation(WebDriver driver) {
        super(driver);
    }

    /** Starts a fresh soft-assertion context for one TestNG test invocation. */
    public static void beginTest() {
        CONTEXT.set(new ValidationContext());
        LogsManager.debug("Initialized a fresh soft-validation context for thread:",
                String.valueOf(Thread.currentThread().getId()));
    }

    @Override
    public void assertTrue(boolean condition, String message) {
        ValidationContext context = CONTEXT.get();
        context.softAssert.assertTrue(condition, message);
        context.assertionCount++;
        logAssertionResult(condition, "assertTrue", message);
    }

    @Override
    public void assertFalse(boolean condition, String message) {
        ValidationContext context = CONTEXT.get();
        context.softAssert.assertFalse(condition, message);
        context.assertionCount++;
        logAssertionResult(!condition, "assertFalse", message);
    }

    @Override
    public void assertEquals(String actual, String expected, String message) {
        ValidationContext context = CONTEXT.get();
        context.softAssert.assertEquals(actual, expected, message);
        context.assertionCount++;
        logAssertionResult(java.util.Objects.equals(actual, expected),
                "assertEquals", message + " | Expected: " + expected + " | Actual: " + actual);
    }

    public void assertEquals(int actual, int expected, String message) {
        ValidationContext context = CONTEXT.get();
        context.softAssert.assertEquals(actual, expected, message);
        context.assertionCount++;
        logAssertionResult(actual == expected, "assertEquals",
                message + " | Expected: " + expected + " | Actual: " + actual);
    }

    /** Removes the current thread's validation context without affecting other test threads. */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * Flushes all soft assertions accumulated by the current test thread.
     * This is called once by TestNGListeners after each test method.
     */
    public static void assertAll() {
        ValidationContext context = CONTEXT.get();
        try {
            if (context.assertionCount == 0) {
                LogsManager.debug("No soft assertions were recorded for this test invocation.");
                return;
            }

            LogsManager.info("Finalizing soft validations. Assertion count:",
                    String.valueOf(context.assertionCount));
            context.softAssert.assertAll();
            LogsManager.info("All soft validations passed.");
        } catch (AssertionError e) {
            LogsManager.error("Soft validation summary reported failures:", e.getMessage());
            throw e;
        } finally {
            CONTEXT.remove();
        }
    }

    private void logAssertionResult(boolean passed, String assertionType, String message) {
        if (passed) {
            LogsManager.debug("Soft validation passed:", assertionType, "|", message);
        } else {
            LogsManager.error("Soft validation failed:", assertionType, "|", message);
        }
    }
}
