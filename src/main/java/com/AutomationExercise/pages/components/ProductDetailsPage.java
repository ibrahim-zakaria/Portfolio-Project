package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ProductDetailsPage {
    GUIDriver driver;

    public ProductDetailsPage(GUIDriver driver) {
        this.driver = driver;
    }

    private final By productImg = By.cssSelector("div.view-product>img ");
    private final By productTitle = By.cssSelector("div.product-information>h2");
    private final By productPrice = By.cssSelector("div.product-information>span>span");
    private final By productQtyLoc = By.id("quantity");
    private final By addToCartBtnLoc = By.cssSelector(".product-information button");
    private final By availabilityLoc = By.xpath("//p[b[normalize-space()='Availability:']]");
    private final By productBrandLoc = By.xpath("//p[b[normalize-space()='Brand:']]");
    private final By productConditionLoc = By.xpath("//p[b[normalize-space()='Condition:']]");
    private final By reviewTextareaLoc = By.id("review");
    private final By categoryLoc = By.xpath("//p[contains(normalize-space(), 'Category')]");
    private final By quantityBtnLoc = By.id("quantity");
    private final By continueShoppingBtnLoc = By.cssSelector(".modal-footer button");
    private final By viewCartBtnLoc = By.cssSelector(".modal-content a[href='/view_cart']");

    private final By submitReviewBtnLoc = By.id("button-review");
    private final By reviewNameLoc = By.id("name");
    private final By reviewEmailLoc = By.id("email");

    @Step("Enter review Name")
    public ProductDetailsPage enterReviewName(String name) {
        LogsManager.info("Page step started: Enter review Name");
        driver.elementActions().enterText(reviewNameLoc, name);
        return this;
    }

    @Step("Enter review Email")
    public ProductDetailsPage enterReviewEmail(String email) {
        LogsManager.info("Page step started: Enter review Email");
        driver.elementActions().enterText(reviewEmailLoc, email);
        return this;
    }

    @Step("Enter review text")
    public ProductDetailsPage enterReviewText(String reviewText) {
        LogsManager.info("Page step started: Enter review text");
        driver.elementActions().enterText(reviewTextareaLoc, reviewText);
        return this;
    }

    @Step("Submit review")
    public ProductDetailsPage submitReview() {
        LogsManager.info("Page step started: Submit review");
        driver.elementActions().click(submitReviewBtnLoc);
        return this;
    }


    @Step("Click on view cart button")
    public CartPage clickOnViewCartBtn() {
        LogsManager.info("Page step started: Click on view cart button");
        driver.elementActions().click(viewCartBtnLoc);
        return new  CartPage(driver);
    }
    @Step("Set product quantity to {qty}")
    public ProductDetailsPage increaseQuantity(int qty) {
        LogsManager.info("Page step started: Set product quantity to:", String.valueOf(qty));
       // int currentQty = Integer.parseInt(
       //         driver.elementActions().getAttributeValue(quantityBtnLoc, "value")
       // );
        driver.elementActions().find(productQtyLoc).clear();
       // int newQty = currentQty + 1;
        driver.elementActions().enterText(quantityBtnLoc, String.valueOf(qty));
        return this;
    }

    @Step("Click on add to cart button")
    public ProductDetailsPage clickOnAddToCartBtn() {
        LogsManager.info("Page step started: Click on add to cart button");
        driver.elementActions().click(addToCartBtnLoc);
        return this;
    }

    @Step("Click on view cart button")
    public ProductDetailsPage clickOnContinueShoppingBtn() {
        LogsManager.info("Page step started: Click Continue Shopping in the cart confirmation modal.");
        driver.elementActions().click(continueShoppingBtnLoc);
        return this;
    }


    @Step("Verify Product Details Page")
    public ProductDetailsPage verifyProductDetailsPageIsLanded() {
        LogsManager.info("Page step started: Verify Product Details Page");
        driver.verification().assertTrue(
                driver.browserActions().getCurrentUrl()
                        .contains("/product_details/"),
                "User was not navigated to the Product Details Page"
        );
        return this;
    }

    @Step("Verify Product Details Are Visible")
    public ProductDetailsPage verifyProductsDetailsIsVisible() {
        LogsManager.info("Page step started: Verify Product Details Are Visible");
        driver.validation().isElementIsVisible(productTitle);
        driver.validation().isElementIsVisible(categoryLoc);
        driver.validation().isElementIsVisible(productPrice);
        driver.validation().isElementIsVisible(availabilityLoc);
        driver.validation().isElementIsVisible(productConditionLoc);
        driver.validation().isElementIsVisible(productBrandLoc);
        return this;
    }

    private Float getPrice() {
        return Float.parseFloat(
                driver.elementActions().getText(productPrice)
                        .replace("Rs.", "")
                        .trim()
        );
    }

    private String getAvailabilityStatus() {
        return driver.elementActions().getText(availabilityLoc)
                .replace("Availability:", "")
                .trim();
    }

    private String getBrand() {
        return driver.elementActions().getText(productBrandLoc)
                .replace("Brand:", "")
                .trim();
    }

    private String getConditionStatus() {
        return driver.elementActions().getText(productConditionLoc)
                .replace("Condition:", "")
                .trim();
    }

    public Boolean verifyProductPrice() {
        double actualPrice = getPrice();
        boolean validPrice = actualPrice > 0;
        if (validPrice) {
            LogsManager.info("Product price validation passed. Displayed price:", String.valueOf(actualPrice));
        } else {
            LogsManager.error("Product price validation failed. Expected a positive price but found:",
                    String.valueOf(actualPrice));
        }
        return validPrice;
    }


}
