package com.AutomationExercise.pages.components;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.utils.logs.LogsManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

public class AllProductsPage {
    public AllProductsPage(GUIDriver driver) {
        this.driver = driver;
        this.productsActions = new ProductsActions(driver);
    }

    GUIDriver driver;
    public WebElement product;
    ProductsActions productsActions;
    CartItemsComponent cartItems;

    private final By searchField = By.id("search_product");
    private final By submitSearch = By.id("submit_search");
    private final By cartModal = By.id("cartModal");

    @Step("Enter a search name")
    public AllProductsPage enterSearchName(String searchName) {
        LogsManager.info("Page step started: Enter a search name");
        driver.elementActions().enterText(searchField, searchName);
        return this;
    }

    @Step("Submit search")
    public AllProductsPage submitSearch() {
        LogsManager.info("Page step started: Submit search");
        driver.elementActions().click(submitSearch);
        return this;
    }

    /* @Step("Hover Over Product")
     public AllProductsPage hoverOverProduct(int order) {
         product = getProductFrame(order);
         //WebElement addToCart = product.findElement(addToCartBtnLoc);
         driver.elementActions().scrollToElementStable(product);
         driver.elementActions().hoverOver(product);
         WebElement overlay = product.findElement(
                 By.cssSelector(".product-overlay")
         );
         driver.elementActions().waitUntilVisible(overlay);
         ///driver.elementActions().waitUntilVisible(product);
         driver.elementActions().waitUntilClickable(product.findElement(addToCartBtnLoc));
         driver.elementActions().waitUntilVisible(product.findElement(productPriceLoc));
         //driver.elementActions().click(addToCart);
         return this;
     }*/
    @Step("Hover Over Product")
    public AllProductsPage hoverOverProduct(int order) {
        LogsManager.info("Page step started: Hover over product card at position:", String.valueOf(order));
        productsActions.hoverOverProduct(order);
        return this;
    }

    @Step("Click Add to Cart Button on Hovered Product")
    public AllProductsPage clickOnHoveredAddToCartButton() {
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
    public AllProductsPage clickOnContinueShoppingButton() {
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

    @Step("Verify all product are displayed")
    public AllProductsPage verifyAllProductsPageIsDisplayed() {
        LogsManager.info("Page step started: Verify all product are displayed");
        driver.verification().assertTrue(
                productsActions.areAllProductsDisplayed()
                ,"Products are not displayed"
        );
        return this;
    }

    @Step("Verify All Products Container Is Visible")
    public AllProductsPage verifyProductsContainerIsDisplayed() {
        LogsManager.info("Page step started: Verify All Products Container Is Visible");
        driver.verification().assertTrue(
                productsActions.isProductsContainerVisible()
                ,"Products container is not displayed");
        return this;
    }

    /* ************************************************************************************ *
     * ********************************* Helpers Components ******************************  *
     * ************************************************************************************ */

    @Step("Click on Add to Cart Button for Product ID: {productId}")
    public AllProductsPage clickOnHoveredAddToCartButton(String productId) {
        LogsManager.info("Page step started: Add product to cart. Product ID:", productId);
        driver.elementActions()
                .click(addToCartBtn(productId));
        return this;
    }

    private By addToCartBtn(String productId) {
        return By.cssSelector(
                "[data-product-id='" + productId + "']"
        );
    }
 public ProductFilterSidebar getProductFilterSidebar() {
        LogsManager.debug("Creating ProductFilterSidebar component for the current browser session.");
        return new ProductFilterSidebar(driver);
 }


 @Step("Verify all displayed product names match search term: {searchText}")
 public AllProductsPage verifyAllProductNamesMatchSearchKey(String searchText) {
        LogsManager.info("Page step started: Verify all displayed product names match search term:", searchText);
        driver.verification().assertTrue(
                productsActions
                        .AreAllProductNamesMatchingSearchKey(searchText)
                ,"Not all product names match the search term"
        );
        return this;
 }

    /*
    * private final Map<String, SelectedProducts> productsData = new LinkedHashMap<>();

private void captureProductData(WebElement product) {
    String id = getProductId(product);
    SelectedProducts fresh = new SelectedProducts(id, getProductName(product), getProductPrice(product), 1);
    productsData.merge(id, fresh, (old, n) ->
            new SelectedProducts(old.id(), old.name(), old.price(), old.qty() + 1));
}

private String getProductName(WebElement product) {
    return driver.elementActions()
            .getText(driver.elementActions().findElement(product, infoNameLoc));
}

private BigDecimal getProductPrice(WebElement product) {
    String value = driver.elementActions()
            .getText(driver.elementActions().findElement(product, infoPriceLoc))
            .replace("Rs.", "").trim();
    return new BigDecimal(value);
}

private void printProductsData() {
    productsData.values().forEach(productData -> LogsManager.debug("Captured product data:", productData.toString()));
}
    * */


}
