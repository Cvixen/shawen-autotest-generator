package sw.autotest.generator.generatePlugin.utils.generateTest;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ParseSwaggerSpecSchemas {

    /**
     * Поиск обязательных полей в models/components.
     *
     * @return Map:
     * ключ   = имя модели
     * значение = список обязательных полей
     * <p>
     * TODO:
     * Если в spec используется allOf —
     * body запроса сейчас собирается некорректно.
     */
    public Map<String, List<String>> searchRequiredFieldInBody(
            OpenAPI openAPI
    ) {

        Map<String, List<String>> mapWithSchema =
                new HashMap<>();

        if (openAPI == null
                || openAPI.getComponents() == null
                || openAPI.getComponents().getSchemas() == null) {

            return mapWithSchema;
        }

        for (Map.Entry<String, Schema> schema
                : openAPI.getComponents()
                .getSchemas()
                .entrySet()) {

            String schemaName =
                    schema.getKey();

            Schema schemaValue =
                    schema.getValue();

            if (schemaValue.getRequired() != null) {

                mapWithSchema.put(
                        schemaName,
                        schemaValue.getRequired()
                );
            }
        }

        return mapWithSchema;
    }

    /**
     * TODO:
     * Метод недоделан.
     * <p>
     * Частично собирает информацию
     * по моделям Swagger/OpenAPI:
     * <p>
     * - типы полей
     * - enum значения
     * - required поля
     */
    public void getAllDataFromSchema() {

        File specDir = new File("spec2");

        File[] files = specDir.listFiles();

        if (files == null) {
            return;
        }

        for (File fileSpec : files) {

            if (!fileSpec.isFile()) {
                continue;
            }

            String fileName =
                    fileSpec.getName();

            if (!fileName.matches("^.*\\.yaml$|^.*\\.yml$")) {
                continue;
            }

            Map<String, List<?>> mapEnum =
                    new HashMap<>();

            Map<String, Map<String, String>> mapElement =
                    new HashMap<>();

            OpenAPI openAPI =
                    new OpenAPIV3Parser()
                            .read(fileSpec.getPath());

            if (openAPI == null
                    || openAPI.getComponents() == null
                    || openAPI.getComponents().getSchemas() == null) {

                continue;
            }

            // collect enums
            for (Map.Entry<String, Schema> schema
                    : openAPI.getComponents()
                    .getSchemas()
                    .entrySet()) {

                Schema schemaValue =
                        schema.getValue();

                if (schemaValue.getEnum() != null) {

                    mapEnum.put(
                            schema.getKey(),
                            schemaValue.getEnum()
                    );
                }
            }

            // collect model fields
            for (Map.Entry<String, Schema> schema
                    : openAPI.getComponents()
                    .getSchemas()
                    .entrySet()) {

                Map<String, String> mapFields =
                        new HashMap<>();

                Schema schemaValue =
                        schema.getValue();

                if ("object".equals(schemaValue.getType())) {
                    log.info(schema.getKey() + schemaValue.getRequired() + "====================");

                    if (schemaValue.getProperties() != null) {

                        for (Object fieldObj
                                : schemaValue.getProperties()
                                .entrySet()) {

                            Map.Entry<String, Schema> field =
                                    (Map.Entry<String, Schema>) fieldObj;

                            String fieldName =
                                    field.getKey();

                            Schema fieldSchema =
                                    field.getValue();

                            // enum reference
                            if (fieldSchema.get$ref() != null) {

                                String enumName =
                                        fieldSchema.get$ref()
                                                .substring(
                                                        fieldSchema.get$ref()
                                                                .lastIndexOf("/") + 1
                                                );

                                mapFields.put(
                                        fieldName,
                                        String.valueOf(
                                                mapEnum.get(enumName)
                                        )
                                );

                            } else {

                                mapFields.put(
                                        fieldName,
                                        fieldSchema.getType()
                                );
                            }
                        }
                    }
                }

                mapElement.put(
                        schema.getKey(),
                        mapFields
                );
            }
            log.info("=================" + fileSpec + mapElement);
        }
    }
}