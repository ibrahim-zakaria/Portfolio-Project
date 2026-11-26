package com.AutomationExercise.drivers;

import com.AutomationExercise.utils.data.reader.PropertyReader;
import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Creates exactly one Chrome session per invocation. */
public class ChromeFactory extends AbstractDriver {

    private ChromeOptions buildOptions(String executionType) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-infobars");
        options.setAcceptInsecureCerts(true);
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        if (executionType.equalsIgnoreCase("localHeadless")
                || executionType.equalsIgnoreCase("remote")) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    @Override
    public WebDriver createDriver() {
        String executionType = System.getProperty("executionType");
        if (executionType == null || executionType.isBlank()) {
            executionType = PropertyReader.getProperty("executionType");
        }
        if (executionType == null || executionType.isBlank()) {
            executionType = "Local";
        }
        ChromeOptions options = buildOptions(executionType);

        if (executionType.equalsIgnoreCase("local")
                || executionType.equalsIgnoreCase("localHeadless")) {
            LogsManager.info("Creating local ChromeDriver session. Headless:",
                    String.valueOf(executionType.equalsIgnoreCase("localHeadless")));
            ChromeDriver chromeDriver = new ChromeDriver(options);
            configureLocalNetworkRules(chromeDriver);
            return chromeDriver;
        }

        if (executionType.equalsIgnoreCase("remote")) {
            try {
                LogsManager.info("Creating remote Chrome session at:", "http://" + host + ":" + port + "/wd/hub");
                return new RemoteWebDriver(
                        new URI("http://" + host + ":" + port + "/wd/hub").toURL(), options);
            } catch (Exception e) {
                LogsManager.error("Unable to create remote Chrome session:",
                        e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
                throw new IllegalStateException("Remote Chrome session creation failed.", e);
            }
        }

        throw new IllegalArgumentException("Unsupported executionType: " + executionType
                + ". Supported values are Local, LocalHeadless, and Remote.");
    }

    /** Applies CDP network rules after the one and only local ChromeDriver has been created. */
    private void configureLocalNetworkRules(ChromeDriver driver) {
        try {
            driver.executeCdpCommand("Network.enable", new HashMap<>());
            driver.executeCdpCommand("Network.setBlockedURLs", Map.of("urls", List.of(
                    "*googlesyndication.com*",
                    "*doubleclick.net*",
                    "*googleadservices.com*",
                    "*google-analytics.com*")));
            LogsManager.info("Chrome DevTools network filtering configured successfully.");
        } catch (Exception e) {
            // Network filtering is an optional optimization; it must not discard a healthy browser session.
            LogsManager.warn("Chrome session started, but DevTools network filtering could not be configured:",
                    e.getMessage());
        }
    }
}
