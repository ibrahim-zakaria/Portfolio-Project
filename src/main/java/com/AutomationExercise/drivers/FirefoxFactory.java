package com.AutomationExercise.drivers;

import com.AutomationExercise.utils.data.reader.PropertyReader;
import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;

public class FirefoxFactory extends AbstractDriver {

    private FirefoxOptions buildOptions(String executionType) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        if (executionType.equalsIgnoreCase("localHeadless")
                || executionType.equalsIgnoreCase("remote")) {
            options.addArguments("--headless");
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
        FirefoxOptions options = buildOptions(executionType);

        if (executionType.equalsIgnoreCase("local")
                || executionType.equalsIgnoreCase("localHeadless")) {
            LogsManager.info("Creating local FirefoxDriver session. Headless:",
                    String.valueOf(executionType.equalsIgnoreCase("localHeadless")));
            return new FirefoxDriver(options);
        }

        if (executionType.equalsIgnoreCase("remote")) {
            try {
                LogsManager.info("Creating remote Firefox session at:", "http://" + host + ":" + port + "/wd/hub");
                return new RemoteWebDriver(
                        new URI("http://" + host + ":" + port + "/wd/hub").toURL(), options);
            } catch (Exception e) {
                LogsManager.error("Unable to create remote Firefox session:",
                        e.getClass().getSimpleName(), "-", String.valueOf(e.getMessage()));
                throw new IllegalStateException("Remote Firefox session creation failed.", e);
            }
        }

        throw new IllegalArgumentException("Unsupported executionType: " + executionType
                + ". Supported values are Local, LocalHeadless, and Remote.");
    }
}
