package com.AutomationExercise.drivers;

import com.AutomationExercise.utils.data.reader.PropertyReader;
import org.openqa.selenium.WebDriver;

import java.net.URISyntaxException;

public abstract class AbstractDriver {
    public abstract WebDriver createDriver() throws URISyntaxException;
    protected String host = PropertyReader.getProperty("remoteHost");
    protected String port = PropertyReader.getProperty("remotePort");
}
