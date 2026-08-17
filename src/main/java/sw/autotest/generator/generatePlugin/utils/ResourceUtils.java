package sw.autotest.generator.generatePlugin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceUtils {

    public static String getResourceAsText(Class<?> clazz, String path) {

        try (InputStream stream = clazz.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalArgumentException(
                        "Resource not found: " + path
                );
            }
            return new String(
                    stream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed read resource: " + path,
                    e
            );
        }
    }
}

