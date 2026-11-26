package com.AutomationExercise.utils.system;

import com.AutomationExercise.utils.logs.LogsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TerminalUtils {
    public static void executeTerminalCommand(String... commandParts) {
        if (commandParts == null || commandParts.length == 0) {
            throw new IllegalArgumentException("Command must not be empty");
        }

        String commandText = String.join(" ", Arrays.asList(commandParts));
        try {
            List<String> effectiveCommand = new ArrayList<>(Arrays.asList(commandParts));
            boolean isWindows = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
            if (isWindows && commandParts[0].toLowerCase(Locale.ROOT).endsWith(".bat")) {
                effectiveCommand.clear();
                effectiveCommand.add("cmd.exe");
                effectiveCommand.add("/c");
                effectiveCommand.addAll(Arrays.asList(commandParts));
            }
            Process process = new ProcessBuilder(effectiveCommand).redirectErrorStream(true).start();
            String output;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                output = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                LogsManager.error("Command failed with exit code:", String.valueOf(exitCode), "Command:", commandText,
                        "Output:", output);
                throw new IllegalStateException("Command failed with exit code " + exitCode + ": " + commandText);
            }
            if (!output.isBlank()) {
                LogsManager.debug("Command output:", output);
            }
        } catch (IOException e) {
            LogsManager.error("Failed to execute terminal command:", commandText, e.getMessage());
            throw new IllegalStateException("Failed to execute command: " + commandText, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogsManager.error("Terminal command was interrupted:", commandText, e.getMessage());
            throw new IllegalStateException("Command interrupted: " + commandText, e);
        }
    }
}
