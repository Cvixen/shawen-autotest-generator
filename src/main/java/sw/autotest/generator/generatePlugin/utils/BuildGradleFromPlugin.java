package sw.autotest.generator.generatePlugin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static sw.autotest.generator.generatePlugin.utils.ResourceUtils.getResourceAsText;

public class BuildGradleFromPlugin {

    public void buildGradle(String pathToProject) {

        try {

            String fileName =
                    pathToProject
                            + "/build.gradle.example";

            File file = new File(fileName);

            if (!file.exists()) {

                String buildGradle = getResourceAsText(getClass(), "/buildGradle/buildGradle");

                if (buildGradle == null) {
                    throw new RuntimeException(
                            "Resource not found: /buildGradle/buildGradle"
                    );
                }

                file.createNewFile();

                Files.writeString(
                        file.toPath(),
                        buildGradle,
                        StandardCharsets.UTF_8
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed create build.gradle.example",
                    e
            );
        }
    }
}
