package com.AutomationExercise.pages.components;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.utils.logs.LogsManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartPage {
    GUIDriver driver;
    private final CartItemsComponent cartItems;
    public List<CartProducts> cartSnapshot;
    List<SelectedProducts> removedProducts = new ArrayList<>();

    public CartPage(GUIDriver driver) {
        this.driver = driver;
        this.cartItems = new CartItemsComponent(driver);
    }

    private final By proceedToCheckoutBtnLoc = By.xpath("//a[normalize-space()='Proceed To Checkout']");
    private final By placeOrderLinkLoc = By.cssSelector("a[href='/payment']");
    private final By itemsTotalPricesLoc = By.cssSelector("td.cart_total_price p");
    private final By itemsRecordsLoc = By.cssSelector("#cart_info_table tr");
    private final By deleteBtnLoc = By.cssSelector(" td a.cart_quantity_delete");
    private final By productDetailsLink = By.cssSelector("td.cart_description a");
    private final By removeProductBtnLoc = By.cssSelector("a.cart_quantity_delete");
    private final By productQtyLoc = By.cssSelector(".cart_quantity button");
    private final By registerAndLoginBtnLoc = By.cssSelector(".modal-body a[href='/login']");
    private final By cartTableLoc = By.id("cart_info_table");

    @Step("Click on proceed to checkout Link")
    public CheckoutPage clickOnProceedToCheckoutLink() {
        LogsManager.info("Page step started: Click on proceed to checkout Link");
        cartSnapshot = cartItems.getCartProductsSnapshot();
        driver.elementActions().click(proceedToCheckoutBtnLoc);
        return new CheckoutPage(driver);
    }

    @Step("Click on the defined Item link")
    public ProductDetailsPage clickOnItemLink(String productId) {
        LogsManager.info("Page step started: Click on the defined Item link");
        driver.elementActions().click(getProductDetailsLink(productId));
        return new ProductDetailsPage(driver);
    }

    @Step("Click on Register/Login link")
    public SignupAndLoginPage clickOnRegisterBtn() {
        LogsManager.info("Page step started: Click on Register/Login link");
        driver.elementActions().click(registerAndLoginBtnLoc);
        return new SignupAndLoginPage(driver);
    }

    @Step("Remove cart product at row position {order}")
    public CartPage removeProductByOrder(int order) {
        LogsManager.info("Page step started: Remove cart product at row position:", String.valueOf(order));
        WebElement targetedProduct = getProductByOrder(order);
        collectProductData(targetedProduct);
        ProductsActions.removeFromSelectedProducts(order - 1);
        WebElement removeBtn = driver.elementActions()
                .findElement(
                        targetedProduct
                        , removeProductBtnLoc
                );
        driver.elementActions().click(getProductRemoveButton(targetedProduct));
        driver.elementActions().waitUntilInvisible(targetedProduct);
        return this;
    }

    private void collectProductData(WebElement product) {
        removedProducts.add(new SelectedProducts(
                cartItems.getProductId(product).split("-")[1],
                cartItems.getProductTitle(product),
                cartItems.getProductPrice(product),
                cartItems.getQuantity(product),
                cartItems.getLineTotalPrice(product)

        ));
    }

    @Step("Verify products are removed from the cart")
    public CartPage verifyProductsAreRemovedFromCart() {
        LogsManager.info("Page step started: Verify products are removed from the cart");
        driver.verification().assertTrue(
                areRemovedProductsAbsentFromCart()
                ,"Some removed products are still present in the cart."
        );
        return this;
    }

    @Step("Remove all Items")
    public CartPage removeAllItems() {
        LogsManager.info("Page step started: Remove all Items");
        int productsCount = cartItems.getCartProducts().size();
        if (productsCount == 0) {
            throw new NoSuchElementException("There are no items in the cart");
        }
        int deleteCount = 0;
        while (!cartItems.getCartProducts().isEmpty()) {
            WebElement product = cartItems.getCartProducts().get(0);
            WebElement removeButton = getProductRemoveButton(product);
            driver.elementActions().click(removeButton);
            driver.elementActions().waitUntilInvisible(product);
            deleteCount++;
        }
        LogsManager.info("Cart cleanup completed. Removed item count:", String.valueOf(deleteCount),
                "| Initial cart item count:", String.valueOf(productsCount));
        return this;
    }

    private BigDecimal calcCartTotalPrice() {
        List<WebElement> cartProducts = cartItems.getCartProducts();
        BigDecimal totalPrice = new BigDecimal(0);
        if (cartProducts.isEmpty()) {
            throw new NoSuchElementException("Cart is empty");
        }
        for (WebElement cartProduct : cartProducts) {
            BigDecimal productLineTotal = cartItems.getLineTotalPrice(cartProduct);
            totalPrice = totalPrice.add(productLineTotal);
        }
        return totalPrice;
    }

    @Step("Verify Cart is visible")
    public CartPage verifyCartIsVisible() {
        LogsManager.info("Page step started: Verify Cart is visible");
        driver.verification().isElementIsVisible(cartTableLoc);
        return this;
    }

    @Step("Verify that product is displayed in cart page with exact quantity")
    public CartPage verifyProductQuantity(String expectedQty) {
        LogsManager.info("Page step started: Verify that product is displayed in cart page with exact quantity");
        String actualQty = driver.elementActions().getText(productQtyLoc);
        driver.verification().equals(actualQty, expectedQty, "Actual qty not equal to expected qty");
        return this;
    }

    @Step("Verify product titles are not empty")
    public CartPage verifyProductTitles() {
        LogsManager.info("Page step started: Verify product titles are not empty");
        driver.validation().assertTrue(
                cartItems.areProductTitlesNotEmpty(),
                "One or more cart product titles are empty"
        );
        return this;
    }

    @Step("Verify product categories are not empty")
    public CartPage verifyProductCategories() {
        LogsManager.info("Page step started: Verify product categories are not empty");
        driver.validation().assertTrue(
                cartItems.areProductCategoriesNonEmpty(),
                "One or more cart product categories are empty"
        );
        return this;
    }

    @Step("Verify product details links are correct")
    public CartPage verifyProductDetailsLinks() {
        LogsManager.info("Page step started: Verify product details links are correct");
        driver.validation().assertTrue(
                cartItems.areProductDetailsLinksCorrect(),
                "One or more product details links are incorrect"
        );
        return this;
    }

    @Step("Verify product images are correct")
    public CartPage verifyProductImages() {
        LogsManager.info("Page step started: Verify product images are correct");
        driver.validation().assertTrue(
                cartItems.areProductImagesCorrect(),
                "One or more product images are incorrect"
        );
        return this;
    }

    @Step("Verify product prices are correct")
    public CartPage verifyProductPrices() {
        LogsManager.info("Page step started: Verify product prices are correct");
        driver.validation().assertTrue(
                cartItems.areProductPricesCorrect(),
                "One or more product prices are incorrect"
        );
        return this;
    }

    @Step("Verify product quantities are correct")
    public CartPage verifyProductQuantities() {
        LogsManager.info("Page step started: Verify product quantities are correct");
        driver.validation().assertTrue(
                cartItems.areProductQuantitiesCorrect(),
                "One or more product quantities are incorrect"
        );
        return this;
    }

    @Step("Verify product quantities are correct")
    public CartPage verifyProductQty(int expectedQty) {
        LogsManager.info("Page step started: Verify product quantities are correct");
        driver.validation().assertTrue(
                cartItems.areProductQuantitiesCorrect(),
                "One or more product quantities are incorrect"
        );
        return this;
    }

    @Step("Verify product total prices are correct")
    public CartPage verifyProductTotalPrices() {
        LogsManager.info("Page step started: Verify product total prices are correct");
        driver.validation().assertTrue(
                cartItems.areProductTotalPricesCorrect(),
                "One or more product total prices are incorrect"
        );
        return this;
    }

    @Step("Verify cart products match expected products")
    public CartPage verifyCartProducts(List<SelectedProducts> expectedProducts) {
        LogsManager.info("Page step started: Verify cart products match expected products");
        driver.verification().assertTrue(areAllProductsMatched(expectedProducts), "Data of cart mismatch to selected user products ");
        return this;
    }

    public boolean areRemovedProductsAbsentFromCart() {
        LogsManager.info("Verifying that removed products are no longer present in the cart. Removed item count:",
                String.valueOf(removedProducts.size()));
        boolean allRemovedProductsAbsent = removedProducts.stream()
                .noneMatch(removed -> !driver.elementActions()
                        .findElements(By.id("product-" + removed.id())).isEmpty());
        if (allRemovedProductsAbsent) {
            LogsManager.info("All products marked for removal are absent from the cart.");
        } else {
            LogsManager.error("At least one product marked for removal is still displayed in the cart.");
        }
        return allRemovedProductsAbsent;
    }
    private WebElement getProductByOrder(int order) {
        return cartItems
                .getCartProducts().get(order - 1);
    }

    private boolean areAllProductsMatched(List<SelectedProducts> expectedProducts) {
        for (SelectedProducts product : expectedProducts) {
            CartProducts productInCart = getProductFromCart(product.id());
            if (!verifyProductDataMatches(productInCart, product)) {
                LogsManager.error("Cart product does not match expected selection. Actual: " + productInCart + " | Expected: " + product);
                return false;
            }
        }
        return true;
    }

    private boolean verifyProductDataMatches(CartProducts productInCart, SelectedProducts userProduct) {
        return productInCart.quantity() == userProduct.qty() &&
                productInCart.unitPrice().compareTo(userProduct.price()) == 0 &&
                productInCart.totalPrice().compareTo(userProduct.total()) == 0;
    }

    private CartProducts getProductFromCart(String productId) {
        List<CartProducts> actualProducts = cartItems.getCartProductsSnapshot();
        for (CartProducts product : actualProducts) {
            if (product.productId().equals("product-" + productId)) {
                return product;
            }
        }
        throw new NoSuchElementException("Product not found");
    }



    private WebElement getProductDetailsLink(String productId) {
        List<WebElement> cartProducts = cartItems.getCartProducts();
        for (WebElement cartProduct : cartProducts) {
            if (cartProduct.getAttribute("id").equals(productId)) {
                return driver.elementActions().findElement(cartProduct, productDetailsLink);
            }
        }
        throw new NoSuchElementException("Cart item not found: " + productId);
    }

    private WebElement getProductRemoveButton(WebElement product) {
        return driver.elementActions().findElement(product, removeProductBtnLoc);
    }
    public NavigationComponent getNavigationComponent() {
        LogsManager.debug("Creating NavigationComponent from:", getClass().getSimpleName());
        return new NavigationComponent(driver);
    }


    private final By subscribeTxtLocator = By.cssSelector("div.single-widget h2");
    private final By subscribeEmailLocator = By.id("susbscribe_email");
    private final By submitSubscribe = By.id("subscribe");
    private final By successSubsMsgLocator = By.cssSelector("div.alert-success.alert");



    @Step("Enter Subscription Email address")
    public CartPage enterSubscriptionEmail(String email) {
        LogsManager.info("Page step started: Enter Subscription Email address");
        driver.elementActions().enterText(subscribeEmailLocator,email);
        return this;
    }

    @Step("Click on submit of Subscription")
    public CartPage clickOnSubscriptionSubmitButton() {
        LogsManager.info("Page step started: Click on submit of Subscription");
        driver.elementActions().click(submitSubscribe);
        return this;
    }


    @Step("Verify SUBSCRIPTION is visible")
    public CartPage verifySubscriptionIsVisible() {
        LogsManager.info("Page step started: Verify SUBSCRIPTION is visible");
        driver.verification().isElementIsVisible(subscribeTxtLocator);
        return this;
    }

    @Step("Verify the subscription success message is visible")
    public CartPage verifySubsSuccessMsgIsVisible() {
        LogsManager.info("Page step started: Verify the subscription success message is visible.");
        String actualMsgTxt = driver.elementActions().getText(subscribeTxtLocator);
        String expectedMsgTxt = "You have been successfully subscribed!";
        driver.verification().equals(
                actualMsgTxt
                ,expectedMsgTxt
                ,"Success message text doesn't match"
        );
        return this;
    }

}
