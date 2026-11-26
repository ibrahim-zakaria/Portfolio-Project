package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.utils.data.reader.PropertyReader;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class NavigationComponent {
    private final GUIDriver driver;

    public NavigationComponent(GUIDriver driver) {
        this.driver = driver;
    }

    private final By homePageLoc = By.xpath("//a[normalize-space()='Home']");
    private final By productsPageLoc = By.cssSelector("a[href='/products']");
    private final By cartPageLoc = By.xpath("//a[normalize-space()='Cart']");
    private final By signupLoginPageLoc = By.cssSelector("a[href='/login']");
    private final By logoutLinkLoc = By.cssSelector("a[href='/logout']");
    private final By deleteAccountLinkLoc = By.cssSelector("a[href='/delete_account']");
    private final By videoTutorialsButton = By.xpath("//a[contains(text(),'Video Tutorials')]");
    private final By contactUsPageLoc = By.cssSelector("a[href='/contact_us']");
    private final By homeSlider = By.id("slider");
    private final By testCasesPageLoc = By.cssSelector("a[href='/test_cases']");
    //private final By APIButtonLoc = By.cssSelector("a[href='/api_list']");
    //private final By homePageLabel = By.cssSelector("h1 > span");


    //Actions Section
    @Step("Navigate to Home Page")
    public HomePage navigateToHomePage() {
        LogsManager.info("Page step started: Navigate to Home Page");
        driver.browserActions().navigateTo(PropertyReader.getProperty("baseUrl").toUpperCase());
        return new HomePage(driver);
    }

    @Step("Click on the Home link")
    public HomePage clickOnHomeLink() {
        LogsManager.info("Page step started: Click on the Home link");
        driver.elementActions().click(homePageLoc);
        return new HomePage(driver);
    }

    @Step("Click on the Products link")
    public AllProductsPage clickOnProductsLink() {
        LogsManager.info("Page step started: Click on the Products link");
        driver.elementActions().click(productsPageLoc);
        return new AllProductsPage(driver);
    }

    @Step("Click on the Cart link")
    public CartPage clickOnCartLink() {
        LogsManager.info("Page step started: Click on the Cart link");
        driver.elementActions().click(cartPageLoc);
        return new CartPage(driver);
    }

    @Step("Click on the Signup / Login link")
    public SignupAndLoginPage clickOnSignupAndLogin() {
        LogsManager.info("Page step started: Click on the Signup / Login link");
        driver.elementActions().click(signupLoginPageLoc);
        return new SignupAndLoginPage(driver);
    }

    @Step("Click on the Logout link")
    public SignupAndLoginPage clickOnLogoutLink() {
        LogsManager.info("Page step started: Click on the Logout link");
        driver.elementActions().click(logoutLinkLoc);
        return new SignupAndLoginPage(driver);
    }

    @Step("Click on the Contact Us Link")
    public ContactUsPage clickOnContactUsLink() {
        LogsManager.info("Page step started: Click on the Contact Us Link");
        driver.elementActions().click(contactUsPageLoc);
        return new ContactUsPage(driver);
    }

    @Step("Click on the Delete Account link")
    public AccountDeletedPage clickOnDeleteAccountLink() {
        LogsManager.info("Page step started: Click on the Delete Account link");
        driver.elementActions().click(deleteAccountLinkLoc);
        return new AccountDeletedPage(driver);
    }

    @Step("Click on test cases link")
    public TestCases clickOnTestCasesLink(){
        LogsManager.info("Page step started: Click on test cases link");
        driver.elementActions().click(testCasesPageLoc);
        return new TestCases(driver);
    }

}
