package com.AutomationExercise.tests;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.pages.components.NavigationComponent;
import com.AutomationExercise.pages.components.ProductsActions;
import com.AutomationExercise.utils.data.generator.TestDataGenerator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HomeTests extends BaseTest{

    @Test(description = "Test Case 10: Verify Subscription in home page" , groups = {"sanity", "TESTER","jenkins"})
    public void verifySubscriptionInHome(){
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .scrollDownToEndOfPage()
                .verifySubscriptionIsVisible()
                .enterSubscriptionEmail(TestDataGenerator.randEmailAddress())
                .clickOnSubscriptionSubmitButton()
                .verifySubsSuccessMsgIsVisible();
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
