package com.AutomationExercise.utils.report;

import com.AutomationExercise.utils.logs.LogsManager;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.nio.file.Files;

import static com.AutomationExercise.utils.data.reader.PropertyReader.getProperty;
import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;

public class AllureEnvironmentManager {
    public static void setupAllureEnvironmentManager() {
        try {
            Files.createDirectories(AllureConstants.RESULTS_FOLDER);
            allureEnvironmentWriter(
                    ImmutableMap.<String, String>builder()
                            .put("OS", getProperty("os.name"))
                            .put("Java version", getProperty("java.runtime.version"))
                            .put("Browser", getProperty("browserType"))
                            .put("Execution Type", getProperty("executionType"))
                            .put("URL", getProperty("baseUrl"))
                            .build(),
                    AllureConstants.RESULTS_FOLDER.toString() + File.separator
            );
            LogsManager.info("Allure environment variables set");
            AllureBinaryManager.downloadAndExtract();
        } catch (Exception e) {
            LogsManager.error("Unable to prepare Allure environment:", e.getMessage());
        }
    }
}
