package sw.autotest.generator.generatePlugin.utils;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class DeleteApiTag {

    public static void deleteMethodWithTagFromSwaggerSpec(
            File specFile,
            String tag
    ) {

        try {

            String specContent =
                    Files.readString(specFile.toPath());

            // Быстрая проверка как в оригинальном плагине
            if (!specContent.contains(tag)) {
                return;
            }

            OpenAPI openAPI =
                    new OpenAPIV3Parser()
                            .read(specFile.getPath());

            if (openAPI == null || openAPI.getPaths() == null) {
                return;
            }

            Set<String> pathsForDelete = new HashSet<>();

            openAPI.getPaths().forEach((path, pathItem) -> {

                if (String.valueOf(pathItem).contains(tag)) {
                    pathsForDelete.add(path);
                }
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
            log.info("Deleted paths with tag: " + tag);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to delete tag: " + tag,
                    e
            );
        }
    }
}