package com.AutomationExercise.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/** Utility methods for project files and generated output directories. */
public class FileUtils {
    private static final Path USER_DIR = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();

    public static Path resolvePath(String path) {
        Path candidate = Paths.get(path);
        return (candidate.isAbsolute() ? candidate : USER_DIR.resolve(candidate)).normalize();
    }

    public static void createDir(String path) {
        try {
            Files.createDirectories(resolvePath(path));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create directory: " + path, e);
        }
    }

    public static void renameFile(String oldPath, String newPath) {
        Path source = resolvePath(oldPath);
        Path destination = resolvePath(newPath);
        try {
            if (destination.getParent() != null) {
                Files.createDirectories(destination.getParent());
            }
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to rename " + source + " to " + destination, e);
        }
    }

    /** Clears the contents but keeps the directory itself available for the next execution. */
    public static void cleanDir(File directory) {
        try {
            Path path = directory.toPath().toAbsolutePath().normalize();
            if (Files.exists(path) && !Files.isDirectory(path)) {
                Files.delete(path);
            }
            Files.createDirectories(path);
            try (var entries = Files.list(path)) {
                for (Path entry : entries.toList()) {
                    org.apache.commons.io.FileUtils.deleteQuietly(entry.toFile());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to clean directory: " + directory, e);
        }
    }
}
