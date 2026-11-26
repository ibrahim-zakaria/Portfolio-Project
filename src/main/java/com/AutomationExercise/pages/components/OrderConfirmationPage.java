package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class OrderConfirmationPage {
    GUIDriver driver;

    public OrderConfirmationPage(GUIDriver driver) {
        this.driver = driver;
    }
    private final By orderPlacedLabelLoc = By.cssSelector("h2[data-qa='order-placed'] b");
    private final By ContinueBtnLoc = By.cssSelector("a[data-qa='continue-button']");
    private final By downloadInvoiceBtnLoc = By.cssSelector("a[href^='/download_invoice']");
    private final By confirmationMsgLoc = By.cssSelector("//p[normalize-space()='Congratulations! Your order has been confirmed!']");

    @Step("Verify order confirmation")
    public OrderConfirmationPage verifyOrderConfirmation() {
        LogsManager.info("Page step started: Verify order confirmation");
        driver.validation().isElementIsVisible(orderPlacedLabelLoc);
        driver.validation().assertEquals(
                driver.elementActions().getText(orderPlacedLabelLoc),
                "ORDER PLACED!",
                "Order confirmation message is incorrect"
        );
        return this;
    }
    @Step("Click on download invoice ")
    public OrderConfirmationPage clickOnDownloadInvoice() {
        LogsManager.info("Page step started: Click on download invoice ");
        driver.elementActions().click(downloadInvoiceBtnLoc);
        return this;
    }
    @Step("Continue to Home Page")
    public NavigationComponent clickOnContinueBtn() {
        LogsManager.info("Page step started: Continue to Home Page");
        driver.elementActions().click(ContinueBtnLoc);
        return new NavigationComponent(driver);
    }
    @Step("Verify the order confirmation message is displayed")
    public OrderConfirmationPage verifyConfirmationMessage() {
        LogsManager.info("Page step started: Verify the order confirmation message is displayed.");
        driver.validation().isElementIsVisible(confirmationMsgLoc);
        return this;
    }
}
