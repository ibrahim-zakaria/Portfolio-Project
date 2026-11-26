package com.AutomationExercise.pages.components;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.utils.logs.LogsManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class HomePage {
    private GUIDriver driver;
    ProductsActions productsActions;

    public HomePage(GUIDriver driver) {
        this.driver = driver;
        productsActions = new ProductsActions(driver);
    }

    private final By homeSlider = By.id("slider");
    private final By userNameLabelLoc = By.tagName("b");
    private final By firstProductViewBtnLoc = By.cssSelector("a[href='/product_details/1']");
    private final By scrollUpBtnLoc = By.cssSelector("a#scrollUp");
    private final By websiteLogo = By.cssSelector("div.logo img");
    private final By subscribeTxtLocator = By.cssSelector("div.single-widget h2");
    private final By subscribeEmailLocator = By.id("susbscribe_email");
    private final By submitSubscribe = By.id("subscribe");
    private final By successSubsMsgLocator = By.cssSelector("div.alert-success.alert");
//#recommended-item-carousel a[data-product-id] *****************
    @Step("Open the details page for the first recommended product")
    public ProductDetailsPage clickOnViewProductOnFirst() {
        LogsManager.info("Opening the product details page for the first recommended product.");
        driver.elementActions().click(firstProductViewBtnLoc);
        return new ProductDetailsPage(driver);
    }
    @Step("Scroll over all products")
    public HomePage scrollOverAllProducts() {
        LogsManager.info("Page step started: Scroll over all products");
        driver.elementActions().scrollTBottomOfPage();
        return this;
    }

    @Step("Hover Over Product")
    public HomePage hoverOverProduct(int order) {
        LogsManager.info("Page step started: Hover over product card at position:", String.valueOf(order));
        productsActions.hoverOverProduct(order);
        return this;
    }

    @Step("Click Add to Cart Button on Hovered Product")
    public HomePage clickOnHoveredAddToCartButton() {
        LogsManager.info("Page step started: Click Add to Cart Button on Hovered Product");
        productsActions.clickOnHoveredAddToCartButton();
        return this;
    }

    @Step("Click View Product Button for Product #{order}")
    public ProductDetailsPage clickOnViewProductBtn(int order) {
        LogsManager.info("Page step started: Open product details for product card position:", String.valueOf(order));
        productsActions.clickOnViewProductBtn(order);
        return new ProductDetailsPage(driver);
    }

    @Step("Click on Continue Shopping Button")
    public HomePage clickOnContinueShoppingButton() {
        LogsManager.info("Page step started: Click on Continue Shopping Button");
        productsActions.clickOnContinueShoppingBtn();
        return this;
    }

    @Step("Click on view cart Button")
    public CartPage clickOnViewCartButton() {
        LogsManager.info("Page step started: Click on view cart Button");
        productsActions.clickOnViewCartBtn();
        return new CartPage(driver);
    }

    @Step("Click On Scroll-up button")
    public HomePage clickOnScrollUpButton() {
        LogsManager.info("Page step started: Click On Scroll-up button");
        driver.elementActions().click(scrollUpBtnLoc);
        return this;
    }
    @Step("Enter Subscription Email address")
    public HomePage enterSubscriptionEmail(String email) {
        LogsManager.info("Page step started: Enter Subscription Email address");
        driver.elementActions().enterText(subscribeEmailLocator,email);
        return this;
    }

    @Step("Click on submit of Subscription")
    public HomePage clickOnSubscriptionSubmitButton() {
        LogsManager.info("Page step started: Click on submit of Subscription");
        driver.elementActions().click(submitSubscribe);
        return this;
    }


    @Step("Scroll up to the Top of Home page")
    public HomePage scrollUpToTopOfPage() {
        LogsManager.info("Page step started: Scroll up to the Top of Home page");
        driver.elementActions().scrollToTop();
        return this;
    }

    @Step("Verify all product are displayed")
    public HomePage verifyAllProductsPageIsDisplayed() {
        LogsManager.info("Page step started: Verify all product are displayed");
        driver.verification().assertTrue(
                productsActions.areAllProductsDisplayed()
                , "Products are not displayed"
        );
        return this;
    }

    @Step("Verify All Products Container Is Visible")
    public HomePage verifyProductsContainerIsDisplayed() {
        LogsManager.info("Page step started: Verify All Products Container Is Visible");
        driver.verification().assertTrue(
                productsActions.isProductsContainerVisible()
                , "Products container is not displayed");
        return this;
    }
    @Step("Scroll to bottom of home page")
    public HomePage scrollDownToEndOfPage() {
        LogsManager.info("Page step started: Scroll to bottom of home page");
        //driver.elementActions().scrollTBottomOfPage();
        //driver.elementActions().scrollTBottomOfPage();
        driver.elementActions().scrollUntilProductsLoad2();
        return new HomePage(driver);
    }

    /**********************************************************************************
     ***************************** Validation & Verification **************************
     **********************************************************************************/

    @Step("Verify Home Page by Home-Slider. is it Visible")
    public HomePage verifyNavigationToHomePage() {
        LogsManager.info("Page step started: Verify Home Page by Home-Slider. is it Visible");
        driver.verification().isElementIsVisible(homeSlider);
        return new HomePage(driver);
    }
    @Step("Verify Website logo is Visible")
    public HomePage verifyLogoIsVisible() {
        LogsManager.info("Page step started: Verify Website logo is Visible");
        driver.verification().isElementIsVisible(websiteLogo);
        return this;
    }

    @Step("Verify that 'Logged in as username' is visible #{expectedUserName}")
    public NavigationComponent verifyUserIsLoggedIn(String expectedUserName) {
        LogsManager.info("Page step started: Verify the logged-in username. Expected:", expectedUserName);
        String actualUserName = driver.elementActions().getText(userNameLabelLoc);
        LogsManager.info("Retrieved logged-in username from the home page:", actualUserName);
        driver.verification().equals(expectedUserName, actualUserName, "User label does not match. Expected: " + expectedUserName + ", Actual: " + actualUserName);
        return new NavigationComponent(driver);
    }
    @Step("Verify SUBSCRIPTION is visible")
    public HomePage verifySubscriptionIsVisible() {
        LogsManager.info("Page step started: Verify SUBSCRIPTION is visible");
        driver.verification().isElementIsVisible(subscribeTxtLocator);
        return new HomePage(driver);
    }

    @Step("Verify the subscription success message is visible")
    public HomePage verifySubsSuccessMsgIsVisible() {
        LogsManager.info("Page step started: Verify the subscription success message is visible.");
        driver.verification().isElementIsVisible(successSubsMsgLocator);
        return this;
    }

    public NavigationComponent getNavigationComponent() {
        LogsManager.debug("Creating NavigationComponent for the current browser session.");
        return new NavigationComponent(driver);
    }

    public ProductFilterSidebar getProductFilterSidebar() {
        LogsManager.debug("Creating ProductFilterSidebar for the current browser session.");
        return new ProductFilterSidebar(driver);
    }



}
