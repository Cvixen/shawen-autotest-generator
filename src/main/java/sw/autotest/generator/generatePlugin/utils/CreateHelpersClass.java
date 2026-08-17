package sw.autotest.generator.generatePlugin.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class CreateHelpersClass {

    public static void createHelpers(
            File spec,
            String nameFile,
            String pathToProject,
            String pathToFolder
    ) {

        Set<String> operationIds =
                getOperationIdsFromSwagger(spec);

        createHelperClass(
                nameFile,
                pathToProject,
                pathToFolder
        );

        createMethodsInHelper(
                operationIds,
                nameFile,
                pathToProject,
                pathToFolder
        );
    }

    private static void createMethodsInHelper(
            Set<String> operationIds,
            String nameFile,
            String pathToProject,
            String pathToFolder
    ) {

        String helperName =
                capitalize(nameFile) + "Helper";

        String helperPath =
                pathToProject
                        + "/"
                        + pathToFolder
                        + "/"
                        + helperName
                        + ".java";

        for (String method : operationIds) {

            String methodBody =
                    String.format(
                            """
                                    
                                        /**
                                         * Метод для ...
                                         */
                                        public void %s {
                                    
                                        }
                                    
                                    """,
                            method
                    );

            insertMethodToHelper(
                    helperPath,
                    method,
                    methodBody
            );
        }
    }

    private static void createHelperClass(
            String nameFile,
            String pathToProject,
            String pathToFolder
    ) {

        try {

            String helperName =
                    capitalize(nameFile) + "Helper";

            Path filePath =
                    Path.of(
                            pathToProject,
                            pathToFolder,
                            helperName + ".java"
                    );

            if (Files.exists(filePath)) {
                return;
            }

            Files.createDirectories(filePath.getParent());

            String content =
                    String.format(
                            """
                                    package helpers;
                                    
                                    import org.springframework.stereotype.Component;
                                    
                                    @Component
                                    public class %s {
                                    
                                    }
                                    """,
                            helperName
                    );

            Files.writeString(filePath, content);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed create helper class",
                    e
            );
        }
    }

    private static Set<String> getOperationIdsFromSwagger(
            File spec
    ) {

        OpenAPI openAPI =
                new OpenAPIV3Parser()
                        .read(spec.getPath());

        Set<String> operationIds =
                new LinkedHashSet<>();

        if (openAPI == null || openAPI.getPaths() == null) {
            return operationIds;
        }

        for (Map.Entry<String, PathItem> entry :
                openAPI.getPaths().entrySet()) {

            PathItem pathItem = entry.getValue();

            addOperationId(
                    operationIds,
                    pathItem.getGet()
            );

            addOperationId(
                    operationIds,
                    pathItem.getPost()
            );

            addOperationId(
                    operationIds,
                    pathItem.getPut()
            );

            addOperationId(
                    operationIds,
                    pathItem.getDelete()
            );

            addOperationId(
                    operationIds,
                    pathItem.getPatch()
            );

            addOperationId(
                    operationIds,
                    pathItem.getTrace()
            );
        }

        return operationIds;
    }

    private static void addOperationId(
            Set<String> operationIds,
            Operation operation
    ) {

        if (operation == null) {
            return;
        }

        if (operation.getOperationId() == null) {
            return;
        }

        operationIds.add(
                operation.getOperationId() + "()"
        );
    }

    private static void insertMethodToHelper(
            String helperPath,
            String method,
            String addMethod
    ) {

        try {

            Path path = Path.of(helperPath);

            String content =
                    Files.readString(path);

            if (content.contains(method)) {
                return;
            }

            int lastBrace =
                    content.lastIndexOf("}");

            if (lastBrace == -1) {

                throw new IllegalStateException(
                        "No closing brace found in helper"
                );
            }

            String updated =
                    content.substring(0, lastBrace).trim()
                            + "\n"
                            + addMethod
                            + "\n}";

            Files.writeString(path, updated);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed insert method to helper",
                    e
            );
        }
    }
}
