package sw.autotest.generator.generatePlugin.utils.generateTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class CreateTestScenarios {

    /**
     * Парсинг созданного конфига из Swagger документации
     * и создание классов с тестами.
     * <p>
     * На данный момент создание тестов идет
     * только по обязательным параметрам.
     *
     * @param fileSpec   swagger/openapi spec
     * @param jsonConfig json config with scenarios
     */
    public void parseSwaggerConfigAndCreateTests(
            File fileSpec,
            String jsonConfig,
            String pathToProject
    ) {

        try {

            String parentDirPath =
                    pathToProject
                            + "/src/test/java/ru/russianpost/testcases/negativeGenerate";

            File parentDir =
                    new File(parentDirPath);

            if (!parentDir.exists()) {
                parentDir.mkdir();
            }

            String substring = fileSpec.getName()
                    .substring(
                            0,
                            fileSpec.getName()
                                    .lastIndexOf(".")
                    );
            File dir = new File(parentDirPath, substring.toLowerCase());

            String generatedTestName = capitalize(substring) + "GeneratedTests.java";

            File generatedFile = new File(dir, generatedTestName);

            if (generatedFile.exists()) {
                return;
            }

            if (!dir.exists()) {
                dir.mkdir();
            }

            GenerateTestBySpec mU =
                    new ObjectMapper().readValue(
                            jsonConfig,
                            GenerateTestBySpec.class
                    );

            // create file
            try (BufferedWriter out =
                         new BufferedWriter(
                                 new FileWriter(generatedFile)
                         )) {

                out.write(
                        "package ru.russianpost.testcases.negativeGenerate."
                                + substring
                                .toLowerCase()
                                + ";"
                );

                out.newLine();
                out.newLine();

                out.write(
                        "import io.qameta.allure.Description;\n"
                                + "import org.junit.jupiter.api.Test;\n"
                                + "import ru.russianpost.utils.TestBase;\n"
                );

                out.newLine();
                out.newLine();

                out.write(
                        "//Scenario by openApi doc \""
                                + mU.getComponentId()
                                .toUpperCase()
                                + "\""
                );

                out.newLine();

                out.write(
                        "//Version spec: "
                                + mU.getVersion()
                                .toUpperCase()
                );

                out.newLine();

                out.write(
                        "public class "
                                + capitalize(
                                substring
                        )
                                + "GeneratedTests extends TestBase {"
                );

                out.newLine();
                out.newLine();
            }

            // generate tests
            if (mU.getMethods() != null) {

                for (GenerateTestBySpec.Paths methods
                        : mU.getMethods()) {

                    try (FileWriter write =
                                 new FileWriter(
                                         generatedFile,
                                         true
                                 )) {

                        // BODY
                        if (methods.getRequiredField() != null
                                && methods.getRequiredField().getBody() != null
                                && !methods.getRequiredField()
                                .getBody()
                                .isEmpty()) {

                            for (var bodyFields
                                    : methods.getRequiredField()
                                    .getBody()) {

                                write.write(
                                        createTestMethod(
                                                methods,
                                                bodyFields.getName(),
                                                "Body",
                                                "в теле запроса"
                                        )
                                );

                                write.write("\n\n");
                            }
                        }

                        // HEADER
                        if (methods.getRequiredField() != null
                                && methods.getRequiredField().getHeader() != null
                                && !methods.getRequiredField()
                                .getHeader()
                                .isEmpty()) {

                            for (var headerFields
                                    : methods.getRequiredField()
                                    .getHeader()) {

                                write.write(
                                        createTestMethod(
                                                methods,
                                                headerFields.getName(),
                                                "Header",
                                                "в header запроса"
                                        )
                                );

                                write.write("\n\n");
                            }
                        }

                        // PARAMETERS
                        if (methods.getRequiredField() != null
                                && methods.getRequiredField().getParameters() != null
                                && !methods.getRequiredField()
                                .getParameters()
                                .isEmpty()) {

                            for (var parameterFields
                                    : methods.getRequiredField()
                                    .getParameters()) {

                                write.write(
                                        createTestMethod(
                                                methods,
                                                parameterFields.getName(),
                                                "Parameters",
                                                "в параметрах запроса"
                                        )
                                );

                                write.write("\n\n");
                            }
                        }

                        // QUERY
                        if (methods.getRequiredField() != null
                                && methods.getRequiredField().getQuery() != null
                                && !methods.getRequiredField()
                                .getQuery()
                                .isEmpty()) {

                            for (var queryFields
                                    : methods.getRequiredField()
                                    .getQuery()) {

                                write.write(
                                        createTestMethod(
                                                methods,
                                                queryFields.getName(),
                                                "Query",
                                                "в query запроса"
                                        )
                                );

                                write.write("\n\n");
                            }
                        }
                    }
                }
            }

            // close class
            try (FileWriter write =
                         new FileWriter(
                                 generatedFile,
                                 true
                         )) {

                write.write("}");
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed create test scenarios",
                    e
            );
        }
    }

    private String createTestMethod(
            GenerateTestBySpec.Paths methods,
            String fieldName,
            String suffix,
            String description
    ) {

        String endpoint =
                methods.getEndPoint()
                        .replace("/", "")
                        .replace("{", "")
                        .replace("}", "")
                        .replace("-", "")
                        .replace(".", "");

        return "    @Test\n"
                + "    @Description(\"В методе ["
                + methods.getMethodAction()
                + " "
                + methods.getEndPoint()
                + "] "
                + description
                + " остуствует обязательное поле \\\""
                + fieldName
                + "\\\"\")\n"
                + "    public void "
                + methods.getMethodAction()
                .toLowerCase()
                + capitalize(endpoint)
                + "WithOut"
                + capitalize(
                fieldName.replace("-", "")
        )
                + "In"
                + suffix
                + "(){}";
    }
}