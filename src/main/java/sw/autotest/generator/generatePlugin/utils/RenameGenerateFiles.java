package sw.autotest.generator.generatePlugin.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameGenerateFiles {

    private static final Pattern CLASS_PATTERN =
            Pattern.compile("public\\s+class\\s+(\\w+)");

    public static void renameApiClient(String pathToClient) {
        try {
            Files.walk(Path.of(pathToClient))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("ApiApi.java"))
                    .forEach(RenameGenerateFiles::removeDoubleApi);

            Files.walk(Path.of(pathToClient))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(RenameGenerateFiles::processFile);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed rename generated files",
                    e
            );
        }
    }

    private static void removeDoubleApi(Path path) {
        try {
            String content = Files.readString(path);
            content =
                    content.replace(
                            "ApiApi",
                            "Api"
                    );

            Files.writeString(path, content);
        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed renove double name Api: " + path,
                    e
            );
        }
    }


    private static void processFile(Path path) {
        try {
            String content = Files.readString(path);
            Matcher matcher = CLASS_PATTERN.matcher(content);
            if (matcher.find()) {
                String className = matcher.group(1);
                Path newPath =
                        path.resolveSibling(
                                className + ".java"
                        );

                Files.move(
                        path,
                        newPath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed process file: " + path,
                    e
            );
        }
    }
}