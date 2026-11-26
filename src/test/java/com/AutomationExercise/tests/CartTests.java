package com.AutomationExercise.tests;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.pages.components.NavigationComponent;
import com.AutomationExercise.pages.components.ProductsActions;
import com.AutomationExercise.utils.data.generator.TestDataGenerator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CartTests extends BaseTest {

    @Test(description = "Test Case 11: Verify Subscription in Cart page", groups = {"regression"})
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

    @Test(description = "Test Case 12: Add Products in Cart", invocationCount = 1, groups = {"regression"})
    public void addProductToCart() {
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnProductsLink()
                .verifyProductsContainerIsDisplayed()
                .hoverOverProduct(1)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()
                .hoverOverProduct(2)
                .clickOnHoveredAddToCartButton()
                .clickOnViewCartButton()
                .verifyCartProducts(ProductsActions.getSelectedProducts());
        //.verifyCartProducts(AllProductsPage.selectedProducts);

    }

    @Test(description = "Test Case 13: Verify Product quantity in Cart", groups="jenkins")
    public void testProductQuantityInCart() {
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .clickOnViewProductOnFirst()
                .increaseQuantity(3)
                .clickOnAddToCartBtn()
                .clickOnViewCartBtn()
                .verifyProductQuantity(3 + "");
    }

    @Test(description = "Test Case 22: Add to cart from Recommended items")
    public void addRecommendedProductToCart() {
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnProductsLink()
                .clickOnViewProductBtn(5)
                .enterReviewName(TestDataGenerator.randFullName())
                .enterReviewEmail(TestDataGenerator.randEmailAddress())
                .enterReviewText(TestDataGenerator.randText())
                .submitReview();
    }

    @Test(description = "Test Case 17: Remove Products From Cart")
    public void removeProductFromCart() {
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .hoverOverProduct(1)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()
                .hoverOverProduct(5)
                .clickOnHoveredAddToCartButton()
                .clickOnViewCartButton()
                .verifyCartIsVisible()
                .removeProductByOrder(2)
                .verifyProductsAreRemovedFromCart();
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
