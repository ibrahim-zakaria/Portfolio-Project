package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class AccountDeletedPage {
    GUIDriver driver;
    public AccountDeletedPage(GUIDriver driver) {
        this.driver = driver;
    }
    private final By accountDeletedLabel = By.cssSelector("h2[data-qa='account-deleted'] b");
    private final By continueBtnLoc = By.cssSelector("a[data-qa='continue-button']");

    @Step("Verify Account is deleted")
    public AccountDeletedPage verifyAccountDeleted(){
        LogsManager.info("Page step started: Verify Account is deleted");
        String actualDeleteMessage = getDeletionStatus();
        String expectedDeleteMessage = "ACCOUNT DELETED!";
        driver.validation().isElementIsVisible(accountDeletedLabel);
        driver.validation().assertEquals(actualDeleteMessage,expectedDeleteMessage,"Account Deleted!");
        return this;
    }

    @Step("Click on continue button")
    public NavigationComponent clickOnContinueBtn(){
        LogsManager.info("Page step started: Click on continue button");
        driver.elementActions().click(continueBtnLoc);
        return new NavigationComponent(driver);
    }

    private String getDeletionStatus(){
        return driver.elementActions().getText(accountDeletedLabel);
    }
}
