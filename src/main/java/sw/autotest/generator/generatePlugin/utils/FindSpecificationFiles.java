package sw.autotest.generator.generatePlugin.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FindSpecificationFiles {

    public List<File> findSpec(
            String pathToSpec,
            String pathToProject
    ) {

        List<File> nameSpecs = new ArrayList<>();

        File directory = new File(
                pathToProject + "/" + pathToSpec
        );

        File[] files = directory.listFiles();

        if (files == null) {
            return nameSpecs;
        }

        for (File file : files) {

            if (file.isFile()
                    && (file.getName().endsWith(".yaml")
                    || file.getName().endsWith(".yml"))) {

                nameSpecs.add(file);
            }
        }

        return nameSpecs;
    }
}