package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ContactUsPage {
    public ContactUsPage(GUIDriver driver) {
        this.driver = driver;
    }
    GUIDriver driver;
    private final By nameLocator = By.cssSelector("input[data-qa='name']");
    private final By emailLocator = By.cssSelector("input[data-qa='email']");
    private final By subjectLocator = By.cssSelector("input[data-qa='subject']");
    private final By messageLocator = By.cssSelector("textarea[data-qa='message']");
    private final By uploadFileLocator = By.cssSelector("input[type='file']");
    private final By submitLocator = By.cssSelector("input[type='submit']");
    private final By GetInTouchLocator = By.xpath("//h2[normalize-space()='Get In Touch']");
    private final By successMsgLocator = By.xpath("//div[@id='contact-page'] //div[normalize-space()='Success! Your details have been submitted successfully.']");
    private final By homeBtnLocator = By.cssSelector("a.btn[href='/']");

    @Step("Enter  name")
    public ContactUsPage enterName(String name) {
        LogsManager.info("Page step started: Enter  name");
        driver.elementActions().enterText(nameLocator, name);
        return this;
    }

    @Step("Enter Email Address")
    public ContactUsPage enterEmail(String email) {
        LogsManager.info("Page step started: Enter Email Address");
        driver.elementActions().enterText(emailLocator, email);
        return this;
    }

    @Step("Enter Subject")
    public ContactUsPage enterEmailSubject(String subject) {
        LogsManager.info("Page step started: Enter Subject");
        driver.elementActions().enterText(subjectLocator, subject);
        return this;
    }

    @Step("Enter Message")
    public ContactUsPage enterMessageText(String message) {
        LogsManager.info("Page step started: Enter Message");
        driver.elementActions().enterText(messageLocator, message);
        return this;
    }

    @Step("Upload File ")
    public ContactUsPage uploadFile(String fileName) {
        LogsManager.info("Page step started: Upload File ");
        driver.elementActions().uploadFile(uploadFileLocator, fileName);
        return this;
    }

    @Step("Click on Submit")
    public ContactUsPage submit() {
        LogsManager.info("Page step started: Click on Submit");
        driver.elementActions().click(submitLocator);
        return this;
    }

    @Step("Click on Home button")
    public HomePage clickOnHomeButton() {
        LogsManager.info("Page step started: Click on Home button");
        driver.elementActions().click(homeBtnLocator);
        return new HomePage(driver);
    }

    @Step("Verify 'GET IN TOUCH' is visible")
    public ContactUsPage verifyGetInTouchIsVisible() {
        LogsManager.info("Page step started: Verify 'GET IN TOUCH' is visible");
        driver.verification().isElementIsVisible(GetInTouchLocator);
        return this;
    }
    @Step("Accept alert Message")
    public ContactUsPage acceptAlertMessage() {
        LogsManager.info("Page step started: Accept alert Message");
        driver.elementActions().acceptAlert();
        return this;
    }

    @Step("Verify successful message")
    public ContactUsPage verifySuccessfulMessage() {
        LogsManager.info("Page step started: Verify successful message");
        String actualTxt = driver.elementActions().getText(successMsgLocator);
        String expectedTxt = "Success! Your details have been submitted successfully.";
        driver.verification().equals(
                expectedTxt
                ,actualTxt
                ,"Actual message doesn't match expected message"
        );
        return this;
    }
}
