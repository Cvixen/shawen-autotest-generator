package sw.autotest.generator.generatePlugin.utils;

import org.gradle.api.model.ObjectFactory;
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask;
import sw.autotest.generator.Extensions;

import javax.inject.Inject;
import java.io.File;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class OpenApiConfig extends GenerateTask {

    @Inject
    public OpenApiConfig(ObjectFactory objectFactory) {
        super(objectFactory);
    }

    public void parameters(
            File spec,
            String basePackage,
            String pathToProject,
            Extensions extensions
    ) {

        String nameFile = spec.getName()
                .substring(0, spec.getName().lastIndexOf("."));

        String lowerCaseName =
                nameFile.toLowerCase(Locale.ROOT);

        // =========================
        // Base OpenAPI config
        // =========================

        getGeneratorName().set("java");

        getLibrary().set("rest-assured");

        getOutputDir().set(pathToProject);

        getInputSpec().set(spec.getPath());

        getInvokerPackage().set(basePackage);

        getApiPackage().set(
                basePackage + "."
                        + lowerCaseName
                        + ".api"
        );

        getModelPackage().set(
                basePackage + "."
                        + lowerCaseName
                        + ".model"
        );

        // =========================
        // Disable unnecessary generation
        // =========================

        getGenerateModelDocumentation().set(false);

        getGenerateApiDocumentation().set(false);

        getGenerateModelTests().set(false);

        getGenerateApiTests().set(false);

        getSkipValidateSpec().set(true);

        // =========================
        // Maven coordinates
        // =========================

        getGroupId().set("sw.generator");

        getId().set("client");

        // =========================
        // Optional template dir
        // =========================

        if (hasText(extensions.getTemplateDir())) {

            getTemplateDir().set(
                    extensions.getTemplateDir()
            );
        }

        // =========================
        // Config options
        // =========================

        getConfigOptions().put(
                "dateLibrary",
                "java8"
        );

        getConfigOptions().put(
                "serializationLibrary",
                "jackson"
        );

        getConfigOptions().put(
                "useJakartaEe",
                "true"
        );

        getConfigOptions().put(
                "annotationLibrary",
                "none"
        );

        // =========================
        // Type mappings
        // =========================

        getTypeMappings().put(
                "OffsetDateTime",
                "String"
        );

        // =========================
        // Global properties
        // =========================

        getGlobalProperties().put(
                "docs",
                "false"
        );

        getGlobalProperties().put(
                "apis",
                ""
        );

        getGlobalProperties().put(
                "models",
                ""
        );

        getGlobalProperties().put(
                "debugOperations",
                "true"
        );

        // =========================
        // Additional properties
        // =========================

        getAdditionalProperties().put(
                "productName",
                capitalize(lowerCaseName)
        );
    }

    private boolean hasText(String value) {
        return value != null
                && !value.isBlank();
    }
}