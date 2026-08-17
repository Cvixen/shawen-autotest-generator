package sw.autotest.generator.generatePlugin;

import sw.autotest.generator.Constants;
import sw.autotest.generator.Extensions;
import sw.autotest.generator.generatePlugin.utils.CreateBeanInTestConfig;
import sw.autotest.generator.generatePlugin.utils.CreateHelpersClass;
import sw.autotest.generator.generatePlugin.utils.CreateUtilClassForGeneratedModelAndClient;
import sw.autotest.generator.generatePlugin.utils.RenameGenerateFiles;

import java.io.File;
import java.util.Locale;

public class DoLastInCreationTask {

    public static void process(
            File spec,
            Extensions extensions,
            String pathToProject
    ) {

        String specName =
                spec.getName()
                        .replace(".yaml", "");

        String lowerName =
                specName.toLowerCase(Locale.ROOT);

        String basePackage =
                Constants.BASE_PACKAGE;

        String basePath =
                pathToProject
                        + "/src/main/java/"
                        + basePackage.replace(".", "/")
                        + "/"
                        + lowerName;

        String pathToApiClient =
                basePath + "/api";

        String pathToClientHelper =
                basePath
                        + "/"
                        + specName
                        + "CallingGeneratedClients";

        RenameGenerateFiles.renameApiClient(
                pathToApiClient
        );

        CreateUtilClassForGeneratedModelAndClient
                .createUtils(pathToProject);

        if (extensions.getTestConfig() != null
                && !extensions.getTestConfig().isBlank()) {

            CreateBeanInTestConfig
                    .createBeanInTestConfig(
                            pathToApiClient,
                            pathToProject
                                    + extensions.getTestConfig()
                    );

        } else {

            CreateBeanInTestConfig
                    .createBeanHelper(
                            pathToApiClient,
                            pathToClientHelper
                    );
        }

        if (extensions.getHelpers() != null
                && !extensions.getHelpers().isBlank()) {

            CreateHelpersClass.createHelpers(
                    spec,
                    specName,
                    pathToProject,
                    extensions.getHelpers()
            );
        }
    }
}