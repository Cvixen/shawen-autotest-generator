package sw.autotest.generator.generatePlugin.utils.generateTest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTestBySpec {

    private String componentId = "";
    private String version = "";
    private List<Paths> methods = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Paths {

        private String methodAction = "";
        private String endPoint = "";
        private String methodName = "";
        private String requestModelName = "";
        private String responsesModelName = "";
        private RequiredField requiredField = new RequiredField();


        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RequiredField {

            private List<Body> body = new ArrayList<>();
            private List<Header> header = new ArrayList<>();
            private List<Parameter> parameters = new ArrayList<>();
            private List<Query> query = new ArrayList<>();

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Body {
                private String name = "";
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Header {

                private String name = "";
                private String type = "";
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Parameter {

                private String name = "";
                private String type = "";

            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Query {

                private String name = "";
                private String type = "";
            }
        }
    }


    // =========================
    // Utility methods
    // =========================

    public static ArrayList<Paths.RequiredField.Body> addValueToBody(
            List<String> arrayRequiredFields
    ) {

        ArrayList<Paths.RequiredField.Body> result =
                new ArrayList<>();

        if (arrayRequiredFields != null) {

            for (String name : arrayRequiredFields) {

                result.add(
                        new Paths.RequiredField.Body(name)
                );
            }
        }

        return result;
    }

    public static ArrayList<Paths.RequiredField.Header> addValueToHeader(
            Map<String, String> mapHeader
    ) {

        ArrayList<Paths.RequiredField.Header> result =
                new ArrayList<>();

        for (Map.Entry<String, String> entry
                : mapHeader.entrySet()) {

            result.add(
                    new Paths.RequiredField.Header(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return result;
    }

    public static ArrayList<Paths.RequiredField.Query> addValueToQuery(
            Map<String, String> mapQuery
    ) {

        ArrayList<Paths.RequiredField.Query> result =
                new ArrayList<>();

        for (Map.Entry<String, String> entry
                : mapQuery.entrySet()) {

            result.add(
                    new Paths.RequiredField.Query(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return result;
    }

    public static ArrayList<Paths.RequiredField.Parameter> addValueToParameter(
            Map<String, String> mapParam
    ) {

        ArrayList<Paths.RequiredField.Parameter> result =
                new ArrayList<>();

        for (Map.Entry<String, String> entry
                : mapParam.entrySet()) {

            result.add(
                    new Paths.RequiredField.Parameter(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return result;
    }
}