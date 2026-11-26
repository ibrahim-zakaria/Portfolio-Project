package com.AutomationExercise.tests;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.pages.components.NavigationComponent;
import com.AutomationExercise.pages.components.ProductsActions;
import com.AutomationExercise.utils.data.generator.TestDataGenerator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ContactUsTests extends BaseTest{

    @Test(description = "Test Case 6: Contact Us Form")
    public void contactUsFormTest(){
        String FILE_PATH = "src/test/resources/test-file/test-Upload-File.txt";
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnContactUsLink()
                .verifyGetInTouchIsVisible()
                .enterName(TestDataGenerator.randFullName())
                .enterEmail(TestDataGenerator.randEmailAddress())
                .enterEmailSubject(TestDataGenerator.emailSubject())
                .enterMessageText(TestDataGenerator.randText())
                .uploadFile(FILE_PATH)
                .submit()
                .acceptAlertMessage()
                .clickOnHomeButton()
                .verifyNavigationToHomePage();

    }









    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        setDriver(new GUIDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        closeTestResources();
    }

}
