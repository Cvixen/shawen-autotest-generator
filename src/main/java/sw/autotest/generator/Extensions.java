package sw.autotest.generator;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class Extensions {

    /**
     * Путь до swagger/openapi спецификаций.
     * Например:
     * spec
     */
    private String specToGenerate;

    /**
     * Генерировать utils.
     */
    private boolean utils = false;

    /**
     * Путь до TestConfig.
     */
    private String testConfig;

    /**
     * URL/пути для скачивания спецификаций.
     */
    private Set<String> download =
            new LinkedHashSet<>();

    /**
     * Куда сохранять скачанные спецификации.
     */
    private String pathToSaveSpec;

    /**
     * Путь до helper classes.
     */
    private String helpers;

    /**
     * Удалять RabbitMQ endpoints.
     */
    private boolean deleteRabbitMq = true;

    /**
     * Удалять deprecated endpoints.
     */
    private boolean deleteDeprecatedMethod = true;

    /**
     * Путь до custom templates.
     */
    private String templateDir;
}