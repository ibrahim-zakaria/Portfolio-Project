package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class AccountCreatedPage {
    GUIDriver driver;
    private final By accountCreatedLabel = By.cssSelector("h2[data-qa='account-created'] b");
    private final By continueBtn = By.cssSelector("a[data-qa='continue-button']");

    public AccountCreatedPage(GUIDriver driver) {
        this.driver = driver;
    }


    @Step("Verify new user creation status")
    public AccountCreatedPage verifyAccountCreation() {
        LogsManager.info("Page step started: Verify new user creation status");
        String actualCreationStatus = driver.elementActions().getText(accountCreatedLabel);
        driver.validation().isElementIsVisible(accountCreatedLabel);
        driver.validation().assertEquals(actualCreationStatus, "ACCOUNT CREATED!", "Account creation failed");
        return this;
    }

    @Step("Click on continue button")
    public HomePage clickContinueButton() {
        LogsManager.info("Page step started: Click on continue button");
        driver.elementActions().click(continueBtn);
        return new HomePage(driver);
    }

}
