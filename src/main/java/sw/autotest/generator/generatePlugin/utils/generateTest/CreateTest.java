package sw.autotest.generator.generatePlugin.utils.generateTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.SneakyThrows;
import sw.autotest.generator.Extensions;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CreateTest {

    /**
     * Создание конфига из Swagger/OpenAPI документации
     * для генерации тестовых классов.
     */
    @SneakyThrows
    public void createTest(Extensions extensions, String pathToProject) {

        File specDirectory = new File(pathToProject + "/" + extensions.getSpecToGenerate());
        File[] files = specDirectory.listFiles();

        if (files == null) {
            return;
        }

        for (File fileSpec : files) {
            if (!fileSpec.isFile()) {
                continue;
            }
            String fileName = fileSpec.getName();
            if (!fileName.matches("^.*\\.yaml$|^.*\\.yml$")) {
                continue;
            }
            OpenAPI openAPI = new OpenAPIV3Parser().read(fileSpec.getPath());

            if (openAPI == null || openAPI.getPaths() == null) {
                continue;
            }

            // Find required fields in components models
            Map<String, List<String>> requiredFieldInModels = new ParseSwaggerSpecSchemas()
                    .searchRequiredFieldInBody(openAPI);

            GenerateTestBySpec configForTest = new GenerateTestBySpec();

            Map<String, String> mapParameters;
            Map<String, String> mapHeader;
            Map<String, String> mapQueryParam;

            for (String method : openAPI.getPaths().keySet()) {
                var pathItem = openAPI.getPaths().get(method);

                if (pathItem == null) {
                    continue;
                }

                var operationsMap = pathItem.readOperationsMap();

                for (Map.Entry<PathItem.HttpMethod, Operation> action : operationsMap.entrySet()) {
                    Operation operation = action.getValue();
                    boolean isDeprecated = operation.getDeprecated() != null;

                    boolean isRabbitMq = operation.getTags() != null && operation.getTags().contains("RabbitMQ");

                    if (isDeprecated || isRabbitMq) {
                        continue;
                    }

                    // Find requestModelName
                    String requestModelName = new ParseSwaggerSpecMethods().requestModelName(action);

                    // Find responsesModelName
                    String responsesModelName = new ParseSwaggerSpecMethods().responsesModelName(action);

                    // Required parameters field
                    mapParameters = new ParseSwaggerSpecMethods().requiredParameters(action);

                    // Find required Header field
                    mapHeader = new ParseSwaggerSpecMethods().requiredHeaders(action);

                    // Find required query fields
                    mapQueryParam = new ParseSwaggerSpecMethods().requiredQueryParam(action);

                    configForTest.setComponentId(
                            fileSpec.getName()
                                    .toLowerCase()
                                    .substring(
                                            0,
                                            fileSpec.getName()
                                                    .lastIndexOf(".")
                                    )
                    );

                    configForTest.setVersion(
                            openAPI.getInfo().getVersion()
                    );
                    GenerateTestBySpec.Paths.RequiredField requiredField =
                            new GenerateTestBySpec.Paths.RequiredField(
                                    GenerateTestBySpec.addValueToBody(
                                            requiredFieldInModels.get(
                                                    requestModelName
                                            )
                                    ),
                                    GenerateTestBySpec.addValueToHeader(
                                            mapHeader
                                    ),
                                    GenerateTestBySpec.addValueToParameter(
                                            mapParameters
                                    ),
                                    GenerateTestBySpec.addValueToQuery(
                                            mapQueryParam
                                    )
                            );

                    GenerateTestBySpec.Paths path =
                            new GenerateTestBySpec.Paths(
                                    (action.getKey()).name(),
                                    method,
                                    operation.getOperationId(),
                                    requestModelName,
                                    responsesModelName,
                                    requiredField
                            );

                    configForTest.getMethods().add(path);

                    new UtilsToCreateTest()
                            .createFolderAndFileWithTests(
                                    fileSpec,
                                    configForTest
                            );

                    mapHeader.clear();
                    mapParameters.clear();
                    mapQueryParam.clear();
                }
            }

            new CreateTestScenarios()
                    .parseSwaggerConfigAndCreateTests(
                            fileSpec,
                            new ObjectMapper()
                                    .writeValueAsString(configForTest),
                            pathToProject
                    );
        }
    }
}