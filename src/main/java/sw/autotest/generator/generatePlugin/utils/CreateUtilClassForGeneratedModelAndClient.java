package sw.autotest.generator.generatePlugin.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static sw.autotest.generator.generatePlugin.utils.ResourceUtils.getResourceAsText;

@Slf4j
public class CreateUtilClassForGeneratedModelAndClient {

    public static void createUtils(String projectPath) {
        log.info("Creating utils for " + projectPath);
        createDir(projectPath);

        createFile(
                projectPath,
                "RFC3339DateFormat.java",
                "/utilClassForGenerateModelAndClient/RFC3339DateFormat"
        );

        createFile(
                projectPath,
                "ResponseSpecBuilders.java",
                "/utilClassForGenerateModelAndClient/ResponseSpecBuilders"
        );

        createFile(
                projectPath,
                "JacksonObjectMapper.java",
                "/utilClassForGenerateModelAndClient/JacksonObjectMapper"
        );

        createFile(
                projectPath,
                "Oper.java",
                "/utilClassForGenerateModelAndClient/Oper"
        );

        createFile(
                projectPath,
                "AllureFilter.java",
                "/utilClassForGenerateModelAndClient/AllureFilter"
        );

        createFile(
                projectPath,
                "Clients.java",
                "/utilClassForGenerateModelAndClient/Clients"
        );
    }

    private static void createDir(String projectPath) {

        Path dir = Path.of(
                projectPath,
                "src/main/java/sw/generator/client/utils"
        );

        try {

            Files.createDirectories(dir);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    private static void createFile(
            String projectPath,
            String fileName,
            String resourcePath
    ) {

        try {

            Path file = Path.of(
                    projectPath,
                    "src/main/java/sw/generator/client/utils",
                    fileName
            );

            if (Files.exists(file)) {
                return;
            }
            log.info("resourcePath " + resourcePath);
            String content = getResourceAsText(CreateUtilClassForGeneratedModelAndClient.class, resourcePath);

            Files.writeString(file, content);
            log.info("Created util file: " + fileName);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}