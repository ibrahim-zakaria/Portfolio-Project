package com.AutomationExercise.utils.system;

import com.AutomationExercise.utils.data.reader.PropertyReader;

public class OSUtils {
    public enum OSType {
        WINDOWS,
        LINUX,
        MAC,
        OTHER
    }
    public static OSType getCurrentOS() {
        String os = PropertyReader.getProperty("os.name").toLowerCase();
        if(os.contains("win")) return OSType.WINDOWS;
        else if(os.contains("mac")) return OSType.MAC;
        else if(os.contains("linux") || os.contains("nux")) return OSType.LINUX;
        else return OSType.OTHER;
    }
}
