package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.utils.data.model.AccountData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SignupPage {

    GUIDriver driver;
    public SignupPage(GUIDriver driver) {
        this.driver = driver;
    }
    //Form Elements
    private final By accountInfoLabel = By.xpath("//b[normalize-space()='Enter Account Information']");
    private final By MrRadioBtn = By.id("id_gender1");
    private final By MrsRadioBtn = By.id("id_gender2");
    private final By password = By.id("password");
    private final By dayOfBirthday = By.id("days");
    private final By monthOfBirthday = By.id("months");
    private final By yearOfBirthday = By.id("years");
    private final By newsLetterCheckBox = By.id("newsletter");
    private final By receiveOffersCheckBox = By.id("optin");
    private final By firstName = By.id("first_name");
    private final By lastName = By.id("last_name");
    private final By company = By.id("company");
    private final By companyAddress = By.id("address1");
    private final By address2 = By.id("address2");
    private final By country = By.id("country");
    private final By state = By.id("state");
    private final By city = By.id("city");
    private final By zipcode = By.id("zipcode");
    private final By mobile_number = By.id("mobile_number");
    private final By createAccountBtn = By.cssSelector("[data-qa='create-account']");
    private final By formLabel = By.cssSelector("#form h2");

    @Step("Fill the account information")
    public SignupPage fillAccountInformation(AccountData data) {
        LogsManager.info("Page step started: Fill the account information");
        // Gender
        if (data.gender().equalsIgnoreCase("male")) {
            driver.elementActions().click(MrRadioBtn);
        } else if (data.gender().equalsIgnoreCase("female")) {
            driver.elementActions().click(MrsRadioBtn);
        } else {
            throw new IllegalArgumentException("Invalid gender: " + data.gender());
        }

        // Password
        driver.elementActions().enterMaskedText(password, data.password());
        // Date of birth
        driver.elementActions().selectByVisibleText(dayOfBirthday, data.day());
        driver.elementActions().selectByVisibleText(monthOfBirthday, data.month());
        driver.elementActions().selectByVisibleText(yearOfBirthday, data.year());

        // Newsletter
        driver.elementActions().setCheckbox(newsLetterCheckBox, data.newsletter());
        // Special offers
        driver.elementActions().setCheckbox(receiveOffersCheckBox, data.receiveOffers());

        // Personal information
        driver.elementActions().enterText(firstName, data.firstName());
        driver.elementActions().enterText(lastName, data.lastName());
        driver.elementActions().enterText(company, data.company());
        // Address
        driver.elementActions().enterText(companyAddress, data.address1());
        driver.elementActions().enterText(address2, data.address2());
        // Location
        driver.elementActions().selectByVisibleText(country, data.country());

        driver.elementActions().enterText(state, data.state());
        driver.elementActions().enterText(city, data.city());
        driver.elementActions().enterText(zipcode, data.zipcode());
        driver.elementActions().enterText(mobile_number, data.mobileNumber());

        return this;
    }

    @Step("Verify the new account information form is displayed")
    public SignupPage verifyFormPageDisplayed() {
        LogsManager.info("Page step started: Verify the new account information form is displayed.");
        driver.elementActions().waitUntilVisible(accountInfoLabel);
        driver.verification().isElementIsVisible(accountInfoLabel);
        return this;
    }

    @Step("Click on Create account button")
    public AccountCreatedPage clickCreateAccountButton() {
        LogsManager.info("Page step started: Click on Create account button");
        driver.elementActions().click(createAccountBtn);
        return new AccountCreatedPage(driver);
    }

    public SignupPage verifyLoginPageDisplayed() {
        LogsManager.info("Verifying that the account information form is displayed.");
        driver.verification().isElementIsVisible(formLabel);
        return this;
    }

}
