package com.AutomationExercise.utils.report;

import com.AutomationExercise.utils.file.FileUtils;
import com.AutomationExercise.utils.logs.LogsManager;

/** Backward-compatible helper for clearing Allure's current result directory. */
public class AllureUtils {
    public static void clearAllureResources() {
        LogsManager.info("Clearing current raw Allure result files:", AllureConstants.RESULTS_FOLDER.toString());
        FileUtils.cleanDir(AllureConstants.RESULTS_FOLDER.toFile());
    }
}
