package com.AutomationExercise.validations;

import com.AutomationExercise.utils.actions.ElementActions;
import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.utils.wait.WaitManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public abstract class BaseAssertion {
    protected final WebDriver driver;
    protected final WaitManager waitManager;
    protected final ElementActions elementActions;

    public BaseAssertion(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver,
                "Cannot create a Validation/Verification object without an initialized WebDriver.");
        this.waitManager = new WaitManager(driver);
        this.elementActions = new ElementActions(driver);
    }

    public abstract void assertTrue(boolean condition, String message);

    protected abstract void assertFalse(boolean condition, String message);

    protected abstract void assertEquals(String actual, String expected, String message);

    public void equals(String actual, String expected, String message) {
        assertEquals(actual, expected, message);
    }

    /**
     * Performs a soft visibility assertion. A timeout is converted into a
     * failed assertion rather than escaping as an immediate TimeoutException.
     */
    public void isElementIsVisible(By locator) {
        boolean visible;
        try {
            visible = waitManager.fluentWait().until(d -> {
                try {
                    return d.findElement(locator).isDisplayed();
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            visible = false;
        }

        if (visible) {
            LogsManager.debug("Visibility validation passed for element:", locator.toString());
        } else {
            LogsManager.warn("Visibility validation failed; element was not visible before timeout:",
                    locator.toString());
        }
        assertTrue(visible, "Expected element to be visible: " + locator);
    }

    public void assertPageUrl(String expectedUrl) {
        String actualUrl = driver.getCurrentUrl();
        LogsManager.info("Validating current page URL. Expected:", expectedUrl, "| Actual:", actualUrl);
        assertEquals(actualUrl, expectedUrl,
                "Page URL mismatch. Expected: " + expectedUrl + " | Actual: " + actualUrl);
    }
}
