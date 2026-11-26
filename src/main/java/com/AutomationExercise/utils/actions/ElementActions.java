package com.AutomationExercise.utils.actions;

import com.AutomationExercise.utils.wait.WaitManager;
import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ElementActions {
    WebDriver driver;
    WaitManager wait;

    public ElementActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitManager(driver);
    }

    public void enterText(By locator, String text) {
        try {
            WebElement element = wait.fluentWait().until(
                    ExpectedConditions.visibilityOfElementLocated(locator));
            new Actions(driver).moveToElement(element).perform();
            element.sendKeys(text);
            LogsManager.info("Entered text into input field:", locator.toString(),
                    "| Supplied value:", maskValue(text));
        } catch (RuntimeException e) {
            LogsManager.error("Unable to enter text into input field:", locator.toString(),
                    "| Reason:", e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
            throw e;
        }
    }

    public void enterMaskedText(By locator, String value) {
        try {
            WebElement element = wait.fluentWait().until(
                    ExpectedConditions.visibilityOfElementLocated(locator));
            element.sendKeys(value);
            LogsManager.info("Entered sensitive text into input field:", locator.toString(),
                    "| Supplied value:", maskValue(value));
        } catch (RuntimeException e) {
            LogsManager.error("Unable to enter sensitive text into input field:", locator.toString(),
                    "| Reason:", e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
            throw e;
        }
    }

    private String maskValue(String value) {
        if (value == null || value.isEmpty()) {
            return "****";
        }
        return "[REDACTED; length=" + value.length() + "]";
    }

    public String getText(By locator) {
        try {
            String text = wait.fluentWait().until(d -> {
                try {
                    WebElement element = d.findElement(locator);
                    String value = element.getText();
                    return value != null && !value.isBlank() ? value : null;
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return null;
                }
            });
            LogsManager.debug("Retrieved non-empty visible text from element:", locator.toString(),
                    "| Text:", text);
            return text;
        } catch (RuntimeException e) {
            LogsManager.error("Unable to retrieve non-empty text from element before timeout:",
                    locator.toString(), "| Reason:", e.getClass().getSimpleName(), "-",
                    String.valueOf(e.getMessage()));
            throw e;
        }
    }

    public String getText(WebElement element) {
        String text = element.getText();
        LogsManager.debug("Retrieved text from WebElement:", element.toString(), "| Text:", text);
        return text;
    }

    public String getAttributeValue(WebElement element, String attributeName) {
        String value = element.getAttribute(attributeName);
        LogsManager.debug("Retrieved element attribute:", attributeName, "| Element:", element.toString(),
                "| Value:", value);
        return value;
    }

    public String getAttributeValue(By locator, String attributeName) {
        wait.fluentWait().until(d -> {
            WebElement element = find(locator);
            scrollToElementJS(locator);
            return element.getAttribute(attributeName) != null;
        });
        WebElement element = find(locator);
        String value = element.getAttribute(attributeName);
        LogsManager.debug("Retrieved element attribute:", attributeName, "| Locator:", locator.toString(),
                "| Value:", value);
        return value;
    }

    public void click(By locator) {
        try {
            WebElement element = wait.fluentWait().until(
                    ExpectedConditions.elementToBeClickable(locator));
            scrollToElementJS(locator);
            element.click();
            LogsManager.info("Clicked element successfully:", locator.toString());
        } catch (RuntimeException e) {
            LogsManager.error("Unable to click element:", locator.toString(),
                    "| Reason:", e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
            throw e;
        }
    }

    public void clickOnContinue(By locator) {
        try {
            WebElement element = wait.fluentWait().until(
                    ExpectedConditions.elementToBeClickable(locator));
            scrollToElementJS(locator);

            String diagnostic = (String) ((JavascriptExecutor) driver)
                    .executeScript("""
                            const protector = document.querySelector('#click-protector');
                            const button = arguments[0];
                            return JSON.stringify({
                                protectorExists: !!protector,
                                protectorParent: protector?.parentElement?.outerHTML,
                                buttonParent: button.parentElement?.outerHTML,
                                protectorInsideButton: button.contains(protector),
                                buttonInsideProtector: protector?.contains(button)
                            });
                            """, element);
            LogsManager.debug("Continue Shopping modal click diagnostics:", diagnostic);

            element.click();
            LogsManager.info("Clicked Continue Shopping button in the cart confirmation modal:",
                    locator.toString());
        } catch (RuntimeException e) {
            LogsManager.error("Unable to click Continue Shopping button:", locator.toString(),
                    "| Reason:", e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
            throw e;
        }
    }


    public void click(WebElement element) {
        try {
            WebElement clickableElement = wait.fluentWait().until(
                    ExpectedConditions.elementToBeClickable(element));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior:'auto', block:'center', inline:'nearest'});",
                    clickableElement);
            clickableElement.click();
            LogsManager.info("Clicked WebElement successfully:", element.toString());
        } catch (ElementClickInterceptedException e) {
            LogsManager.error("Click on WebElement was intercepted:", element.toString(),
                    "| Reason:", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            LogsManager.error("Unable to click WebElement:", element.toString(),
                    "| Reason:", e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
            // Do not swallow WebDriver failures; the test must fail with the real cause.
            throw e;
        }
    }


    //Function that override fineElement function
    public WebElement find(By locator) {
        return driver.findElement(locator);
    }

    public boolean isDisplayed(By locator) {
        try {
            wait.fluentWait().until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );
            LogsManager.debug("Element became visible:", locator.toString());
            return true;
        } catch (TimeoutException e) {
            LogsManager.debug("Element did not become visible before timeout:", locator.toString());
            return false;
        }
    }

    public boolean areAllElementsDisplayed(By locator) {
        try {
            wait.fluentWait().until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)
            );
            LogsManager.debug("All matching elements became visible:", locator.toString());
            return true;
        } catch (TimeoutException e) {
            LogsManager.debug("Not all matching elements became visible before timeout:", locator.toString());
            return false;
        }
    }

    /*
     *Function to scroll to the Element using JS
     * @param locator of the Element
     */
    public void scrollToElementJS(By locator) {
        WebElement element = driver.findElement(locator);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior:'auto', block:'center', inline:'center'});",
                element
        );
        LogsManager.debug("Scrolled target element into the center of the viewport:", locator.toString());
    }

    public void scrollToElementStable(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "arguments[0].scrollIntoView({behavior:'instant', block:'center', inline:'nearest'});", element);

        // ننتظر حتى يثبت scrollY في قراءتين متتاليتين
        final double[] last = {-1};
        wait.fluentWait().until(d -> {
            double y = ((Number) js.executeScript("return window.scrollY;")).doubleValue();
            boolean stable = (y == last[0]);
            last[0] = y;
            return stable;
        });
        LogsManager.debug("Element scroll position stabilized.");
    }

    public boolean isElementCovered(WebElement element) {
        boolean covered = (Boolean) ((JavascriptExecutor) driver).executeScript("""
                const rect = arguments[0].getBoundingClientRect();
                const x = rect.left + rect.width / 2;
                const y = rect.top + rect.height / 2;
                const topElement = document.elementFromPoint(x, y);
                
                return topElement == null
                    || !(topElement == arguments[0]
                    || arguments[0].contains(topElement));
                """, element);
        if (covered) {
            LogsManager.debug("Element center point is currently covered by another element:", element.toString());
        }
        return covered;
    }


    public void hoverUntilVisible(WebElement container, By childLocator) {
        LogsManager.info("Hovering over product/container until child becomes visible:", childLocator.toString());
        wait.fluentWait().until(d -> {
            scrollToElementStable(container);
            if (isElementCovered(container)) {
                return false; // انتظر حتى يزول العنصر الذي يغطيه
            }
            new Actions(driver)
                    .moveToElement(container, -10, -10)
                    .moveToElement(container)
                    .perform();
            return container.findElement(childLocator).isDisplayed();
        });
        LogsManager.info("Hover action completed; target child is visible:", childLocator.toString());
    }

    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    public void uploadFile(By locator, String fileName) {
        File file = new File(System.getProperty("user.dir"), fileName);
        if (!file.isFile()) {
            LogsManager.error("File upload aborted because the requested file does not exist:",
                    file.getAbsolutePath());
            throw new IllegalArgumentException("Upload file does not exist: " + file.getAbsolutePath());
        }

        LogsManager.info("Starting file upload. File:", file.getAbsolutePath(),
                "| File input locator:", locator.toString());
        try {
            WebElement fileInput = wait.fluentWait().until(
                    ExpectedConditions.presenceOfElementLocated(locator));
            scrollToElementJS(locator);
            fileInput.sendKeys(file.getAbsolutePath());
            LogsManager.info("File path was sent to the upload input successfully:",
                    file.getAbsolutePath(), "| Locator:", locator.toString());
        } catch (RuntimeException e) {
            LogsManager.error("File upload failed:", file.getAbsolutePath(),
                    "| Locator:", locator.toString(), "| Reason:",
                    e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
            throw e;
        }
    }

    public WebElement findElement(WebElement parent, By childLocator) {
        try {
            WebElement element = parent.findElement(childLocator);
            LogsManager.debug("Located child element inside parent container:", childLocator.toString());
            return element;
        } catch (NoSuchElementException e) {
            LogsManager.error("Unable to locate child element inside parent container:",
                    childLocator.toString(), "| Reason:", e.getMessage());
            throw e;
        }
    }

    public void selectByVisibleText(By locator, String text) {
        try {
            new Select(driver.findElement(locator)).selectByVisibleText(text);
            LogsManager.info("Selected dropdown option by visible text:", text,
                    "| Dropdown:", locator.toString());
        } catch (RuntimeException e) {
            LogsManager.error("Unable to select dropdown option by visible text:", text,
                    "| Dropdown:", locator.toString(), "| Reason:", e.getMessage());
            throw e;
        }
    }

    public void selectByValue(By locator, String value) {
        try {
            new Select(driver.findElement(locator)).selectByValue(value);
            LogsManager.info("Selected dropdown option by value:", value,
                    "| Dropdown:", locator.toString());
        } catch (RuntimeException e) {
            LogsManager.error("Unable to select dropdown option by value:", value,
                    "| Dropdown:", locator.toString(), "| Reason:", e.getMessage());
            throw e;
        }
    }

    public void selectByIndex(By locator, int index) {
        try {
            new Select(driver.findElement(locator)).selectByIndex(index);
            LogsManager.info("Selected dropdown option at index:", String.valueOf(index),
                    "| Dropdown:", locator.toString());
        } catch (RuntimeException e) {
            LogsManager.error("Unable to select dropdown option at index:", String.valueOf(index),
                    "| Dropdown:", locator.toString(), "| Reason:", e.getMessage());
            throw e;
        }
    }

    public void setCheckbox(By locator, boolean shouldBeChecked) {
        try {
            WebElement checkbox = driver.findElement(locator);
            if (checkbox.isSelected() != shouldBeChecked) {
                checkbox.click();
            }
            LogsManager.info("Checkbox state verified:", String.valueOf(shouldBeChecked),
                    "| Locator:", locator.toString());
        } catch (RuntimeException e) {
            LogsManager.error("Unable to set checkbox state:", String.valueOf(shouldBeChecked),
                    "| Locator:", locator.toString(), "| Reason:", e.getMessage());
            throw e;
        }
    }

    public void waitUntilInvisible(WebElement element) {
        wait.fluentWait().until(
                ExpectedConditions.invisibilityOf(element)
        );
        LogsManager.info("Element is no longer visible:", element.toString());
    }

    public void hoverOver(WebElement element) {
        new Actions(driver)
                .moveToElement(element)
                .perform();

        LogsManager.info("Moved the mouse pointer over element:", element.toString());
    }

    public WebElement waitUntilClickable(WebElement element) {
        WebElement clickable = wait.fluentWait().until(ExpectedConditions.elementToBeClickable(element));
        LogsManager.debug("WebElement is clickable:", element.toString());
        return clickable;
    }

    public WebElement waitUntilVisible(By locator) {
        WebElement visible = wait.fluentWait().until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        LogsManager.debug("Element is visible:", locator.toString());
        return visible;
    }

    public String waitAndGetTextContent(By locator) {
        String text = wait.fluentWait().until(d -> {
            List<WebElement> elements = d.findElements(locator);
            if (elements.isEmpty()) {
                return null;
            }
            String content = elements.get(0).getAttribute("textContent");
            return (content != null && !content.isBlank()) ? content.trim() : null;
        });
        LogsManager.debug("Retrieved non-empty textContent from element:", locator.toString(),
                "| Text:", text);
        return text;
    }

    public WebElement waitUntilVisible(WebElement element) {
        WebElement visibleElement = wait.fluentWait().until(
                ExpectedConditions.visibilityOf(element)
        );
        LogsManager.debug("WebElement became visible:", element.toString());
        return visibleElement;
    }

    public void scrollTBottomOfPage() {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight);");
        LogsManager.info("Scrolled to the bottom of the current page.");
    }

    public void diagnoseClickTarget(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        Map<String, Object> result =
                (Map<String, Object>) js.executeScript(
                        """
                                const r = arguments[0].getBoundingClientRect();
                                const x = r.left + r.width / 2;
                                const y = r.top + r.height / 2;
                                const top = document.elementFromPoint(x, y);
                                
                                return {
                                    targetTag: arguments[0].tagName,
                                    targetClass: arguments[0].className,
                                    displayed: arguments[0].getBoundingClientRect().width > 0,
                                    topTag: top ? top.tagName : null,
                                    topId: top ? top.id : null,
                                    topClass: top ? top.className : null,
                                    topHtml: top ? top.outerHTML : null
                                };
                                """,
                        element
                );

        result.forEach((key, value) ->
                LogsManager.debug("Click target diagnostic:", key, "=", String.valueOf(value))
        );
    }

    public void scrollUntilProductsLoad() {
        LogsManager.info("Scrolling through the page until its document height stabilizes and products finish loading.");
        JavascriptExecutor js = (JavascriptExecutor) driver;

        long previousHeight = 0;
        int stableCount = 0;

        while (stableCount < 3) {
            js.executeScript(
                    "window.scrollTo(0, document.body.scrollHeight)"
            );

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

            long currentHeight = ((Number) js.executeScript(
                    "return document.body.scrollHeight"
            )).longValue();

            if (currentHeight == previousHeight) {
                stableCount++;
            } else {
                stableCount = 0;
            }

            previousHeight = currentHeight;
        }

        js.executeScript("window.scrollTo(0, 0)");
        LogsManager.info("Product loading scroll completed; browser returned to the top of the page.");
    }

    public void scrollUntilProductsLoad2() {
        LogsManager.info("Scrolling incrementally to the bottom of the page while waiting for lazy-loaded products.");
        JavascriptExecutor js = (JavascriptExecutor) driver;

        long previousHeight = -1;
        int stableCount = 0;

        while (stableCount < 3) {
            js.executeScript("window.scrollBy(0, 600)");

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

            long currentHeight = ((Number) js.executeScript(
                    "return document.documentElement.scrollHeight"
            )).longValue();

            if (currentHeight == previousHeight
                    && ((Number) js.executeScript(
                    "return window.scrollY + window.innerHeight"
            )).longValue() >= currentHeight - 5) {
                stableCount++;
            } else {
                stableCount = 0;
            }

            previousHeight = currentHeight;
        }
        LogsManager.info("Incremental product loading scroll completed.");
    }

    public void scrollToTop() {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, 0)");
        LogsManager.info("Scrolled to the top of the current page.");
    }


    public String waitForPaymentSuccessMessage(By locator) {
        return wait.fluentWait()
                .pollingEvery(Duration.ofMillis(100))
                .until(d -> {
                    List<WebElement> elements =
                            d.findElements(locator);

                    for (WebElement element : elements) {
                        if (element.isDisplayed()) {
                            String text = element.getText().trim();

                            if (!text.isEmpty()) {
                                return text;
                            }
                        }
                    }
                    return null;
                });
    }

    /********* Start mentoring *********************************************************/
    public void startTransientMessageObserver(By locator) {
        String selector = ((By.ByCssSelector) locator).toString()
                .replace("By.cssSelector: ", "");

        ((JavascriptExecutor) driver).executeScript("""
                const selector = arguments[0];
                const storageKey = "paymentSuccessMessage";
                const expectedText =
                    "Your order has been placed successfully!";
                
                sessionStorage.removeItem(storageKey);
                
                const checkMessage = () => {
                    const element = document.querySelector(selector);
                
                    if (!element || element.getClientRects().length === 0) {
                        return;
                    }
                
                    const text = element.textContent.trim();
                
                    if (text.includes(expectedText)) {
                        sessionStorage.setItem(storageKey, text);
                        observer.disconnect();
                    }
                };
                
                const observer = new MutationObserver(checkMessage);
                
                observer.observe(document.documentElement, {
                    subtree: true,
                    childList: true,
                    attributes: true,
                    characterData: true
                });
                
                checkMessage();
                """, selector);
    }

    /***********************Wait For Transient Message ********************************************************/

    public String getCapturedPaymentMessage() {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("""
                        return sessionStorage.getItem(
                            "paymentSuccessMessage"
                        );
                        """);
    }

    /*******************************************/
    public String captureTransientMessage(By locator) {
        String selector = ((By.ByCssSelector) locator).toString()
                .replace("By.cssSelector: ", "");

        JavascriptExecutor js = (JavascriptExecutor) driver;

        return (String) js.executeAsyncScript("""
                const selector = arguments[0];
                const callback = arguments[arguments.length - 1];
                const expectedText = "Your order has been placed successfully!";
                let finished = false;
                
                const finish = (text) => {
                    if (finished) return;
                    finished = true;
                    observer.disconnect();
                    clearTimeout(timer);
                    callback(text);
                };
                
                const checkMessage = () => {
                    const element = document.querySelector(selector);
                
                    if (element &&
                        element.offsetParent !== null &&
                        element.textContent.trim().includes(expectedText)) {
                        finish(element.textContent.trim());
                    }
                };
                
                const observer = new MutationObserver(checkMessage);
                
                observer.observe(document.documentElement, {
                    subtree: true,
                    childList: true,
                    attributes: true,
                    characterData: true
                });
                
                const timer = setTimeout(() => finish(null), 10000);
                
                checkMessage();
                """, selector);
    }

    public void acceptAlert() {
        String alertText = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();
        LogsManager.info("Accepted browser alert. Alert text:", alertText);
    }
}

