package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.utils.data.model.AccountData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

public class CheckoutPage {
    GUIDriver driver;
    CartItemsComponent cartItems;
    //private final CartItemsComponent orderItems;
    public CheckoutPage(GUIDriver driver) {
        this.driver = driver;
        cartItems = new CartItemsComponent(driver);
    }

    private final By cartTotalPrice = By.xpath("//tr[td[@colspan='2']//b[normalize-space()='Total Amount']]//p[@class='cart_total_price']");
   private final  By placeOrderBtn = By.cssSelector("a[href='/payment']");
    // Delivery Address
    private final By deliveryName = By.cssSelector("#address_delivery .address_firstname");
    private final By deliveryAddressLines = By.cssSelector("#address_delivery .address_address1.address_address2");
    private final By deliveryCity = By.cssSelector("#address_delivery .address_city");
    private final By deliveryCountry = By.cssSelector("#address_delivery .address_country_name");
    private final By deliveryPhone = By.cssSelector("#address_delivery .address_phone");

    // Invoice Address
    private final By invoiceName = By.cssSelector("#address_invoice .address_firstname");
    private final By invoiceAddressLines = By.cssSelector("#address_invoice .address_address1.address_address2");
    private final By invoiceCity = By.cssSelector("#address_invoice .address_city");
    private final By invoiceCountry = By.cssSelector("#address_invoice .address_country_name");
    private final By invoicePhone = By.cssSelector("#address_invoice .address_phone");
    private final By commentFieldLoc = By.cssSelector("textarea[name='message']");


    @Step("Add Comment on textarea")
    public CheckoutPage addComment(String comment) {
        LogsManager.info("Page step started: Add Comment on textarea");
        driver.elementActions().enterText(commentFieldLoc, comment);
        return this;
    }
    @Step("the delivery address should match the registration details")
    public CheckoutPage verifyDeliveryAddress(AccountData accountData){
        LogsManager.info("Page step started: the delivery address should match the registration details");
       //Expected Data
        String title = accountData.gender().equalsIgnoreCase("Male")
                ? "Mr. "
                : "Mrs. ";
        String expectedName = title + accountData.firstName() + " " + accountData.lastName();
        String expectedCompany = accountData.company();
        String expectedAddress1 = accountData.address1();
        String expectedAddress2 = accountData.address2();
        String expectedCityStateZip = accountData.city() + " " + accountData.state() + " " + accountData.zipcode();
        String expectedCountry = accountData.country();
        String expectedPhone = accountData.mobileNumber();

        //Actual Sorted Data
        String actualDeliveryName = driver.elementActions().getText(deliveryName);
        List<WebElement> deliveryLines = driver.elementActions().findElements(deliveryAddressLines);
        String actualDeliveryCompany = driver.elementActions().getText(deliveryLines.get(0));
        String actualDeliveryAddress1 = driver.elementActions().getText(deliveryLines.get(1));
        String actualDeliveryAddress2 = driver.elementActions().getText(deliveryLines.get(2));
        String actualDeliveryCityStateZip = driver.elementActions().getText(deliveryCity);
        String actualDeliveryCountry = driver.elementActions().getText(deliveryCountry);
        String actualDeliveryPhone = driver.elementActions().getText(deliveryPhone);

        String actualInvoiceName = driver.elementActions().getText(invoiceName);
        List<WebElement> invoiceLines = driver.elementActions().findElements(invoiceAddressLines);
        String actualInvoiceCompany = driver.elementActions().getText(invoiceLines.get(0));
        String actualInvoiceAddress1 = driver.elementActions().getText(invoiceLines.get(1));
        String actualInvoiceAddress2 = driver.elementActions().getText(invoiceLines.get(2));
        String actualInvoiceCityStateZip = driver.elementActions().getText(invoiceCity);
        String actualInvoiceCountry = driver.elementActions().getText(invoiceCountry);
        String actualInvoicePhone = driver.elementActions().getText(invoicePhone);

        driver.validation().assertEquals(actualDeliveryName, expectedName,"Delivery name does not match");
        driver.validation().assertEquals(actualDeliveryCompany, expectedCompany,"Delivery company does not match");
        driver.validation().assertEquals( actualDeliveryAddress1, expectedAddress1,"Delivery address 1 does not match");
        driver.validation().assertEquals(actualDeliveryAddress2, expectedAddress2,"Delivery address 2 does not match");
        driver.validation().assertEquals( actualDeliveryCityStateZip, expectedCityStateZip,"Delivery city, state, and zipcode do not match");
        driver.validation().assertEquals( actualDeliveryCountry, expectedCountry,"Delivery country does not match");
        driver.validation().assertEquals(actualDeliveryPhone, expectedPhone,"Delivery phone does not match");

        driver.validation().assertEquals( actualInvoiceName, expectedName,"Invoice name does not match");
        driver.validation().assertEquals(actualInvoiceCompany, expectedCompany,"Invoice company does not match");
        driver.validation().assertEquals( actualInvoiceAddress1, expectedAddress1,"Invoice address 1 does not match");
        driver.validation().assertEquals( actualInvoiceAddress2, expectedAddress2,"Invoice address 2 does not match");
        driver.validation().assertEquals( actualInvoiceCityStateZip, expectedCityStateZip,"Invoice city, state, and zipcode do not match");
        driver.validation().assertEquals(actualInvoiceCountry, expectedCountry,"Invoice country does not match");
        driver.validation().assertEquals(actualInvoicePhone, expectedPhone,"Invoice phone does not match");

        return this;
    }

    @Step("Click Place Order to navigate to the Payment Page")
    public PaymentPage clickOnPlaceOrderBtn(){
        LogsManager.info("Page step started: Click Place Order to navigate to the Payment Page");
        driver.elementActions().click(placeOrderBtn);
        return new PaymentPage(driver);
    }
    @Step("Back to the Cart")
    public CartPage backToCart(){
        LogsManager.info("Page step started: Back to the Cart");
        return new CartPage(driver);
    }

    @Step("Verify user-selected products match cart products")
    public CheckoutPage verifyUserSelectedProductsMatch(List<SelectedProducts> selectedProducts){
        LogsManager.info("Page step started: Verify user-selected products match cart products");
       driver.verification().assertTrue(
               cartItems.areAllProductsMatched(selectedProducts)
       ,"Cart items are not matched");
        return this;
    }


    @Step("Verify cart products match expected products")
    public CheckoutPage verifyCartProducts(List<CartProducts> expectedProducts) {
        LogsManager.info("Page step started: Verify cart products match expected products");
        List<CartProducts> actualProducts = cartItems.getCartProductsSnapshot();
        driver.validation().assertEquals(actualProducts.size(), expectedProducts.size(), "Cart products count does not match");
        driver.validation().assertTrue(
                new HashSet<>(actualProducts).containsAll(expectedProducts)
                        && new HashSet<>(expectedProducts).containsAll(actualProducts),
                "Cart products do not match expected products"
        );
        return this;
    }

    @Step("Verify expected cart total matches displayed cart total")
    public CheckoutPage verifyCartTotal(){
        LogsManager.info("Page step started: Verify expected cart total matches displayed cart total");
        BigDecimal expectedTotal = cartItems.getExpectedCartTotalPrice();
        BigDecimal actualTotal = getActualCartTotalPrice();
        driver.validation().assertTrue(
                actualTotal.compareTo(expectedTotal) == 0,
                "Cart total mismatch. Expected: " + expectedTotal
                        + ", Actual: " + actualTotal
        );
        return this;
    }

    public NavigationComponent getNavigationComponent() {
        LogsManager.debug("Creating NavigationComponent from:", getClass().getSimpleName());
        return new NavigationComponent(driver);
    }

    private BigDecimal calculateTotalPrice() {
        List<WebElement> products = cartItems.getCartProducts();
        BigDecimal totalCartPrice = new BigDecimal(0);
        for (WebElement product : products) {
            BigDecimal productTotalPrice = cartItems.getLineTotalPrice(product);
            totalCartPrice = totalCartPrice.add(productTotalPrice);
        }
        return totalCartPrice;
    }
    private BigDecimal getActualCartTotalPrice(){
        return cartItems.getBigDecimal( driver.elementActions().getText(cartTotalPrice)
                .replace("Rs.","")
                .trim()
        );
    }



}
