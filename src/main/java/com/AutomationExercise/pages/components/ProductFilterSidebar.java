package com.AutomationExercise.pages.components;

import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.drivers.GUIDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ProductFilterSidebar {
    private GUIDriver driver;

    public ProductFilterSidebar(GUIDriver driver) {
        this.driver = driver;
    }

    //Women Category section
    private final By womenCatLoc = By.cssSelector("a[href='#Women'] span i");
    private final By womenDressCatLoc = By.xpath("//div[@id='Women']//a[normalize-space()='Dress']");
    private final By womenTopsCatLoc = By.xpath("//div[@id='Women']//a[normalize-space()='Tops']");
    private final By womenSareeCatLoc = By.xpath("//div[@id='Women']//a[normalize-space()='Saree']");
    //Men Category section
    private final By menCatLoc = By.cssSelector("a[href='#Men']");
    private final By menDressCatLoc = By.xpath("//div[@id='Men']//a[normalize-space()='Tshirts']");
    private final By menTopsCatLoc = By.xpath("//div[@id='Men']//a[normalize-space()='Jeans']");
    //Kids Category section
    private final By kidsCatLoc = By.cssSelector("a[href='#Kids']");
    private final By kidsDressCatLoc = By.xpath("//div[@id='Kids']//a[normalize-space()='Dress']");
    private final By kidsTopsAndShirtsCatLoc = By.xpath("//div[@id='Kids']//a[normalize-space()='Tops & Shirts']");
    //All brands Locators section
    private final By poloBrandLocator = By.cssSelector("a[href='/brand_products/Polo']");
    private final By HAndMBrandLocator = By.cssSelector("a[href='/brand_products/H&M']");
    private final By madameBrandLocator = By.cssSelector("a[href='/brand_products/Madame']");
    private final By mastAndHarbourBrandLocator = By.cssSelector("a[href='/brand_products/Mast & Harbour']");
    private final By babyHugBrandLocator = By.cssSelector("a[href='/brand_products/Babyhug']");
    private final By allenSollyBrandLocator = By.cssSelector("a[href='/brand_products/Allen Solly Junior']");
    private final By KookieBrandLocator = By.cssSelector("a[href='/brand_products/Kookie Kids']");
    private final By bibabibaBrandLocator = By.cssSelector("a[href='/brand_products/BibaBiba']");
    //heading Locator
    private final By headingLocator = By.cssSelector("h2.title ");


    /**
     * WOMEN_DRESS("Women - Dress Products"),
     * WOMEN_TOPS("Women - Tops Products"),
     * WOMEN_SAREE("Women - Saree Products"),
     * MEN_TSHIRTS("Men - Tshirts Products"),
     * MEN_JEANS("Men - Jeans Products"),
     * KIDS_DRESS("Kids - Dress Products"),
     * KIDS_TOPS_SHIRTS("Kids - Tops & Shirts Products"),
     * BRAND_POLO("Brand - Polo Products"),
     * BRAND_HM("Brand - H&M Products"),
     * BRAND_MADAME("Brand - Madame Products"),
     * BRAND_MAST_HARBOUR("Brand - Mast & Harbour Products"),
     * BRAND_BABYHUG("Brand - Babyhug Products"),
     * BRAND_ALLEN_SOLLY("Brand - Allen Solly Junior Products"),
     * BRAND_KOOKIE_KIDS("Brand - Kookie Kids Products"),
     * BRAND_BIBA("Brand - Biba Products");
     */



    @Step("Click to expand Women Category")
    public ProductFilterSidebar clickOnWomenCat() {
        LogsManager.info("Page step started: Click to expand Women Category");
        driver.elementActions().click(womenCatLoc);
        return this;
    }
    @Step("Click on Dress Products in [Women>Dress]")
    public ProductFilterSidebar clickOnDressCat() {
        LogsManager.info("Page step started: Click on Dress Products in [Women>Dress]");
        driver.elementActions().click(womenDressCatLoc);
        return this;
    }
    @Step("Click on Tops Products in [Women>Tops]")
    public ProductFilterSidebar clickOnTopsCat() {
        LogsManager.info("Page step started: Click on Tops Products in [Women>Tops]");
        driver.elementActions().click(womenTopsCatLoc);
        return this;
    }
    @Step("Click on Saree in [Women>Saree]")
    public ProductFilterSidebar clickOnSareeCat() {
        LogsManager.info("Page step started: Click on Saree in [Women>Saree]");
        driver.elementActions().click(womenSareeCatLoc);
        return this;
    }

    @Step("Click on Polo Brand")
    public ProductFilterSidebar clickOnPoloBrand() {
        LogsManager.info("Page step started: Click on Polo Brand");
        driver.elementActions().click(poloBrandLocator);
        return this;
    }

    @Step("Verify category or brand heading")
    public ProductFilterSidebar verifyHeading(CategoryHeading expectedHeading) {
        LogsManager.info("Page step started: Verify category or brand heading");
        String actualHeading = driver.elementActions().getText(headingLocator)
                .replaceAll("\\s+", " ")
                .trim();
        LogsManager.info("Comparing category/brand heading. Expected:", expectedHeading.getText(),
                "| Actual:", actualHeading);
        driver.verification().assertTrue(actualHeading.contains(expectedHeading.getText())
                ,"Unexpected heading"
        );
        return this;
    }

    @Step("Verify brands are visible in the left sidebar")
    public ProductFilterSidebar verifyCategoriesAreVisible(){
        LogsManager.info("Page step started: Verify brands are visible in the left sidebar");
        driver.verification().assertTrue(
                driver.elementActions().areAllElementsDisplayed(By.cssSelector("#accordian div.panel"))
                ,"Categories are not visible in the left sidebar"
        );
        return this;
    }
    @Step("Verify brands are visible in the left sidebar")
    public ProductFilterSidebar verifyBrandsAreVisible(){
        LogsManager.info("Page step started: Verify brands are visible in the left sidebar");
        driver.verification().assertTrue(
                driver.elementActions().areAllElementsDisplayed(By.cssSelector(".brands-name ul li a span"))
                ,"Brands are not visible in the left sidebar"
        );
        return this;
    }
    @Step("Verify filtered products are displayed")
    public ProductFilterSidebar verifyFilteredProductsAreDisplayed(){
        LogsManager.info("Page step started: Verify filtered products are displayed");
        driver.verification().assertTrue(
        driver.elementActions().
                areAllElementsDisplayed(
                        By.cssSelector(".features_items .product-image-wrapper"))
                ,"Brand products are not displayed after filtering"
        );
        return this;
    }




}
