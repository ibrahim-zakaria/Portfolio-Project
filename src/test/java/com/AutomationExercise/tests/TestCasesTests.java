package com.AutomationExercise.tests;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.pages.components.NavigationComponent;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestCasesTests extends BaseTest{

    @Test(description = "Test Case 7: Verify Test Cases Page")
    public void verifyTestCasesPage(){
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnTestCasesLink()
                .verifyPageTitle();
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
