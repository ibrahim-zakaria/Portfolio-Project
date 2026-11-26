package com.AutomationExercise.utils.actions;

import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

public class BrowserActions {
    private final WebDriver driver;

    public BrowserActions(WebDriver driver) {
        this.driver = driver;
    }

    public void maximizeWindow() {
        driver.manage().window().maximize();
        LogsManager.info("Browser window maximized.");
    }

    public void close() {
        LogsManager.info("Closing the current browser window.");
        driver.close();
    }

    public void navigateTo(String url) {
        LogsManager.info("Navigating browser to URL:", url);
        driver.get(url);
        LogsManager.info("Navigation completed. Current URL:", driver.getCurrentUrl());
    }

    public String getCurrentUrl() {
        String currentUrl = driver.getCurrentUrl();
        LogsManager.debug("Retrieved current browser URL:", currentUrl);
        return currentUrl;
    }

    public void refreshPage() {
        LogsManager.info("Refreshing the current browser page:", driver.getCurrentUrl());
        driver.navigate().refresh();
        LogsManager.info("Browser page refresh command completed.");
    }

    public void openNewWindow() {
        driver.switchTo().newWindow(WindowType.WINDOW);
        LogsManager.info("Opened a new browser window. Window handle:", driver.getWindowHandle());
    }
}
