package sw.autotest.generator.generatePlugin.utils.generateTest;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class ParseSwaggerSpecMethods {

    /**
     * Поиск action (метода) в swagger документации.
     *
     * @return Map.Entry<PathItem.HttpMethod, Operation>
     */
    public Map.Entry<PathItem.HttpMethod, Operation> action(
            OpenAPI openAPI
    ) {

        Map.Entry<PathItem.HttpMethod, Operation> actionFind =
                new AbstractMap.SimpleEntry<>(null, null);

        for (String method : openAPI.getPaths().keySet()) {

            PathItem pathItem =
                    openAPI.getPaths().get(method);

            if (pathItem == null) {
                continue;
            }

            for (Map.Entry<PathItem.HttpMethod, Operation> actionEach
                    : pathItem.readOperationsMap().entrySet()) {

                Operation operation =
                        actionEach.getValue();

                if (operation == null) {
                    continue;
                }

                boolean notDeprecated =
                        operation.getDeprecated() == null;

                boolean notRabbitMq =
                        operation.getTags() == null
                                || !operation.getTags().contains("RabbitMQ");

                if (notDeprecated && notRabbitMq) {
                    actionFind = actionEach;
                }
            }
        }

        return actionFind;
    }

    /**
     * Поиск имени request model.
     *
     * @return имя модели
     */
    public String requestModelName(
            Map.Entry<PathItem.HttpMethod, Operation> action
    ) {

        String requestModelName = "";

        if (action == null
                || action.getValue() == null
                || action.getValue().getRequestBody() == null
                || action.getValue().getRequestBody().getContent() == null) {

            return requestModelName;
        }

        for (var content
                : action.getValue()
                .getRequestBody()
                .getContent()
                .values()) {

            if (content.getSchema() != null
                    && content.getSchema().get$ref() != null) {

                String ref =
                        content.getSchema().get$ref();

                requestModelName =
                        ref.substring(
                                ref.lastIndexOf("/") + 1
                        );
            }
        }

        return requestModelName;
    }

    /**
     * Поиск имени response model.
     *
     * @return имя модели
     */
    public String responsesModelName(
            Map.Entry<PathItem.HttpMethod, Operation> action
    ) {

        String responsesModelName = "";

        if (action == null
                || action.getValue() == null
                || action.getValue().getResponses() == null
                || action.getValue().getResponses().get("200") == null
                || action.getValue().getResponses().get("200").getContent() == null) {

            return responsesModelName;
        }

        for (var content
                : action.getValue()
                .getResponses()
                .get("200")
                .getContent()
                .values()) {

            if (content.getSchema() != null
                    && content.getSchema().get$ref() != null) {

                String ref =
                        content.getSchema().get$ref();

                responsesModelName =
                        ref.substring(
                                ref.lastIndexOf("/") + 1
                        );
            }
        }

        return responsesModelName;
    }

    /**
     * Поиск обязательных path параметров.
     *
     * @return map<name, type>
     */
    public Map<String, String> requiredParameters(
            Map.Entry<PathItem.HttpMethod, Operation> action
    ) {

        Map<String, String> mapParameters =
                new HashMap<>();

        if (action == null
                || action.getValue() == null
                || action.getValue().getParameters() == null) {

            return mapParameters;
        }

        action.getValue()
                .getParameters()
                .forEach(parameter -> {

                    if ("path".equals(parameter.getIn())
                            && Boolean.TRUE.equals(parameter.getRequired())) {

                        String type =
                                parameter.getSchema() != null
                                        ? parameter.getSchema().getType()
                                        : null;

                        mapParameters.put(
                                parameter.getName(),
                                type
                        );
                    }
                });

        return mapParameters;
    }

    /**
     * Поиск обязательных header параметров.
     *
     * @return map<name, type>
     */
    public Map<String, String> requiredHeaders(
            Map.Entry<PathItem.HttpMethod, Operation> action
    ) {

        Map<String, String> mapHeader =
                new HashMap<>();

        if (action == null
                || action.getValue() == null
                || action.getValue().getParameters() == null) {

            return mapHeader;
        }

        action.getValue()
                .getParameters()
                .forEach(parameter -> {

                    if ("header".equals(parameter.getIn())
                            && Boolean.TRUE.equals(parameter.getRequired())) {

                        String type =
                                parameter.getSchema() != null
                                        ? parameter.getSchema().getType()
                                        : null;

                        mapHeader.put(
                                parameter.getName(),
                                type
                        );
                    }
                });

        return mapHeader;
    }

    /**
     * Поиск обязательных query параметров.
     *
     * @return map<name, type>
     */
    public Map<String, String> requiredQueryParam(
            Map.Entry<PathItem.HttpMethod, Operation> action
    ) {

        Map<String, String> mapQueryParam =
                new HashMap<>();

        if (action == null
                || action.getValue() == null
                || action.getValue().getParameters() == null) {

            return mapQueryParam;
        }

        action.getValue()
                .getParameters()
                .forEach(parameter -> {

                    if ("query".equals(parameter.getIn())
                            && Boolean.TRUE.equals(parameter.getRequired())) {

                        String type = "object";

                        if (parameter.getSchema() != null
                                && parameter.getSchema().getType() != null) {

                            type =
                                    parameter.getSchema().getType();
                        }

                        mapQueryParam.put(
                                parameter.getName(),
                                type
                        );
                    }
                });

        return mapQueryParam;
    }
}