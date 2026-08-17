package sw.autotest.generator.generatePlugin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static sw.autotest.generator.generatePlugin.utils.ResourceUtils.getResourceAsText;

public class CreateClassForStarterSpring {

    public void createClassForStarterSpring(String pathToProject) {

        createDirHelpers(pathToProject);
        createDirApp(pathToProject);
        createDirConfig(pathToProject);
        createDirModel(pathToProject);

        app(pathToProject);
        appConfig(pathToProject);
        testConfig(pathToProject);
        url(pathToProject);

        application(pathToProject);
        applicationDevelop(pathToProject);
        applicationTest(pathToProject);
    }

    private void createDirHelpers(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/main/java/helpers"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void createDirApp(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/main/java/app"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void createDirConfig(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/main/java/app/config"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void createDirModel(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/main/java/app/config/model"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void app(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/main/java/app/App.java";

        createFileIfNotExists(
                fileName,
                "/appClassForStarter/App"
        );
    }

    private void appConfig(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/main/java/app/config/AppConfig.java";

        createFileIfNotExists(
                fileName,
                "/appClassForStarter/AppConfig"
        );
    }

    private void testConfig(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/main/java/app/config/TestConfig.java";

        createFileIfNotExists(
                fileName,
                "/appClassForStarter/TestConfig"
        );
    }

    private void url(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/main/java/app/config/model/Url.java";

        createFileIfNotExists(
                fileName,
                "/appClassForStarter/UrlModel"
        );
    }

    private void application(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/main/resources/application.yaml";

        createFileIfNotExists(
                fileName,
                "/appClassForStarter/application"
        );
    }

    private void applicationDevelop(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/main/resources/application-develop.yaml";

        createFileIfNotExists(
                fileName,
                "/appClassForStarter/application-develop"
        );
    }

    private void applicationTest(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/main/resources/application-test.yaml";

        createFileIfNotExists(
                fileName,
                "/appClassForStarter/application-test"
        );
    }

    private void createFileIfNotExists(
            String fileName,
            String resourcePath
    ) {

        try {

            File file = new File(fileName);

            if (!file.exists()) {

                String content = getResourceAsText(getClass(), resourcePath);

                if (content == null) {
                    throw new RuntimeException(
                            "Resource not found: "
                                    + resourcePath
                    );
                }

                File parent = file.getParentFile();

                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                file.createNewFile();

                Files.writeString(
                        file.toPath(),
                        content,
                        StandardCharsets.UTF_8
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed create file: " + fileName, e);
        }
    }
}
