package sw.autotest.generator.generatePlugin.utils;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class DeleteDeprecatedMethod {

    public static void deleteDeprecatedMethod(File specFile) {

        try {

            OpenAPI openAPI =
                    new OpenAPIV3Parser()
                            .read(specFile.getPath());

            if (openAPI == null || openAPI.getPaths() == null) {
                return;
            }

            Set<String> pathsForDelete = new HashSet<>();

            openAPI.getPaths().forEach((path, pathItem) -> {

                if (pathItem == null) {
                    return;
                }

                Map<PathItem.HttpMethod, Operation> operations =
                        pathItem.readOperationsMap();

                if (operations == null) {
                    return;
                }

                operations.forEach((httpMethod, operation) -> {

                    if (operation != null
                            && Boolean.TRUE.equals(operation.getDeprecated())) {

                        pathsForDelete.add(path);
                    }
                });
            });

            pathsForDelete.forEach(path ->
                    openAPI.getPaths().remove(path)
            );

            String updatedSpec =
                    Yaml.mapper()
                            .writeValueAsString(openAPI);

            Files.writeString(
                    specFile.toPath(),
                    updatedSpec
            );
            log.info("Deleted deprecated paths: " + pathsForDelete.size());
        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to delete deprecated methods",
                    e
            );
        }
    }
}