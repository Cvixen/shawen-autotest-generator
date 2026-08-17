package sw.autotest.generator.generatePlugin.utils;

import lombok.SneakyThrows;
import org.gradle.api.InvalidUserDataException;
import sw.autotest.generator.Extensions;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Download {
    @SneakyThrows
    public void downloadAndSaveSpecification(
            Extensions extensions,
            String pathToProject
    ) {

        if (!extensions.getDownload().isEmpty()) {

            if (extensions.getPathToSaveSpec() == null
                    || extensions.getPathToSaveSpec().isBlank()) {

                throw new InvalidUserDataException(
                        "If you are using download, you need to specify pathToSaveSpec = \"....\""
                );
            }

            for (String dataToDownload : extensions.getDownload()) {

                downloadFile(
                        new URL(dataToDownload),
                        extensions.getPathToSaveSpec(),
                        pathToProject
                );
            }
        }
    }

    public void downloadFile(
            URL url,
            String pathToSave,
            String pathToProject
    ) {

        File directory =
                new File(pathToProject + "/" + pathToSave);

        directory.mkdirs();

        try (
                ReadableByteChannel rbc =
                        Channels.newChannel(url.openStream());

                FileOutputStream fos =
                        new FileOutputStream(
                                pathToProject
                                        + "/"
                                        + pathToSave
                                        + "/"
                                        + extractFileName(url)
                        )
        ) {

            fos.getChannel()
                    .transferFrom(rbc, 0, Long.MAX_VALUE);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed download file from: " + url,
                    e
            );
        }
    }

    private String extractFileName(URL url) {

        String urlString = url.toString();

        return urlString.substring(
                urlString.lastIndexOf("/") + 1
        );
    }
}