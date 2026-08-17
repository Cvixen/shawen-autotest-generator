package sw.autotest.generator.generatePlugin.utils.generateTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class UtilsToCreateTest {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    /**
     * Тестовый метод для сохранения json файла
     * со сценарием генерации тестов.
     */
    public void createFolderAndFileWithTests(
            File fileSpec,
            GenerateTestBySpec configForTest
    ) {

        String testConfigFolder =
                System.getProperty("user.dir")
                        + "/a_test_config";

        File folder =
                new File(testConfigFolder);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName =
                fileSpec.getName()
                        .toLowerCase()
                        .substring(
                                0,
                                fileSpec.getName().lastIndexOf(".")
                        );

        File jsonFile =
                new File(
                        folder,
                        fileName + ".json"
                );

        try {

            OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValue(
                            jsonFile,
                            configForTest
                    );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed save test config: "
                            + jsonFile.getAbsolutePath(),
                    e
            );
        }
    }
}