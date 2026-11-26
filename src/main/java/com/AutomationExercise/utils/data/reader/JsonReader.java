package com.AutomationExercise.utils.data.reader;

import com.AutomationExercise.utils.logs.LogsManager;
import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JsonReader {
    // Searched in order; paths are resolved against the project directory, not the IDE's working directory.
    private static final List<String> TEST_DATA_DIRECTORIES = List.of(
            "src/test/resources/test-data",
            "src/main/resources/test-data");
    private static final Path USER_DIR = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();

    String jsonContent;
    String jsonFileName;

    public JsonReader(String jsonFileName) {
        this.jsonFileName = jsonFileName;
        String fileName = jsonFileName.endsWith(".json") ? jsonFileName : jsonFileName + ".json";
        Path file = TEST_DATA_DIRECTORIES.stream()
                .map(dir -> USER_DIR.resolve(dir).resolve(fileName))
                .filter(Files::isRegularFile)
                .findFirst()
                .orElse(null);
        if (file == null) {
            LogsManager.error("JsonReader Error: file not found:", fileName,
                    "| Searched in:", TEST_DATA_DIRECTORIES.toString(), "| Base:", USER_DIR.toString());
            return;
        }
        try (FileReader reader = new FileReader(file.toFile())) {
            JSONObject data = (JSONObject) new JSONParser().parse(reader);
            jsonContent = data.toJSONString();
        } catch (Exception e) {
            LogsManager.error("JsonReader Error", file.toString(), e.getMessage());
        }
    }

    public String getJsonValue(String jsonPath) {
        try {
            return JsonPath.read(jsonContent, jsonPath);
        } catch (Exception e) {
            LogsManager.error("JsonReader Error", e.getMessage());
            return "{}";
        }
    }
}
