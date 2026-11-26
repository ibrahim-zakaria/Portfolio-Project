package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class TestCases {
    GUIDriver driver;
    public TestCases(GUIDriver driver) {
        this.driver = driver;
    }
    private final By pageTitleLocator = By.tagName("b");

    @Step("Verify Page Title is visible")
    public TestCases verifyPageTitle(){
        LogsManager.info("Page step started: Verify Page Title is visible");
        driver.verification().isElementIsVisible(pageTitleLocator);
        return this;
    }

}
