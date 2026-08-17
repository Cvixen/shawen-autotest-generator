package sw.autotest.generator;

import org.gradle.api.Project;

public class Dependencies {

    private static final String IMPLEMENTATION = "implementation";

    public void addDependenciesToProject(Project project) {

        project.afterEvaluate(p -> {

            p.getDependencies().add(
                    IMPLEMENTATION,
                    "org.openapitools:jackson-databind-nullable:0.2.10"
            );
            //TODO: перевести на версию io.swagger.core.v3:swagger-annotations:2.2.52
            p.getDependencies().add(
                    IMPLEMENTATION,
                    "io.swagger:swagger-annotations:1.6.16"
            );

            p.getDependencies().add(
                    IMPLEMENTATION,
                    "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.1"
            );

        });
    }
}