package com.AutomationExercise.utils.time;

public class TimeManager {
    public static String getTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HHmmss").format(new java.util.Date());
    }
    public static String getSimpleTimestamp() {
        return Long.toString(System.currentTimeMillis());
    }
}
