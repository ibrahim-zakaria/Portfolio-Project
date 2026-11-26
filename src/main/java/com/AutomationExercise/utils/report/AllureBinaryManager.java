package com.AutomationExercise.utils.report;

import com.AutomationExercise.utils.system.OSUtils;
import com.AutomationExercise.utils.system.TerminalUtils;
import com.AutomationExercise.utils.logs.LogsManager;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.StandardCopyOption;

public class AllureBinaryManager {
        private static final String STATIC_VERSION = "2.34.1";
  /*  private static class LazyHolder{
       static final String VERSION = resolveVersion();
        private static String resolveVersion(){
            try{
                String url = Jsoup.connect("https://github.com/allure-framework/allure2/releases/latest").followRedirects(true).execute().url().toString();
                return url.split("/tag/")[1];
            } catch (Exception e) {
                LogsManager.error("Failed to resolve Allure version", e.getMessage());
                throw new IllegalStateException("Can't resolve Allure version", e);
            }
        }
    }*/

    private static Path downloadZip (String version) {
        try {
            String url = AllureConstants.ALLURE_ZIP_BASE_URL + version + "/allure-commandline-" + version + ".zip";
            LogsManager.info("Preparing Allure CLI download. Version:", version, "| Source URL:", url);
            Path zipFile = Paths.get(AllureConstants.EXTRACTION_DIR.toString(), "allure-" + version + ".zip");
            if (!Files.exists(zipFile)) {
                Files.createDirectories(AllureConstants.EXTRACTION_DIR);
                // Download to a temp file first so an interrupted download never leaves a corrupt zip behind.
                Path partFile = zipFile.resolveSibling(zipFile.getFileName() + ".part");
                try (BufferedInputStream in = new BufferedInputStream(new URI(url).toURL().openStream());
                     OutputStream out = Files.newOutputStream(partFile)) {
                    in.transferTo(out);
                } catch (Exception e) {
                    Files.deleteIfExists(partFile);
                    LogsManager.error("Failed to download Allure CLI from:", url, "| Reason:", e.getMessage());
                    return zipFile;
                }
                Files.move(partFile, zipFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return zipFile;
        } catch (Exception e) {
            LogsManager.error("Error downloading Allure zip file", e.getMessage());
            return Paths.get("");
        }

    }



//Extract ZIP file for Allure
    private static void extractZip(Path zipPath) {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path extractionRoot = AllureConstants.EXTRACTION_DIR.toAbsolutePath().normalize();
                Path filePath = extractionRoot.resolve(entry.getName()).normalize();
                if (!filePath.startsWith(extractionRoot)) {
                    throw new IllegalStateException("Unsafe path in Allure archive: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zipInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Exception e) {
            LogsManager.error("Error extracting Allure zip file", e.getMessage());
        }

    }

    public static void downloadAndExtract() {
        try {
            //String version = LazyHolder.VERSION;
            String version = STATIC_VERSION;
            Path extractionDir = Paths.get(AllureConstants.EXTRACTION_DIR.toString(), "allure-" + version);
            // Check the executable itself, not just the folder: a half-extracted folder must not count as installed.
            if (Files.isRegularFile(getExecutable())) {
                LogsManager.info("Allure binaries already exist:", getExecutable().toString());
                return;
            }
            if (Files.exists(extractionDir)) {
                org.apache.commons.io.FileUtils.deleteQuietly(extractionDir.toFile());
            }

            Path zipPath = downloadZip(version);
            if (!Files.isRegularFile(zipPath)) {
                LogsManager.error("Allure CLI archive is missing; reports cannot be generated. Check network access.");
                return;
            }
            extractZip(zipPath);
            if (!Files.isRegularFile(getExecutable())) {
                LogsManager.error("Allure CLI was not extracted correctly. Expected:", getExecutable().toString());
                // Drop a possibly corrupt archive so the next run downloads it again.
                Files.deleteIfExists(zipPath);
                return;
            }
            LogsManager.info("Allure binaries downloaded and extracted.");

            // Give execute permissions to the binary if not on Windows
            if (!OSUtils.getCurrentOS().equals(OSUtils.OSType.WINDOWS)) {
                TerminalUtils.executeTerminalCommand("chmod", "u+x", getExecutable().toString());
            }

            // Clean up the downloaded archive after successful extraction.
            if (Files.exists(zipPath)) {
                Files.deleteIfExists(zipPath);
            }
            
            }catch (Exception e){
                LogsManager.error("Error downloading or extraction binaries", e.getMessage());
        }
    }



    public static Path getExecutable() {
        //String version = LazyHolder.VERSION;
        String version = STATIC_VERSION;
        Path binaryPath = Paths.get(AllureConstants.EXTRACTION_DIR.toString(), "allure-" + version, "bin", "allure");
        return OSUtils.getCurrentOS() == OSUtils.OSType.WINDOWS
                ? binaryPath.resolveSibling(binaryPath.getFileName() + ".bat")
                : binaryPath;
    }
}
