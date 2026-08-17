package sw.autotest.generator.generatePlugin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static sw.autotest.generator.generatePlugin.utils.ResourceUtils.getResourceAsText;

public class CreateUtilClassForTest {

    public void createUtilsClassesForTest(String pathToProject) {

        createDirRu(pathToProject);
        createDirPost(pathToProject);
        createDirTestCases(pathToProject);
        createDirUtil(pathToProject);

        restAssuredHelper(pathToProject);
        testBase(pathToProject);
        testExample(pathToProject);

        gitlabCi();
        gitignore();

        allureProperties(pathToProject);
        allureGradle(pathToProject);
    }

    private void createDirRu(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/test/java",
                "ru"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void createDirPost(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/test/java/ru",
                "russianpost"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void createDirTestCases(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/test/java/ru/russianpost",
                "testcases"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void createDirUtil(String pathToProject) {

        File dir = new File(
                pathToProject + "/src/test/java/ru/russianpost",
                "utils"
        );

        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void restAssuredHelper(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/test/java/ru/russianpost/utils/RestAssuredHelper.java";

        createFileFromResource(
                fileName,
                "/utilClassForTest/RestAssuredHelper"
        );
    }

    private void testBase(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/test/java/ru/russianpost/utils/TestBase.java";

        createFileFromResource(
                fileName,
                "/utilClassForTest/TestBase"
        );
    }

    private void testExample(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/test/java/ru/russianpost/testcases/ExampleTest.java";

        createFileFromResource(
                fileName,
                "/utilClassForTest/ExampleTest"
        );
    }

    private void gitlabCi() {

        String fileName =
                System.getProperty("user.dir")
                        + "/.gitlab-ci.yml";

        createFileFromResource(
                fileName,
                "/utilClassForTest/gitlab-ci"
        );
    }

    private void gitignore() {

        String fileName =
                System.getProperty("user.dir")
                        + "/.gitignore";

        createFileFromResource(
                fileName,
                "/utilClassForTest/gitignore"
        );
    }

    private void allureProperties(String pathToProject) {

        String fileName =
                pathToProject
                        + "/src/test/resources/allure.properties";

        createFileFromResource(
                fileName,
                "/utilClassForTest/allureProperties"
        );
    }

    private void allureGradle(String pathToProject) {

        String fileName =
                pathToProject
                        + "/allure.gradle";

        createFileFromResource(
                fileName,
                "/utilClassForTest/allureGradle"
        );
    }

    private void createFileFromResource(
            String fileName,
            String resourcePath
    ) {

        try {

            File file = new File(fileName);

            if (file.exists()) {
                return;
            }

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            String content = getResourceAsText(getClass(), resourcePath);

            if (content == null) {
                throw new RuntimeException(
                        "Resource not found: " + resourcePath
                );
            }

            Files.writeString(
                    Path.of(fileName),
                    content
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed create file: " + fileName,
                    e
            );
        }
    }
}
