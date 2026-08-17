package sw.autotest.generator.generatePlugin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static sw.autotest.generator.generatePlugin.utils.ResourceUtils.getResourceAsText;

public class CreateTemplates {

    public void createTemplates(String pathToProject) {
        createDirTemplates(pathToProject);
        createDirLibraries(pathToProject);
        createDirRestAssured(pathToProject);
        apiTemplate(pathToProject);
        modelTemplate(pathToProject);
        pojoTemplate(pathToProject);
    }

    private void createDirTemplates(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/main/resources/templates"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void createDirLibraries(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/main/resources/templates/libraries"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void createDirRestAssured(String pathToProject) {

        File dir = new File(
                pathToProject
                        + "/src/main/resources/templates/libraries/rest-assured"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void apiTemplate(String pathToProject) {

        createFileIfMissing(
                pathToProject
                        + "/src/main/resources/templates/libraries/rest-assured/api.mustache",
                "/templates/api.mustache"
        );
    }

    private void modelTemplate(String pathToProject) {

        createFileIfMissing(
                pathToProject
                        + "/src/main/resources/templates/model.mustache",
                "/templates/model.mustache"
        );
    }

    private void pojoTemplate(String pathToProject) {

        createFileIfMissing(
                pathToProject
                        + "/src/main/resources/templates/pojo.mustache",
                "/templates/pojo.mustache"
        );
    }

    private void createFileIfMissing(
            String filePath,
            String resourcePath
    ) {

        try {

            File file = new File(filePath);

            if (file.exists()) {
                return;
            }

            file.createNewFile();

            String content = getResourceAsText(CreateTemplates.class, resourcePath);

            Files.writeString(
                    file.toPath(),
                    content,
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}