package sw.autotest.generator.generatePlugin.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CreateBeanInTestConfig {

    private static final Pattern STATIC_FACTORY_PATTERN =
            Pattern.compile(
                    "public\\s+static\\s+(\\w+)\\s+(\\w+)\\s*\\(Supplier<RequestSpecBuilder>\\s+reqSpecSupplier\\)"
            );

    private static final Pattern PACKAGE_PATTERN =
            Pattern.compile("package\\s+([\\w.]+);");

    public static void createBeanInTestConfig(
            String pathToClient,
            String pathToTestConfig
    ) {

        try (Stream<Path> paths = Files.walk(Path.of(pathToClient))) {

            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> processApiFile(
                            path,
                            pathToTestConfig
                    ));

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed create beans in test config",
                    e
            );
        }
    }

    public static void createBeanHelper(
            String pathToClient,
            String pathToClientHelper
    ) {

        try {

            Path helperPath = Path.of(pathToClientHelper);

            if (!Files.exists(helperPath)) {

                Files.createDirectories(helperPath.getParent());
                Files.createFile(helperPath);
            }

            ensureHelperImports(helperPath);

            try (Stream<Path> paths =
                         Files.walk(Path.of(pathToClient))) {

                paths.filter(Files::isRegularFile)
                        .filter(path ->
                                path.toString().endsWith(".java"))
                        .forEach(path ->
                                processHelperFile(
                                        path,
                                        helperPath
                                ));
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed create helper beans",
                    e
            );
        }
    }

    private static void processApiFile(
            Path apiFile,
            String testConfigPath
    ) {

        try {

            String content = Files.readString(apiFile);

            Matcher matcher =
                    STATIC_FACTORY_PATTERN.matcher(content);

            if (!matcher.find()) {
                return;
            }

            String className = matcher.group(1);
            String staticMethodName = matcher.group(2);

            String bean =
                    createBean(
                            className,
                            staticMethodName
                    );

            insertBean(
                    Path.of(testConfigPath),
                    className,
                    bean
            );

            addImport(
                    Path.of(testConfigPath),
                    apiFile
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed process api file: " + apiFile,
                    e
            );
        }
    }

    private static void processHelperFile(
            Path apiFile,
            Path helperPath
    ) {

        try {

            String apiContent =
                    Files.readString(apiFile);

            Matcher matcher =
                    STATIC_FACTORY_PATTERN.matcher(apiContent);

            if (!matcher.find()) {
                return;
            }

            String className = matcher.group(1);
            String staticMethodName = matcher.group(2);

            String helperContent =
                    Files.readString(helperPath);

            if (helperContent.contains(className)) {
                return;
            }

            String importLine =
                    buildImport(apiFile);

            if (!helperContent.contains(importLine)) {

                helperContent +=
                        "\nimport "
                                + importLine
                                + ";";

                Files.writeString(
                        helperPath,
                        helperContent
                );
            }

            String bean =
                    createBeanToHelper(
                            className,
                            staticMethodName
                    );

            Files.writeString(
                    helperPath,
                    "\n" + bean,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed process helper file: " + apiFile,
                    e
            );
        }
    }

    private static String createBean(
            String className,
            String staticMethodName
    ) {

        String methodName =
                decapitalize(className);

        return String.format("""
                        
                        @Bean
                        @Scope("prototype")
                        public %s %s() {
                            return %s.%s(
                                    () -> getSpecBuilder(
                                            testConfig.getUrl().getTestUrl()
                                    )
                            );
                        }
                        """
                ,
                className,
                methodName,
                className,
                staticMethodName
        );
    }

    private static String createBeanToHelper(
            String className,
            String staticMethodName
    ) {

        String methodName =
                decapitalize(className);

        return String.format("""
                        
                        %s %s;
                        
                        @BeforeAll
                        void client() {
                        
                            %s =
                                    %s.%s(
                                            () -> new RequestSpecBuilder()
                                                    .log(LogDetail.ALL)
                                                    .setConfig(
                                                            config()
                                                                    .objectMapperConfig(
                                                                            objectMapperConfig()
                                                                                    .defaultObjectMapper(
                                                                                            jackson()
                                                                                    )
                                                                    )
                                                    )
                                                    .addFilter(
                                                            new ResponseLoggingFilter()
                                                    )
                                                    .addHeader(
                                                            "X-CorrelationId",
                                                            UUID.randomUUID()
                                                                    .toString()
                                                    )
                                                    .setBaseUri(
                                                            "http://your.endpoint/"
                                                    )
                                    );
                        }
                        
                        ======================================
                        """
                ,
                className,
                methodName,
                methodName,
                className,
                staticMethodName
        );
    }

    private static void insertBean(
            Path testConfigPath,
            String className,
            String bean
    ) throws IOException {

        String content =
                Files.readString(testConfigPath);

        String methodName =
                decapitalize(className);

        if (content.contains(methodName + "()")) {
            return;
        }

        int lastBrace =
                content.lastIndexOf("}");

        if (lastBrace == -1) {

            throw new IllegalStateException(
                    "No closing brace found in "
                            + testConfigPath
            );
        }

        String updated =
                content.substring(0, lastBrace).trim()
                        + "\n"
                        + bean
                        + "\n}";

        Files.writeString(
                testConfigPath,
                updated
        );
    }

    private static void addImport(
            Path testConfigPath,
            Path apiFile
    ) throws IOException {

        String content =
                Files.readString(testConfigPath);

        String importLine =
                buildImport(apiFile);

        if (content.contains(importLine)) {
            return;
        }

        Matcher matcher =
                PACKAGE_PATTERN.matcher(content);

        if (!matcher.find()) {

            throw new IllegalStateException(
                    "Package declaration not found in "
                            + testConfigPath
            );
        }

        String packageLine =
                matcher.group(0);

        String updated =
                content.replace(
                        packageLine,
                        packageLine
                                + "\n\nimport "
                                + importLine
                                + ";"
                );

        Files.writeString(
                testConfigPath,
                updated
        );
    }

    private static String buildImport(Path apiFile) {

        String normalized =
                apiFile.toString()
                        .replace("\\", "/");

        int srcIndex =
                normalized.indexOf("/java/");

        if (srcIndex == -1) {

            throw new IllegalStateException(
                    "Cannot build import from path: "
                            + apiFile
            );
        }

        String importPath =
                normalized.substring(srcIndex + 6)
                        .replace("/", ".")
                        .replace(".java", "");

        return importPath;
    }

    private static void ensureHelperImports(
            Path helperPath
    ) throws IOException {

        String content =
                Files.readString(helperPath);

        if (content.contains(
                "io.restassured.builder.RequestSpecBuilder"
        )) {
            return;
        }

        String imports = """
                
                import io.restassured.builder.RequestSpecBuilder;
                import io.restassured.filter.log.LogDetail;
                import io.restassured.filter.log.ResponseLoggingFilter;
                import java.util.UUID;
                import org.junit.jupiter.api.BeforeAll;
                
                import static io.restassured.RestAssured.config;
                import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
                import static app.generator.client.utils.JacksonObjectMapper.jackson;
                
                """;

        Files.writeString(
                helperPath,
                content + "\n" + imports
        );
    }

    public static String decapitalize(String value) {

        if (value == null || value.isEmpty()) {
            return value;
        }

        return Character.toLowerCase(value.charAt(0))
                + value.substring(1);
    }
}