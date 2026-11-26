package com.AutomationExercise.pages.components;

import com.AutomationExercise.drivers.GUIDriver;
import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductsActions {
    private final GUIDriver driver;
    private WebElement product;

    // Product selections belong to the current test thread, never to the whole JVM.
    private static final ThreadLocal<List<SelectedProducts>> SELECTED_PRODUCTS =
            ThreadLocal.withInitial(ArrayList::new);

    public ProductsActions(GUIDriver driver) {
        this.driver = Objects.requireNonNull(driver, "GUIDriver must not be null.");
        LogsManager.debug("ProductsActions initialized for thread:",
                String.valueOf(Thread.currentThread().getId()));
    }

    private final By productsPageHeadingLabel = By.cssSelector("h2.title");
    private final By allProductCardsLoc = By.cssSelector("div.features_items > div:has(> div.product-image-wrapper)");
    private final By productPriceLoc = By.cssSelector(".product-overlay .overlay-content h2");
    private final By productNameLoc = By.cssSelector(".product-overlay p");
    private final By productCard = By.className("single-products");
    private final By viewProductBtnLoc = By.cssSelector("a[href*='/product_details']");

    //Buttons
    private final By continueShoppingBtnLoc = By.cssSelector("#cartModal button");
    private final By viewCartBtnLoc = By.cssSelector("#cartModal a");
    private final By addToCartBtnLoc = By.cssSelector(".product-overlay a");
    private final By visibleProductNameLocator = By.cssSelector("div.productinfo > p");


    private int duplicateProductIndex = 0;

    public void waitProductsLoad() {
        LogsManager.info("Waiting until all product images are displayed on the products page.");
        boolean loaded = driver.elementActions()
                .areAllElementsDisplayed(By.cssSelector(".productinfo.text-center img"));
        if (!loaded) {
            throw new IllegalStateException("Product images did not become visible before the configured wait timeout.");
        }
        LogsManager.info("All product images are visible.");
    }

    public static void removeAllSelectedProducts() {
        List<SelectedProducts> currentSelections = SELECTED_PRODUCTS.get();
        int removedCount = currentSelections.size();
        SELECTED_PRODUCTS.remove();
        LogsManager.debug("Cleared test-scoped selected-product data. Removed entries:",
                String.valueOf(removedCount), "| Thread:", String.valueOf(Thread.currentThread().getId()));
    }

    public void clickOnContinueShoppingBtn() {
        WebElement modal = driver.elementActions().find(By.id("cartModal"));

        LogsManager.debug("Cart confirmation modal state. Displayed:", String.valueOf(modal.isDisplayed()),
                "| Size:", String.valueOf(modal.getSize()));

        WebElement button = driver.elementActions().find(
                By.cssSelector("#cartModal button")
        );

        LogsManager.debug("Continue Shopping button state. Displayed:", String.valueOf(button.isDisplayed()),
                "| Enabled:", String.valueOf(button.isEnabled()), "| Size:", String.valueOf(button.getSize()));
        driver.elementActions().
                clickOnContinue(continueShoppingBtnLoc);
    }

    public void clickOnViewCartBtn() {
        LogsManager.info("Opening the cart from the add-to-cart confirmation modal.");
        driver.elementActions().click(viewCartBtnLoc);
    }

    public void clickOnAddToCartBtn() {
        LogsManager.info("Clicking the Add to Cart control for the currently selected product.");
        driver.elementActions().click(addToCartBtnLoc);
    }

    public void hoverOverProduct(int order) {
        LogsManager.info("Preparing to hover over product card at position:", String.valueOf(order));
        product = getProductCard(order);

        try {
            WebElement wrapper = product.findElement(By.xpath(".."));
            WebElement overlay = wrapper.findElement(
                    By.cssSelector(".product-overlay"));

            driver.elementActions().hoverUntilVisible(
                    wrapper, By.cssSelector(".product-overlay"));

            driver.elementActions().waitUntilVisible(overlay);

            driver.elementActions().waitUntilClickable(
                    wrapper.findElement(addToCartBtnLoc));

            driver.elementActions().waitUntilVisible(
                    wrapper.findElement(productPriceLoc));

        } catch (RuntimeException e) {
            logHoverDiagnostics(order);
            throw e;
        }
    }

    public void clickOnHoveredAddToCartButton(){
        LogsManager.info("Clicking Add to Cart on the product card currently under hover.");
        WebElement btn = driver.elementActions()
                .findElement(product, addToCartBtnLoc);
        driver.elementActions().waitUntilClickable(btn);
        try {
            //driver.elementActions().hoverOver(product);
            driver.elementActions().click(btn);
        } catch (RuntimeException e) {
            // driver.elementActions().diagnoseClickTarget(btn);
            throw e;
        }
        captureProductData(product);

    }

    public void clickOnViewProductBtn(int order) {
        LogsManager.info("Opening product details for product card position:", String.valueOf(order));
        driver.elementActions().diagnoseClickTarget(getViewProductBtn(order));
        driver.elementActions().click(getViewProductBtn(order));

    }

    /** Returns an immutable snapshot so callers cannot mutate another test's selection state. */
    public static List<SelectedProducts> getSelectedProducts() {
        List<SelectedProducts> snapshot = List.copyOf(SELECTED_PRODUCTS.get());
        LogsManager.debug("Read test-thread selected products snapshot. Item count:",
                String.valueOf(snapshot.size()), "| Thread:", String.valueOf(Thread.currentThread().getId()));
        return snapshot;
    }

    public static void removeFromSelectedProducts(int index) {
        List<SelectedProducts> currentSelections = SELECTED_PRODUCTS.get();
        if (index < 0 || index >= currentSelections.size()) {
            throw new IndexOutOfBoundsException("Selected-product index " + index
                    + " is outside the current test selection list of size " + currentSelections.size() + ".");
        }
        SelectedProducts removed = currentSelections.remove(index);
        LogsManager.info("Removed selected product from test context:", removed.name(),
                "| Product ID:", removed.id());
    }

    public static void addNewSelectedProduct(SelectedProducts newSelectedProduct) {
        Objects.requireNonNull(newSelectedProduct, "Selected product must not be null.");
        SELECTED_PRODUCTS.get().add(newSelectedProduct);
        LogsManager.info("Captured selected product:", newSelectedProduct.name(),
                "| Product ID:", newSelectedProduct.id(), "| Quantity:",
                String.valueOf(newSelectedProduct.qty()), "| Line total:", newSelectedProduct.total().toString());
    }

    public boolean isProductsContainerVisible() {
        boolean visible = driver.elementActions().isDisplayed(productsPageHeadingLabel);
        LogsManager.info("Products page heading visibility check completed. Visible:", String.valueOf(visible));
        return visible;
    }

    public boolean areAllProductsDisplayed() {
        boolean displayed = driver.elementActions().areAllElementsDisplayed(allProductCardsLoc);
        LogsManager.info("Product cards visibility check completed. All displayed:", String.valueOf(displayed));
        return displayed;
    }

    public void captureProductData(WebElement product) {
        LogsManager.info("Capturing product details from the selected product card.");
        BigDecimal price = getProductPrice(product);
        String name = getProductName(product);
        String id = getProductId(product);
        int qty = 1;
        if (!getSelectedProducts().isEmpty()) {
            if (isProductAlreadyCaptured()) {
                SelectedProducts tempProductData = getSelectedProducts().get(duplicateProductIndex);
                qty = tempProductData.qty() + 1;
                removeFromSelectedProducts(duplicateProductIndex);
            }
        }
        SelectedProducts tempProData = new SelectedProducts(
                id,
                name,
                price,
                qty,
                price.multiply(BigDecimal.valueOf(qty))
        );
        addNewSelectedProduct(tempProData);
        LogsManager.debug("Selected-product snapshot updated. Current item count:",
                String.valueOf(SELECTED_PRODUCTS.get().size()));
    }

    private void logHoverDiagnostics(int order) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver.get();
            LogsManager.error("Hover operation failed for product card:", String.valueOf(order));
            LogsManager.error("Browser vertical scroll position:", String.valueOf(js.executeScript("return window.scrollY")));
            LogsManager.error("Product card hover state:",
                    String.valueOf(js.executeScript("return arguments[0].matches(':hover')", product)));
            LogsManager.error("Potential advertising overlays found:", String.valueOf(driver.elementActions().findElements(
                    By.cssSelector("iframe[id^='aswift'], ins.adsbygoogle, #google_vignette")).size()));
            driver.elementActions().diagnoseClickTarget(product);
        } catch (Exception diagEx) {
            LogsManager.error("Unable to collect hover diagnostics:", diagEx.getClass().getSimpleName(),
                    "-", String.valueOf(diagEx.getMessage()));
        }
    }

    private WebElement getProductCard(int order) {
        return driver.elementActions()
                .findElement(
                        getProductByOrder(order)
                        , productCard
                );

    }

    private List<WebElement> getAllProducts() {
        return driver.elementActions()
                .findElements(allProductCardsLoc);
    }

    private WebElement getProductByOrder(int order) {
        List<WebElement> allProducts = getAllProducts();
        if (order <= 0 || order > allProducts.size()) {
            throw new IllegalArgumentException("Product order " + order + " is invalid. The page currently contains "
                    + allProducts.size() + " product cards.");
        }
        return allProducts.get(order - 1);
    }

    private String getProductId(WebElement product) {
        WebElement element = driver.elementActions().findElement(product, addToCartBtnLoc);
        return driver.elementActions().getAttributeValue(element, "data-product-id");
    }

    private String getProductName(WebElement product) {
        WebElement element = driver.elementActions().findElement(
                product
                , productNameLoc
        );
        return driver.elementActions().getText(element);
    }

    private WebElement getViewProductBtn(int order) {
        WebElement viewProductLink = getProductByOrder(order);
        return driver.elementActions()
                .findElement(
                        viewProductLink
                        , viewProductBtnLoc
                );
    }

    private BigDecimal getProductPrice(WebElement product) {
        WebElement element =
                driver.elementActions().findElement(product, productPriceLoc);
        LogsManager.debug("Reading displayed price from product card. Element HTML:",
                element.getAttribute("outerHTML"));

        String value = driver.elementActions().getText(element)
                .replace("Rs.", "").trim();

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            LogsManager.error("Unable to parse product price. Displayed value was:", value);
            throw e;
        }
    }

    private boolean isProductAlreadyCaptured() {
        String id = getProductId(product);
        duplicateProductIndex = 0;
        for (SelectedProducts productData : SELECTED_PRODUCTS.get()) {
            if (productData.id().equals(id)) {
                return true;
            }
            duplicateProductIndex++;
        }
        return false;
    }

    private void printProductsData() {
        LogsManager.debug("Current test-selected products:", getSelectedProducts().toString());
    }

    public boolean AreAllProductNamesMatchingSearchKey(String searchText) {
        List<WebElement> productsNames =
                driver.elementActions().findElements(visibleProductNameLocator);
        if (productsNames.isEmpty()) {
            LogsManager.warn("No products found for search key: " + searchText);
            return false;
        }
        for (WebElement productName : productsNames) {
            if (!productName.getText().toLowerCase().contains(searchText.toLowerCase())) {
                LogsManager.error("Product name does not contain the requested search term. Search term:",
                        searchText, "| Actual product name:", productName.getText());
                return false;
            }
        }
        LogsManager.info("All displayed product names contain the requested search term:", searchText);
        return true;
    }

}
