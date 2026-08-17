package sw.autotest.generator;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import sw.autotest.generator.generatePlugin.DoFirstInCreationTask;
import sw.autotest.generator.generatePlugin.DoLastInCreationTask;
import sw.autotest.generator.generatePlugin.ExecutionBeforeCreation;
import sw.autotest.generator.generatePlugin.utils.Download;
import sw.autotest.generator.generatePlugin.utils.OpenApiConfig;
import sw.autotest.generator.generatePlugin.utils.FindSpecificationFiles;

import java.io.File;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.capitalize;


public class SwPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        Extensions extensions =
                project.getExtensions()
                        .create(
                                "generator",
                                Extensions.class
                        );

        new Dependencies()
                .addDependenciesToProject(project);

        project.afterEvaluate(currentProject -> {

            String pathToProject =
                    currentProject
                            .getProjectDir()
                            .getAbsolutePath();

            new Download()
                    .downloadAndSaveSpecification(
                            extensions,
                            pathToProject
                    );

            new ExecutionBeforeCreation()
                    .createBuildGradleAndUtilsClassForTest(
                            extensions,
                            pathToProject
                    );

            List<File> specs =
                    new FindSpecificationFiles()
                            .findSpec(
                                    extensions.getSpecToGenerate(),
                                    pathToProject
                            );

            for (File spec : specs) {

                String taskName =
                        "generate"
                                + capitalize(
                                spec.getName()
                                        .replace(".yaml", "")
                        );

                currentProject
                        .getTasks()
                        .register(
                                taskName,
                                OpenApiConfig.class,
                                task -> {

                                    task.parameters(
                                            spec,
                                            Constants.BASE_PACKAGE,
                                            pathToProject,
                                            extensions
                                    );

                                    task.doFirst(action ->
                                            DoFirstInCreationTask.doFirstInCreationTask(
                                                    spec,
                                                    pathToProject,
                                                    extensions
                                            )
                                    );

                                    task.doLast(action ->
                                            DoLastInCreationTask.process(
                                                    spec,
                                                    extensions,
                                                    pathToProject
                                            )
                                    );

                                    task.setGroup(
                                            "shawen autotest generator"
                                    );
                                }
                        );
            }
        });
    }
}