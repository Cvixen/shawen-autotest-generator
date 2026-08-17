package sw.autotest.generator.generatePlugin;

import sw.autotest.generator.Extensions;
import sw.autotest.generator.generatePlugin.utils.BuildGradleFromPlugin;
import sw.autotest.generator.generatePlugin.utils.CreateClassForStarterSpring;
import sw.autotest.generator.generatePlugin.utils.CreateUtilClassForTest;
import sw.autotest.generator.generatePlugin.utils.generateTest.CreateTest;

public class ExecutionBeforeCreation {

    /**
     * Метод для создания:
     * - build.gradle.example
     * - Utils классов для тестов
     * - стартового Spring-класса
     * - тестовых сценариев
     *
     * @param extensions    передаваемые расширения generator
     * @param pathToProject путь до проекта
     */
    public static void createBuildGradleAndUtilsClassForTest(
            Extensions extensions,
            String pathToProject
    ) {
        if (extensions.isUtils()) {
            new BuildGradleFromPlugin().buildGradle(pathToProject);
            new CreateUtilClassForTest().createUtilsClassesForTest(pathToProject);
            new CreateClassForStarterSpring().createClassForStarterSpring(pathToProject);
            new CreateTest().createTest(extensions, pathToProject);
        }
    }
}