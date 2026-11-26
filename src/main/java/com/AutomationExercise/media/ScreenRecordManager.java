package com.AutomationExercise.media;

import com.AutomationExercise.utils.data.reader.PropertyReader;
import com.AutomationExercise.utils.logs.LogsManager;
import com.AutomationExercise.utils.report.AllureConstants;

import java.io.File;
import java.nio.file.Path;

/**
 * Video-recording lifecycle hook. Recording remains disabled in video.properties by default.
 * The current project snapshot does not contain an active recorder implementation, so this
 * class reports the setting explicitly instead of pretending that a recording was produced.
 */
public final class ScreenRecordManager {

    public static final Path RECORDINGS_DIRECTORY = AllureConstants.RECORDINGS_FOLDER;
    public static final String RECORDINGS_PATH = RECORDINGS_DIRECTORY + File.separator;

    private ScreenRecordManager() {
    }

    public static void setRecording() {
        boolean enabled = Boolean.parseBoolean(PropertyReader.getProperty("recordTests"));
        if (enabled) {
            LogsManager.warn("Video recording is enabled in video.properties, but no recorder backend is currently connected.");
        } else {
            LogsManager.debug("Video recording is disabled. Set recordTests=true only after a recorder backend is configured.");
        }
    }

    public static void stopRecording(String testName) {
        LogsManager.debug("Video recording stop hook reached for test:", testName,
                "| No active recorder session is configured in this project snapshot.");
    }
}
