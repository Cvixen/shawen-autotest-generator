package sw.autotest.generator.generatePlugin;

import lombok.extern.slf4j.Slf4j;
import sw.autotest.generator.Constants;
import sw.autotest.generator.Extensions;
import sw.autotest.generator.generatePlugin.utils.CreateTemplates;
import sw.autotest.generator.generatePlugin.utils.DeleteApiTag;
import sw.autotest.generator.generatePlugin.utils.DeleteDeprecatedMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;

@Slf4j
public class DoFirstInCreationTask {

    /**
     * Метод, что происходит внутри таски первое:
     * <p>
     * CreateTemplates.createTemplates() -
     * создание темплейтов для генерации клиентов и моделей;
     * <p>
     * deleteDirectory(pathToApiClient) -
     * удаление всех файлов из папки api;
     * <p>
     * deleteDirectory(pathToModel) -
     * удаление всех файлов из папки model;
     */
    public static void doFirstInCreationTask(
            File spec,
            String pathToProject,
            Extensions extensions
    ) {

        if (extensions.isDeleteRabbitMq()) {

            DeleteApiTag.deleteMethodWithTagFromSwaggerSpec(
                    spec,
                    "RabbitMQ"
            );
        }

        if (extensions.isDeleteDeprecatedMethod()) {

            DeleteDeprecatedMethod.deleteDeprecatedMethod(
                    spec
            );
        }

        String specName =
                spec.getName()
                        .replace(".yaml", "");

        String lowerName =
                specName.toLowerCase(Locale.ROOT);

        String basePackage =
                Constants.BASE_PACKAGE;

        String pathToApiClient =
                pathToProject
                        + "/src/main/java/"
                        + basePackage.replace(".", "/")
                        + "/"
                        + lowerName
                        + "/api";

        String pathToModel =
                pathToProject
                        + "/src/main/java/"
                        + basePackage.replace(".", "/")
                        + "/"
                        + lowerName
                        + "/model";

        if (extensions.isUtils()) {

            new CreateTemplates().createTemplates(
                    pathToProject
            );
        }

        deleteDirectory(pathToApiClient);
        deleteDirectory(pathToModel);
        log.info("===============\n"
                + "Start generate client and models for "
                + specName.toUpperCase(Locale.ROOT) + "\n==============="

        );
    }

    private static void deleteDirectory(String path) {

        Path dir = Path.of(path);

        if (!Files.exists(dir)) {
            return;
        }

        try {

            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed delete directory: " + path,
                    e
            );
        }
    }
}