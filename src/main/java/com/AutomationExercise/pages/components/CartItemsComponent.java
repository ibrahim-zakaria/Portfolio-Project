package com.AutomationExercise.pages.components;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CartItemsComponent {
    private final GUIDriver driver;

    public CartItemsComponent(GUIDriver driver) {
        this.driver = Objects.requireNonNull(driver, "GUIDriver must not be null.");
        LogsManager.debug("CartItemsComponent initialized for the current test thread.");
    }

    private final By cartProductRowsLoc = By.cssSelector("tr[id^='product-']");
    private final By productPriceLoc = By.cssSelector("td.cart_price p");
    private final By productQuantityLoc = By.cssSelector("td.cart_quantity");
    private final By placeOrderLinkLoc = By.cssSelector("a[href='/payment']");
    private final By productTotalPriceLoc = By.cssSelector("td.cart_total p");
    //private final By productsIdLoc = By.cssSelector("tr[id^='product-']");
    private final By productsLinkLoc = By.cssSelector("td.cart_description h4 a");
    private final By productCategoryLoc = By.cssSelector("td.cart_description p");
    private final By productsImgLoc = By.cssSelector("td.cart_product a img");

    private final String productDetailsBaseUrl = "https://automationexercise.com/product_details/";
    private final String productImageBaseUrl = "https://automationexercise.com/get_product_picture/";


    /*************************************************************************/

    public boolean areAllProductsMatched(List<SelectedProducts> selectedProducts) {
        LogsManager.info("Validating cart contents against the products selected during this test. Expected item count:",
                String.valueOf(selectedProducts.size()));
        for (SelectedProducts expectedProduct : selectedProducts) {
            CartProducts actualProduct = getProductFromCart(expectedProduct.id());
            if (!verifyProductDataMatches(actualProduct, expectedProduct)) {
                LogsManager.error("Cart product does not match the expected selection. Actual:",
                        actualProduct.toString(), "| Expected:", expectedProduct.toString());
                return false;
            }
        }
        LogsManager.info("All cart products match their expected quantities, unit prices, and line totals.");
        return true;
    }

    private boolean verifyProductDataMatches(CartProducts productInCart, SelectedProducts userProduct) {
        return productInCart.quantity() == userProduct.qty() &&
                productInCart.unitPrice().compareTo(userProduct.price()) == 0 &&
                productInCart.totalPrice().compareTo(userProduct.total()) == 0;
    }

    private CartProducts getProductFromCart(String selectedProductId) {

        String actualProductId = "product-" + selectedProductId;
        return getCartProductsSnapshot()
                .stream()
                .filter(product -> product.productId().equals(actualProductId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Product not found" + actualProductId));
    }

    /*
    **********************{ Linked Hash Map } ***************
    * private CartProducts getProductFromCart(String selectedProductId) {
        String actualProductId = "product-" + selectedProductId;
        return getCartProductsSnapshot()
                .getOrDefault(actualProductId, null) != null
                ? getCartProductsSnapshot().get(actualProductId)
                : throw new NoSuchElementException(
                        "Product not found: " + actualProductId);
    }
    ************************************{End}***********************
    * */


    public BigDecimal getExpectedCartTotalPrice() {
        BigDecimal expectedTotal = ProductsActions.getSelectedProducts()
                .stream()
                .map(SelectedProducts::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LogsManager.info("Calculated expected cart total from the products selected in this test:",
                expectedTotal.toPlainString());
        return expectedTotal;
        //.map(product -> product.total())
        //.reduce(BigDecimal.ZERO, (total, price) -> total.add(price));
    }


    public List<CartProducts> getCartProductsSnapshot() {
        List<WebElement> ProductsRows = driver.elementActions().findElements(cartProductRowsLoc);
        List<CartProducts> cartSnapshot = new ArrayList<>();
        for (WebElement productRow : ProductsRows) {
            cartSnapshot.add(getProductData(productRow));
        }
        LogsManager.info("Captured cart snapshot. Number of product rows:", String.valueOf(cartSnapshot.size()));
        return cartSnapshot;
    }

    /**************************************************************************/

    //To get cart's Count of items
    public int getCartItemsCount() {
        int itemCount = driver.elementActions().findElements(cartProductRowsLoc).size();
        LogsManager.debug("Read the number of product rows currently displayed in the cart:",
                String.valueOf(itemCount));
        return itemCount;
    }

    public List<WebElement> getCartProducts() {
        List<WebElement> products = driver.elementActions().findElements(cartProductRowsLoc);
        LogsManager.debug("Retrieved cart product row elements. Current row count:", String.valueOf(products.size()));
        return products;
    }

    public BigDecimal getBigDecimal(String value) {
        try {
            BigDecimal amount = new BigDecimal(value);
            LogsManager.debug("Parsed monetary value from cart text:", value,
                    "| Numeric value:", amount.toPlainString());
            return amount;
        } catch (NumberFormatException e) {
            LogsManager.error("Could not parse cart monetary value:", value,
                    "| Reason:", e.getMessage());
            throw e;
        }
    }

    public BigDecimal getLineTotalPrice(WebElement product) {
        String displayedTotal = product.findElement(productTotalPriceLoc).getText()
                .replace("Rs.", "").trim();
        BigDecimal total = getBigDecimal(displayedTotal);
        LogsManager.debug("Read cart line total price:", total.toPlainString());
        return total;
    }

    public boolean areProductPricesCorrect() {
        List<WebElement> cartProducts = getCartProducts();
        if (cartProducts.isEmpty()) {
            LogsManager.warn("Cannot validate cart product prices because the cart contains no products.");
            return false;
        }
        for (WebElement cartProduct : cartProducts) {
            BigDecimal actualUnitPrice = getProductPrice(cartProduct);
            if (actualUnitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                LogsManager.error("Cart product has a non-positive unit price:",
                        getProductId(cartProduct), "| Actual price:", actualUnitPrice.toPlainString());
                return false;
            }
        }
        LogsManager.info("Validated that all cart products have a positive unit price. Product count:",
                String.valueOf(cartProducts.size()));
        return true;
    }

    public boolean areProductQuantitiesCorrect() {
        List<WebElement> cartProducts = getCartProducts();
        if (cartProducts.isEmpty()) {
            LogsManager.warn("Cannot validate cart product quantities because the cart contains no products.");
            return false;
        }
        for (WebElement cartProduct : cartProducts) {
            int qty = getQuantity(cartProduct);
            if (qty <= 0) {
                LogsManager.error("Cart product has an invalid quantity:", getProductId(cartProduct),
                        "| Actual quantity:", String.valueOf(qty));
                return false;
            }
        }
        LogsManager.info("Validated that all cart products have a positive quantity. Product count:",
                String.valueOf(cartProducts.size()));
        return true;
    }

    public boolean areProductTotalPricesCorrect() {
        List<WebElement> productsRows = getCartProducts();
        if (productsRows.isEmpty()) {
            LogsManager.warn("Cannot validate cart line totals because the cart contains no products.");
            return false;
        }
        for (WebElement productRow : productsRows) {
            BigDecimal unitPrice = getProductPrice(productRow);
            int qty = getQuantity(productRow);
            BigDecimal expectedLineTotalPrice = unitPrice.multiply(BigDecimal.valueOf(qty));
            BigDecimal actualLineTotalPrice = getLineTotalPrice(productRow);
            if (expectedLineTotalPrice.compareTo(actualLineTotalPrice) != 0) {
                LogsManager.error("Cart line total is incorrect. Product:", getProductId(productRow),
                        "| Expected:", expectedLineTotalPrice.toPlainString(),
                        "| Actual:", actualLineTotalPrice.toPlainString());
                return false;
            }
        }
        LogsManager.info("Validated cart line totals against unit price multiplied by quantity. Product count:",
                String.valueOf(productsRows.size()));
        return true;
    }

    public boolean areProductImagesCorrect() {
        List<WebElement> productsRows = getCartProducts();
        if (productsRows.isEmpty()) {
            LogsManager.warn("Cannot validate cart product images because the cart contains no products.");
            return false;
        }
        for (WebElement productRow : productsRows) {
            String expectedImageUrl = buildExpectedProductUrl(productRow, productImageBaseUrl);
            String actualImageUrl = getProductImageUrl(productRow);
            if (!expectedImageUrl.equals(actualImageUrl)) {
                LogsManager.error("Cart product image URL is incorrect. Product:", getProductId(productRow),
                        "| Expected:", expectedImageUrl, "| Actual:", actualImageUrl);
                return false;
            }
        }
        LogsManager.info("Validated product image URLs for all cart rows. Product count:",
                String.valueOf(productsRows.size()));
        return true;
    }

    public boolean areProductCategoriesNonEmpty() {
        List<WebElement> productsRows = getCartProducts();
        if (productsRows.isEmpty()) {
            LogsManager.warn("Cannot validate cart product categories because the cart contains no products.");
            return false;
        }
        for (WebElement productRow : productsRows) {
            String category = getProductCategory(productRow).trim();
            if (category.isEmpty()) {
                LogsManager.error("Cart product has an empty category label. Product:", getProductId(productRow));
                return false;
            }
        }
        LogsManager.info("Validated that every cart product has a non-empty category label.");
        return true;
    }

    public boolean areProductDetailsLinksCorrect() {
        List<WebElement> productsRows = getCartProducts();
        if (productsRows.isEmpty()) {
            LogsManager.warn("Cannot validate product details links because the cart contains no products.");
            return false;
        }
        for (WebElement productRow : productsRows) {
            String expectedUrl = buildExpectedProductUrl(productRow, productDetailsBaseUrl);
            String actualUrl = getProductDetailsLink(productRow);
            if (!expectedUrl.equals(actualUrl)) {
                LogsManager.error("Cart product details link is incorrect. Product:", getProductId(productRow),
                        "| Expected:", expectedUrl, "| Actual:", actualUrl);
                return false;
            }
        }
        LogsManager.info("Validated product details links for all cart rows.");
        return true;
    }

    public boolean areProductTitlesNotEmpty() {
        List<WebElement> productsRows = getCartProducts();
        if (productsRows.isEmpty()) {
            LogsManager.warn("Cannot validate cart product titles because the cart contains no products.");
            return false;
        }
        for (WebElement productRow : productsRows) {
            if (getProductTitle(productRow).trim().isEmpty()) {
                LogsManager.error("Cart product has an empty title. Product:", getProductId(productRow));
                return false;
            }
        }
        LogsManager.info("Validated that all cart product titles are non-empty.");
        return true;
    }

    public String buildExpectedProductUrl(WebElement productRow, String urlPrefix) {
        String rowId = getProductId(productRow);
        String productId = rowId.substring(rowId.indexOf('-') + 1);
        String expectedUrl = urlPrefix + productId;
        LogsManager.debug("Built expected product URL from cart row:", expectedUrl);
        return expectedUrl;
    }




    /*******************************************************************
     * *************************** INNER TOOLS *********************** *
     ********************************************************************/

    private CartProducts getProductData(WebElement product) {
        return new CartProducts(
                getProductId(product),
                getProductImageUrl(product),
                getProductTitle(product),
                getProductCategory(product),
                getProductPrice(product),
                getQuantity(product),
                getLineTotalPrice(product)
        );
    }

    public String getProductId(WebElement product) {
        String productId = product.getAttribute("id");
        LogsManager.debug("Read cart product row identifier:", productId);
        return productId;
    }

    private String getProductImageUrl(WebElement product) {
        return product.findElement(productsImgLoc)
                .getAttribute("src");
    }

    public String getProductTitle(WebElement product) {
        String title = product.findElement(productsLinkLoc).getText();
        LogsManager.debug("Read product title from cart row:", title);
        return title;
    }

    private String getProductCategory(WebElement product) {
        return product.findElement(productCategoryLoc)
                .getText();
    }

    public int getQuantity(WebElement product) {
        String quantityText = product.findElement(productQuantityLoc).getText().trim();
        int quantity = Integer.parseInt(quantityText);
        LogsManager.debug("Read product quantity from cart row:", String.valueOf(quantity));
        return quantity;
    }

    public BigDecimal getProductPrice(WebElement product) {
        String priceText = product.findElement(productPriceLoc).getText()
                .replace("Rs.", "").trim();
        BigDecimal price = getBigDecimal(priceText);
        LogsManager.debug("Read product unit price from cart row:", price.toPlainString());
        return price;
    }

    private String getProductDetailsLink(WebElement productRow) {
        return productRow.findElement(productsLinkLoc)
                .getAttribute("href");
    }


}
