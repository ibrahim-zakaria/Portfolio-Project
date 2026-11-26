package com.AutomationExercise.drivers;

import com.AutomationExercise.utils.data.reader.PropertyReader;
import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class EdgeFactory extends AbstractDriver {

    private EdgeOptions buildOptions(String executionType) {
        EdgeOptions options = new EdgeOptions();
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
        EdgeOptions options = buildOptions(executionType);

        if (executionType.equalsIgnoreCase("local")
                || executionType.equalsIgnoreCase("localHeadless")) {
            LogsManager.info("Creating local EdgeDriver session. Headless:",
                    String.valueOf(executionType.equalsIgnoreCase("localHeadless")));
            EdgeDriver driver = new EdgeDriver(options);
            configureLocalNetworkRules(driver);
            return driver;
        }

        if (executionType.equalsIgnoreCase("remote")) {
            try {
                LogsManager.info("Creating remote Edge session at:", "http://" + host + ":" + port + "/wd/hub");
                return new RemoteWebDriver(
                        new URI("http://" + host + ":" + port + "/wd/hub").toURL(), options);
            } catch (Exception e) {
                LogsManager.error("Unable to create remote Edge session:",
                        e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
                throw new IllegalStateException("Remote Edge session creation failed.", e);
            }
        }

        throw new IllegalArgumentException("Unsupported executionType: " + executionType
                + ". Supported values are Local, LocalHeadless, and Remote.");
    }

    private void configureLocalNetworkRules(EdgeDriver driver) {
        try {
            driver.executeCdpCommand("Network.enable", Map.of());
            driver.executeCdpCommand("Network.setBlockedURLs", Map.of("urls", List.of(
                    "*doubleclick.net/*",
                    "*googlesyndication.com/*",
                    "*googleadservices.com/*",
                    "*google.com/pagead/*")));
            LogsManager.info("Edge DevTools network filtering configured successfully.");
        } catch (Exception e) {
            LogsManager.warn("Edge session started, but DevTools network filtering could not be configured:",
                    e.getMessage());
        }
    }
}
