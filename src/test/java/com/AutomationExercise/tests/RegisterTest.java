package com.AutomationExercise.tests;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.pages.components.NavigationComponent;
import com.AutomationExercise.utils.data.model.AccountData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegisterTest extends BaseTest {

    //Test Scripts
    @Test(description = "Test Case 1: Register User")
    public void signupTest() {
        AccountData accountData = randomAccountData();
        new NavigationComponent(getDriver())
                .clickOnSignupAndLogin()
                .inputSignupName(accountData.username())
                .inputSignupEmail(accountData.email())
                .clickSignupButton()
                .verifyFormPageDisplayed()
                .fillAccountInformation(accountData)
                .clickCreateAccountButton()
                .verifyAccountCreation()
                .clickContinueButton()
                .verifyUserIsLoggedIn(accountData.username())
                .clickOnDeleteAccountLink()
                .verifyAccountDeleted();
        LogsManager.debug("Temporary registration flow completed; generated credentials were not written to the console.");
    }

    @Test(description = "Test Case 2: Login User with correct email and password")
    public void validLoginTest() {
        AccountData accountData = createTestUser();
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .getNavigationComponent()
                .clickOnSignupAndLogin()
                .login(accountData.email(), accountData.password())
                .verifyUserIsLoggedIn(accountData.username())
                .clickOnDeleteAccountLink()
                .verifyAccountDeleted();
    }

    @Test(description = "Test Case 3: Login User with incorrect email and password")
    public void loginWithInvalidCredentialsTest() {
        AccountData accountData = randomAccountData();
        new NavigationComponent(getDriver())
                .clickOnSignupAndLogin()
                .inputEmail(accountData.email())
                .inputPassword(accountData.password())
                .clickLoginButton()
                .verifyLoginErrorMessage("Invalid email or password");
    }

    @Test(description = "Test Case 4: Logout User" )
    public void logoutTest() {
        AccountData  accountData = createTestUser();
        String email = accountData.email();
        String password = accountData.password();
        String username = accountData.username();
        new NavigationComponent(getDriver())
                .clickOnSignupAndLogin()
                .login(email, password)
                .verifyUserIsLoggedIn(username)
                .clickOnLogoutLink()
                .verifyUserIsNavigatedToLoginPage()
                .clickOnSignupAndLogin()
                .login(email,password)
                .getNavigationComponent()
                .clickOnDeleteAccountLink()
                .verifyAccountDeleted();
    }

    @Test(description = "Test Case 5: Register User with existing email", groups={"test-Group"})
    public void registerTest() {
        AccountData accountData = createTestUser();
        new NavigationComponent(getDriver())
                .clickOnSignupAndLogin()
                .signup(accountData.username(), accountData.email())
                .verifySignupErrorMessage("Invalid email or password");

        deleteAccount(accountData.email(), accountData.password(), accountData.username());
    }


    //Configurations
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        setDriver(new GUIDriver());
        new NavigationComponent(getDriver()).navigateToHomePage();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        closeTestResources();
    }

}
