package com.AutomationExercise.tests;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.drivers.WebDriverProvider;
import com.AutomationExercise.pages.components.NavigationComponent;
import com.AutomationExercise.pages.components.ProductsActions;
import com.AutomationExercise.utils.data.generator.TestDataGenerator;
import com.AutomationExercise.utils.data.model.AccountData;
import com.AutomationExercise.utils.data.reader.JsonReader;
import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.WebDriver;

/**
 * Shared TestNG base class. The GUIDriver facade is stored per thread because
 * TestNG may execute multiple methods from the same test-class instance in parallel.
 */
public class BaseTest implements WebDriverProvider {

    private final ThreadLocal<GUIDriver> driverContext = new ThreadLocal<>();
    private final ThreadLocal<JsonReader> testDataContext = new ThreadLocal<>();

    /** Stores the facade belonging to the current TestNG worker thread. */
    protected void setDriver(GUIDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("GUIDriver must not be null.");
        }
        driverContext.set(driver);
        LogsManager.debug("GUIDriver facade attached to BaseTest instance. Thread:",
                String.valueOf(Thread.currentThread().getId()));
    }

    /** Returns the current thread's GUIDriver facade. */
    protected GUIDriver getDriver() {
        GUIDriver currentDriver = driverContext.get();
        if (currentDriver == null) {
            throw new IllegalStateException("No GUIDriver facade is attached to this test thread. "
                    + "Check that @BeforeMethod(alwaysRun = true) calls setDriver(new GUIDriver()). Thread: "
                    + Thread.currentThread().getId());
        }
        return currentDriver;
    }

    @Override
    public WebDriver getWebDriver() {
        GUIDriver currentDriver = driverContext.get();
        return currentDriver == null ? null : currentDriver.getIfAvailable();
    }

    /** Optional shared data setup retained for test classes that need testData.json. */
    public void beforeMethod() {
        testDataContext.set(new JsonReader("testData.json"));
        LogsManager.debug("Loaded test data for the current thread. Test class:", getClass().getSimpleName(),
                "| Thread:", String.valueOf(Thread.currentThread().getId()));
    }

    protected JsonReader getTestData() {
        JsonReader currentTestData = testDataContext.get();
        if (currentTestData == null) {
            throw new IllegalStateException("Test data has not been initialized for this thread. "
                    + "Call beforeMethod() before requesting it.");
        }
        return currentTestData;
    }

    /** Shared teardown helper used by every Test Class @AfterMethod(alwaysRun = true). */
    protected void closeTestResources() {
        try {
            ProductsActions.removeAllSelectedProducts();
        } finally {
            GUIDriver.quitDriver();
            driverContext.remove();
            testDataContext.remove();
            LogsManager.debug("Test-thread driver context removed. Thread:",
                    String.valueOf(Thread.currentThread().getId()));
        }
    }

    protected static AccountData randomAccountData() {
        TestDataGenerator.NameParts name = TestDataGenerator.randomNameParts();
        TestDataGenerator.CompanyData company = TestDataGenerator.randomCompany();
        TestDataGenerator.Birthday birthday = TestDataGenerator.randomBirthday();
        return new AccountData(
                TestDataGenerator.randEmailAddress(),
                TestDataGenerator.randFullName(),
                TestDataGenerator.randGender(),
                TestDataGenerator.randPassword(),
                birthday.day(),
                birthday.month(),
                birthday.year(),
                name.first(),
                name.last(),
                company.name(),
                company.address(),
                TestDataGenerator.address(),
                "India",
                TestDataGenerator.state(),
                TestDataGenerator.city(),
                TestDataGenerator.zipcode(),
                TestDataGenerator.mobileNumber(),
                true,
                true
        );
    }

    protected AccountData createTestUser() {
        AccountData accountData = randomAccountData();
        LogsManager.info("Creating a temporary test account for the current test invocation.");
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .getNavigationComponent()
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
                .clickOnLogoutLink()
                .verifyUserLoggedOut();
        LogsManager.info("Temporary test account created and logged out successfully:", accountData.username());
        return accountData;
    }

    protected void login(String email, String password, String userName) {
        LogsManager.info("Logging in with the supplied test account:", userName);
        new NavigationComponent(getDriver())
                .clickOnSignupAndLogin()
                .login(email, password)
                .verifyUserIsLoggedIn(userName);
    }

    protected void login() {
        JsonReader jsonReader = new JsonReader("users");
        String email = jsonReader.getJsonValue("$.validUser.email");
        String password = jsonReader.getJsonValue("$.validUser.password");
        String userName = jsonReader.getJsonValue("$.validUser.userName");
        login(email, password, userName);
    }

    protected JsonReader getSearchTestData() {
        return new JsonReader("search-data");
    }

    protected void deleteAccount(String email, String password, String userName) {
        LogsManager.info("Deleting the temporary account after the test:", userName);
        new NavigationComponent(getDriver())
                .clickOnSignupAndLogin()
                .login(email, password)
                .verifyUserIsLoggedIn(userName)
                .clickOnDeleteAccountLink()
                .verifyAccountDeleted();
    }
}
