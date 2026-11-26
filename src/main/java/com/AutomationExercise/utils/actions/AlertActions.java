package com.AutomationExercise.utils.actions;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.utils.wait.WaitManager;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

public class AlertActions {
    private final WebDriver driver;
    private final WaitManager waitManager;

    public AlertActions(WebDriver driver) {
        this.driver = driver;
        this.waitManager = new WaitManager(driver);
    }

    public void acceptAlert() {
        try {
            waitManager.fluentWait().until(d -> {
                try {
                    d.switchTo().alert().accept();
                    return true;
                } catch (NoAlertPresentException e) {
                    return false;
                }
            });
            LogsManager.info("Browser alert accepted successfully.");
        } catch (RuntimeException e) {
            LogsManager.error("Unable to accept browser alert:", e.getClass().getSimpleName(), "-", e.getMessage());
            throw e;
        }
    }

    public void dismissAlert() {
        try {
            waitManager.fluentWait().until(d -> {
                try {
                    d.switchTo().alert().dismiss();
                    return true;
                } catch (NoAlertPresentException e) {
                    return false;
                }
            });
            LogsManager.info("Browser alert dismissed successfully.");
        } catch (RuntimeException e) {
            LogsManager.error("Unable to dismiss browser alert:", e.getClass().getSimpleName(), "-", e.getMessage());
            throw e;
        }
    }

    public String getAlertText() {
        try {
            String text = waitManager.fluentWait().until(d -> {
                try {
                    return d.switchTo().alert().getText();
                } catch (NoAlertPresentException e) {
                    return null;
                }
            });
            LogsManager.info("Retrieved browser alert text:", text);
            return text;
        } catch (RuntimeException e) {
            LogsManager.error("Unable to retrieve browser alert text:",
                    e.getClass().getSimpleName(), "-", e.getMessage());
            throw e;
        }
    }

    public void setAlertText(String text) {
        try {
            waitManager.fluentWait().until(d -> {
                try {
                    d.switchTo().alert().sendKeys(text);
                    return true;
                } catch (NoAlertPresentException e) {
                    return false;
                }
            });
            LogsManager.info("Entered text into browser alert. Value:", "[REDACTED; length=" +
                    (text == null ? 0 : text.length()) + "]");
        } catch (RuntimeException e) {
            LogsManager.error("Unable to enter text into browser alert:",
                    e.getClass().getSimpleName(), "-", e.getMessage());
            throw e;
        }
    }
}
