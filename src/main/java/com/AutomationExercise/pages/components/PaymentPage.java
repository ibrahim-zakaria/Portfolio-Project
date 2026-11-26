package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.utils.data.model.PaymentCardData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import java.util.Objects;

public class PaymentPage {
    GUIDriver driver;

    public PaymentPage(GUIDriver driver) {
        this.driver = driver;
    }


    private final By NameOnCardLoc = By.cssSelector("input[data-qa='name-on-card']");
    private final By cardNumberLoc = By.cssSelector("input[data-qa='card-number']");
    private final By cardCvcLoc = By.cssSelector("input[data-qa='cvc']");
    private final By cardExpiryMonthLoc = By.cssSelector("input[data-qa='expiry-month']");
    private final By cardExpiryYearLoc = By.cssSelector("input[data-qa='expiry-year']");
    private final By payAndConfirmOrderBtnLoc = By.cssSelector("button[data-qa='pay-button']");
    private final By paymentConfirmMsgLoc = By.cssSelector("#success_message .alert-success");

    @Step("Fill Payment Details")
    public PaymentPage fillPaymentDetails(PaymentCardData cardData) {
        LogsManager.info("Page step started: Fill Payment Details");
        driver.elementActions().enterText(NameOnCardLoc, cardData.nameOnCard());
        driver.elementActions().enterText(cardNumberLoc, cardData.cardNumber());
        driver.elementActions().enterText(cardCvcLoc, cardData.cvc());
        driver.elementActions().enterText(cardExpiryMonthLoc, cardData.expirationMonth());
        driver.elementActions().enterText(cardExpiryYearLoc, cardData.expirationYear());
        return this;
    }

    @Step("Verify payment success")
    public OrderConfirmationPage verifyPaymentSuccess() {
        LogsManager.info("Page step started: Verify payment success");

        driver.verification().assertTrue(
                isPaymentSuccessful()
                , "Unsuccessful payment process"
        );
        return new OrderConfirmationPage(driver);
    }

    String actualText;

   /* @Step("Click on pay and confirm Order and Submit Payment")
    public PaymentPage clickOnPayAndConfirmOrder() {
        driver.elementActions().click(payAndConfirmOrderBtnLoc);
        //driver.elementActions().waitUntilVisible(paymentConfirmMsgLoc);
        actualText = driver.elementActions().captureTransientMessage(paymentConfirmMsgLoc);
       /* actualText = driver.elementActions()
                .waitUntilVisible(paymentConfirmMsgLoc)
                .getText();
        LogsManager.info("Payment confirmation message captured:", actualText);
        return this;
    }*/

    @Step("Click on Pay and Confirm Order")
    public PaymentPage clickOnPayAndConfirmOrder() {
        LogsManager.info("Page step started: Click on Pay and Confirm Order");
        driver.elementActions()
                .startTransientMessageObserver(paymentConfirmMsgLoc);
        driver.elementActions().click(payAndConfirmOrderBtnLoc);
        actualText = driver.elementActions()
                .getCapturedPaymentMessage();

        return this;
    }
    @Step("Verify payment success message")
    public boolean isPaymentSuccessful() {
        LogsManager.info("Page step started: Verify payment success message");
        /*try {
            actualText = driver.elementActions()
                    .waitUntilVisible(paymentConfirmMsgLoc)
                    .getText();*/

        String expectedMessage = "Your order has been placed successfully!";
        boolean successful = Objects.equals(actualText, expectedMessage);
        if (successful) {
            LogsManager.info("Payment confirmation message matched the expected success text.");
        } else {
            LogsManager.error("Payment confirmation message did not match. Expected:", expectedMessage,
                    "| Actual:", String.valueOf(actualText));
        }
        return successful;
        /*} catch (TimeoutException e) {
            return false;
        }*/
    }
}
