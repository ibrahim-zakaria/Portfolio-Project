package com.AutomationExercise.tests;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.pages.components.CategoryHeading;
import com.AutomationExercise.pages.components.NavigationComponent;
import com.AutomationExercise.pages.components.ProductsActions;
import com.AutomationExercise.utils.data.model.AccountData;
import com.AutomationExercise.utils.data.generator.TestDataGenerator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductsTest extends BaseTest {

    @Test(description = "Test Case 8: Verify All Products and product detail page")
    public void verifyAllProductsAndProductDetailPage() {
        //create new user
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .getNavigationComponent()
                .clickOnProductsLink()
                .verifyAllProductsPageIsDisplayed()
                .verifyProductsContainerIsDisplayed()
                .clickOnViewProductBtn(1)
                .verifyProductDetailsPageIsLanded()
                .verifyProductsDetailsIsVisible();
    }

    @Test(description = "Test Case 9: Search Product")
    public void searchProduct() {
        String searchKey = getSearchTestData().getJsonValue("$.search.key");
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnProductsLink()
                .enterSearchName(searchKey)
                .submitSearch()
                .verifyAllProductNamesMatchSearchKey(searchKey);
    }

    @Test(description = "Test Case 14: Place Order: Register while Checkout")
    public void placeOrderAndRegister() {
        AccountData accountData = randomAccountData();
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
                .clickOnProceedToCheckoutLink()
                .backToCart()
                .clickOnRegisterBtn()
                .inputSignupName(accountData.username())
                .inputSignupEmail(accountData.email())
                .clickSignupButton()
                .verifyFormPageDisplayed()
                .fillAccountInformation(accountData)
                .clickCreateAccountButton()
                .verifyAccountCreation()
                .clickContinueButton()
                .verifyNavigationToHomePage()
                .verifyUserIsLoggedIn(accountData.username())
                .clickOnCartLink()
                .verifyCartProducts(ProductsActions.getSelectedProducts())
                .clickOnProceedToCheckoutLink()
                .verifyDeliveryAddress(accountData)
                .clickOnPlaceOrderBtn()
                .fillPaymentDetails(TestDataGenerator.paymentData())
                .clickOnPayAndConfirmOrder()
                .verifyPaymentSuccess()
                .clickOnDownloadInvoice()
                .clickOnContinueBtn()
                .clickOnDeleteAccountLink()
                .verifyAccountDeleted();


    }

    @Test(description = "Test Case 15: Place Order: Register before Checkout")
    public void CheckoutAfterRegister() {
        AccountData accountData = randomAccountData();
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
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
                .clickOnProductsLink()
                .hoverOverProduct(3)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()
                .hoverOverProduct(2)
                .clickOnHoveredAddToCartButton()
                .clickOnViewCartButton()
                .verifyCartIsVisible()
                .verifyCartProducts(ProductsActions.getSelectedProducts())
                .clickOnProceedToCheckoutLink()
                .verifyDeliveryAddress(accountData)
                .verifyUserSelectedProductsMatch(ProductsActions.getSelectedProducts())
                .verifyCartTotal()
                .addComment(TestDataGenerator.randText())
                .clickOnPlaceOrderBtn()
                .fillPaymentDetails(TestDataGenerator.paymentData())
                .clickOnPayAndConfirmOrder()
                .verifyPaymentSuccess()
                .clickOnDownloadInvoice()
                .clickOnContinueBtn()
                .clickOnDeleteAccountLink()
                .verifyAccountDeleted()
                .clickOnContinueBtn();
    }

    @Test(description = "Test Case 16: Place Order: Login before Checkout")
    public void CheckoutAfterLogin() {
        AccountData userData = createTestUser();
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnSignupAndLogin()
                .login(userData.email(), userData.password())
                .verifyUserIsLoggedIn(userData.username())
                .clickOnProductsLink()
                .hoverOverProduct(3)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()
                .hoverOverProduct(2)
                .clickOnHoveredAddToCartButton()
                .clickOnViewCartButton()
                .verifyCartProducts(ProductsActions.getSelectedProducts())
                .clickOnProceedToCheckoutLink()
                .verifyDeliveryAddress(userData)
                .verifyUserSelectedProductsMatch(ProductsActions.getSelectedProducts())
                .verifyCartTotal()
                .addComment(TestDataGenerator.randText())
                .clickOnPlaceOrderBtn()
                .fillPaymentDetails(TestDataGenerator.paymentData())
                .clickOnPayAndConfirmOrder()
                .verifyPaymentSuccess()
                .clickOnContinueBtn()
                .clickOnDeleteAccountLink()
                .verifyAccountDeleted()
                .clickOnContinueBtn();


    }

    @Test(description = "Test Case 18: View Category Products")
    public void viewCategoryProducts() {
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getProductFilterSidebar()
                .verifyCategoriesAreVisible()
                .clickOnWomenCat()
                .clickOnDressCat()
                .verifyHeading(CategoryHeading.WOMEN_DRESS);
    }

    @Test(description = "Test Case 19: View & Cart Brand Products")
    public void viewBrandProducts() {
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .getNavigationComponent()
                .clickOnProductsLink()
                .getProductFilterSidebar()
                .clickOnPoloBrand()
                .verifyHeading(CategoryHeading.BRAND_POLO)
                .verifyFilteredProductsAreDisplayed();
    }

    @Test(description = "Test Case 20: Search Products and Verify Cart After Login")
    public void searchProductsAfterLogin() {
        String searchKey = getSearchTestData().getJsonValue("$.search.key");
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnProductsLink()
                .enterSearchName(searchKey)
                .submitSearch()
                .verifyAllProductNamesMatchSearchKey(searchKey);

    }

    @Test(description = "Test Case 21: Add review on product")
    public void addReviewOnProduct() {
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnProductsLink()
                .verifyAllProductsPageIsDisplayed()
                .clickOnViewProductBtn(1)
                .enterReviewName(TestDataGenerator.randFullName())
                .enterReviewEmail(TestDataGenerator.randEmailAddress())
                .enterReviewText(TestDataGenerator.randText())
                .submitReview()
                ;
    }

    @Test(description = "Test Case 23: Verify address details in checkout page")
    public void verifyCheckoutAddressDetails() {
        AccountData userData = randomAccountData();
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnSignupAndLogin()
                .inputSignupName(userData.username())
                .inputSignupEmail(userData.email())
                .clickSignupButton()
                .verifyFormPageDisplayed()
                .fillAccountInformation(userData)
                .clickCreateAccountButton()
                .verifyAccountCreation()
                .clickContinueButton()
                .verifyUserIsLoggedIn(userData.username())
                .clickOnHomeLink()
                .hoverOverProduct(2)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()
                .hoverOverProduct(4)
                .clickOnHoveredAddToCartButton()
                .clickOnViewCartButton()
                .clickOnProceedToCheckoutLink()
                .verifyDeliveryAddress(userData)
                .getNavigationComponent()
                .clickOnDeleteAccountLink()
                .verifyAccountDeleted()
                .clickOnContinueBtn();
    }

    @Test(description = "Test Case 24: Download Invoice after purchase order")
    public void downloadInvoiceAfterPurchaseOrder() {
        AccountData userData = randomAccountData();
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .scrollOverAllProducts()
                .hoverOverProduct(1)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()
                .hoverOverProduct(33)
                .clickOnHoveredAddToCartButton()
                .clickOnViewCartButton()
                .verifyCartIsVisible()
                .clickOnProceedToCheckoutLink()
                .backToCart()
                .clickOnRegisterBtn()
                .inputSignupName(userData.username())
                .inputSignupEmail(userData.email())
                .clickSignupButton()
                .verifyFormPageDisplayed()
                .fillAccountInformation(userData)
                .clickCreateAccountButton()
                .verifyAccountCreation()
                .clickContinueButton()
                .verifyUserIsLoggedIn(userData.username())
                .clickOnCartLink()
                .clickOnProceedToCheckoutLink()
                .verifyDeliveryAddress(userData)
                .verifyUserSelectedProductsMatch(ProductsActions.getSelectedProducts())
                .addComment(TestDataGenerator.randText())
                .clickOnPlaceOrderBtn()
                .fillPaymentDetails(TestDataGenerator.paymentData())
                .clickOnPayAndConfirmOrder()
                .verifyPaymentSuccess()
                .clickOnContinueBtn()
                .clickOnDeleteAccountLink()
                .verifyAccountDeleted()
                .clickOnContinueBtn();

    }

    @Test(description = "Test Case 25: Verify Scroll Up using 'Arrow' button and Scroll Down functionality")
    public void verifyScrollUpButton() {
        // ProductsActions.waitProductsLoad();
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .getNavigationComponent()
                .clickOnHomeLink()
                .scrollDownToEndOfPage()
                .verifySubscriptionIsVisible()
                .clickOnScrollUpButton();
    }

    @Test(description = "Test Case 26: Verify Scroll Up without 'Arrow' button and Scroll Down functionality")
    public void verifyScrollDownWithoutArrowButton() {
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .scrollDownToEndOfPage()
                .verifySubscriptionIsVisible()
                .scrollUpToTopOfPage()
                .verifyLogoIsVisible();
    }

    @Test(description = "Verify addition of Multi products")
    public void verifyAdditionOfMultiProducts() {
        new NavigationComponent(getDriver())
                .navigateToHomePage()
                .verifyNavigationToHomePage()
                .scrollOverAllProducts()
                .hoverOverProduct(34)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()

                .hoverOverProduct(2)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()

                .hoverOverProduct(15)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()

                .hoverOverProduct(32)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()

                .hoverOverProduct(2)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()


                .hoverOverProduct(33)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()

                .hoverOverProduct(1)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()

                .hoverOverProduct(2)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()

                .hoverOverProduct(15)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()

                .hoverOverProduct(32)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()

                .hoverOverProduct(2)
                .clickOnHoveredAddToCartButton()
                .clickOnContinueShoppingButton()


                .hoverOverProduct(33)
                .clickOnHoveredAddToCartButton()
                .clickOnViewCartButton();
    }

    /***************************************************************************/
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        setDriver(new GUIDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        closeTestResources();
    }

}
