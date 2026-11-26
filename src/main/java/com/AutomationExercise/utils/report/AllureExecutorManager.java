package com.AutomationExercise.utils.report;

import com.AutomationExercise.utils.logs.LogsManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Creates Allure executor metadata for local runs and supported CI platforms. */
public final class AllureExecutorManager {
    private AllureExecutorManager() {
    }

    public static void setupExecutor() {
        try {
            Files.createDirectories(AllureConstants.RESULTS_FOLDER);
            Map<String, String> fields = detectExecutor();
            Path executorFile = AllureConstants.RESULTS_FOLDER.resolve("executor.json");
            Files.writeString(executorFile, toJson(fields), StandardCharsets.UTF_8);
            LogsManager.info("Allure executor metadata created:", executorFile.toString(),
                    "| Executor:", fields.getOrDefault("name", "Unknown"));
        } catch (IOException e) {
            LogsManager.error("Unable to create Allure executor metadata:", e.getMessage());
        }
    }

    private static Map<String, String> detectExecutor() {
        Map<String, String> fields = new LinkedHashMap<>();
        String customReportUrl = env("ALLURE_REPORT_URL");

        // GitHub Actions environment variables.
        if (isSet("GITHUB_ACTIONS")) {
            String repository = env("GITHUB_REPOSITORY");
            String server = envOrDefault("GITHUB_SERVER_URL", "https://github.com");
            String runId = env("GITHUB_RUN_ID");
            String runNumber = env("GITHUB_RUN_NUMBER");
            String workflow = envOrDefault("GITHUB_WORKFLOW", "Automation Tests");
            String runUrl = nonBlank(repository) && nonBlank(runId)
                    ? server + "/" + repository + "/actions/runs/" + runId : env("GITHUB_RUN_URL");

            fields.put("name", "GitHub Actions");
            fields.put("type", "GitHub Actions");
            fields.put("buildName", workflow + (nonBlank(runNumber) ? " #" + runNumber : ""));
            putIfPresent(fields, "buildUrl", runUrl);
            putIfPresent(fields, "reportUrl", customReportUrl);
            return fields;
        }

        // Jenkins environment variables.
        if (isSet("JENKINS_URL") || isSet("BUILD_URL")) {
            String job = envOrDefault("JOB_NAME", "Jenkins Job");
            String number = env("BUILD_NUMBER");
            fields.put("name", "Jenkins");
            fields.put("type", "Jenkins");
            fields.put("buildName", job + (nonBlank(number) ? " #" + number : ""));
            putIfPresent(fields, "buildUrl", env("BUILD_URL"));
            putIfPresent(fields, "reportUrl", customReportUrl);
            return fields;
        }

        // Local IDE, Maven CLI, or other local execution.
        String executionType = System.getProperty("executionType", "Local");
        String buildName = System.getProperty("taf.execution.id", "Local Run");
        fields.put("name", "Local Execution");
        fields.put("type", executionType.equalsIgnoreCase("Local") ? "Local" : executionType);
        fields.put("buildName", "AutomationExercise - " + buildName);
        fields.put("reportName", "AutomationExercise Test Report");
        putIfPresent(fields, "reportUrl", customReportUrl);
        return fields;
    }

    private static String toJson(Map<String, String> fields) {
        StringBuilder json = new StringBuilder("{\n");
        int index = 0;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            json.append("  \"").append(escape(entry.getKey())).append("\": \"")
                    .append(escape(entry.getValue())).append("\"");
            if (++index < fields.size()) {
                json.append(',');
            }
            json.append('\n');
        }
        return json.append('}').toString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private static String env(String key) {
        return System.getenv(key);
    }

    private static String envOrDefault(String key, String fallback) {
        String value = env(key);
        return nonBlank(value) ? value : fallback;
    }

    private static boolean isSet(String key) {
        return nonBlank(env(key));
    }

    private static boolean nonBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static void putIfPresent(Map<String, String> fields, String key, String value) {
        if (nonBlank(value)) {
            fields.put(key, value);
        }
    }
}
