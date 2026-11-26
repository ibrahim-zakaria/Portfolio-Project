package com.AutomationExercise.utils.data.reader;

import com.AutomationExercise.utils.logs.LogsManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

/** Loads framework properties once while allowing Maven/Jenkins system properties to override them. */
public final class PropertyReader {

    private static volatile Properties cachedProperties;

    private PropertyReader() {
    }

    public static Properties loadProperties() {
        Properties loaded = cachedProperties;
        if (loaded != null) {
            return copyOf(loaded);
        }

        synchronized (PropertyReader.class) {
            if (cachedProperties == null) {
                Properties properties = new Properties();
                try {
                    Collection<File> propertyFiles = FileUtils.listFiles(
                            new File("src/main/resources"), new String[]{"properties"}, true);

                    for (File file : propertyFiles) {
                        try (var inputStream = FileUtils.openInputStream(file)) {
                            properties.load(inputStream);
                            LogsManager.debug("Loaded framework properties from:", file.getPath());
                        } catch (Exception e) {
                            LogsManager.error("Unable to load properties file:", file.getPath(),
                                    "| Reason:", e.getMessage());
                            throw new IllegalStateException("Unable to load properties file: " + file, e);
                        }
                    }

                    // JVM arguments supplied by Maven/Jenkins take precedence over file values.
                    properties.putAll(System.getProperties());
                    System.getProperties().putAll(properties);
                    cachedProperties = properties;
                    LogsManager.info("Framework properties loaded successfully. Property count:",
                            String.valueOf(properties.size()));
                } catch (Exception e) {
                    LogsManager.error("Framework properties could not be loaded:", e.getMessage());
                    throw new IllegalStateException("Framework properties initialization failed.", e);
                }
            }
            return copyOf(cachedProperties);
        }
    }

    public static String getProperty(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Property key must not be null or blank.");
        }

        // Check the live JVM properties first so runtime/Maven overrides remain effective.
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }

        String value = loadProperties().getProperty(key);
        if (value == null || value.isBlank()) {
            LogsManager.warn("Framework property is missing or blank:", key);
            return null;
        }
        return value.trim();
    }

    private static Properties copyOf(Properties source) {
        Properties copy = new Properties();
        copy.putAll(source);
        return copy;
    }
}
