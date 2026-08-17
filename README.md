# Shawen Autotest Generator

Gradle-плагин для генерации Java-клиентов, моделей и вспомогательной инфраструктуры автотестов из OpenAPI / Swagger спецификаций.

---

## Возможности

Плагин автоматически:

* генерирует Java API-клиенты;
* генерирует модели запросов и ответов;
* подключает кастомные Mustache-шаблоны;
* создает util-классы для работы со сгенерированным кодом;
* создает helper-классы;
* добавляет Spring Bean в тестовую конфигурацию;
* исправляет проблемы именования файлов после генерации OpenAPI Generator;
* удаляет deprecated методы из спецификации;
* удаляет RabbitMQ-теги из спецификации;
* создает отдельную Gradle task для каждой Swagger/OpenAPI спецификации.

---

# Подключение плагина

## Вариант через buildscript

```gradle
buildscript {

    dependencies {
        classpath "sw.autotest.generator:shawen-autotest-generator:1.0-SNAPSHOT"
    }
}

apply plugin: "sw.autotest.generator"
```

---

# Структура проекта

Swagger/OpenAPI спецификации должны находиться в директории:

```text
spec/
```

Например:

```text
spec/
 ├─ account-service.yaml
 ├─ payment-service.yaml
 └─ notification-service.yaml
```

---

# Настройка

В `build.gradle` необходимо добавить блок:

```gradle
generator {

    specToGenerate = "spec"

    utils = true

    testConfig =
            "/src/main/java/config/TestConfig.java"

    helpers =
            "src/main/java/helpers"

    deleteRabbitMq = true

    deleteDeprecatedMethod = true

    templateDir =
            "$rootDir/src/main/resources/templates"
}
```

---

# Параметры конфигурации

## specToGenerate

Путь к директории со Swagger/OpenAPI спецификациями.

```gradle
specToGenerate = "spec"
```

---

## utils

Генерация util-классов для работы со сгенерированными клиентами и моделями.

```gradle
utils = true
```

---

## testConfig

Путь до Spring-конфигурации, в которую будут автоматически добавлены Bean'ы для клиентов.

```gradle
testConfig =
        "/src/main/java/config/TestConfig.java"
```

Если параметр не указан, вместо Bean создаются helper-классы.

---

## helpers

Путь для генерации helper-классов.

```gradle
helpers =
        "src/main/java/helpers"
```

---

## deleteRabbitMq

Удаление RabbitMQ тегов из Swagger спецификации перед генерацией.

```gradle
deleteRabbitMq = true
```

---

## deleteDeprecatedMethod

Удаление deprecated методов из Swagger спецификации перед генерацией.

```gradle
deleteDeprecatedMethod = true
```

---

## templateDir

Путь до кастомных Mustache-шаблонов.

```gradle
templateDir =
        "$rootDir/src/main/resources/templates"
```

Если параметр не указан, OpenAPI Generator использует встроенные шаблоны.

---

# Генерация клиентов

Для каждой Swagger спецификации автоматически создается отдельная Gradle task.

Например:

```text
spec/account-service.yaml
```

создаст задачу:

```bash
generateAccountService
```

Запуск:

```bash
./gradlew generateAccountService
```

---

# Результат генерации

Для файла:

```text
account-service.yaml
```

будут созданы пакеты:

```text
sw.generator.client.accountservice.api
sw.generator.client.accountservice.model
```

---

# Использование кастомных шаблонов

Плагин поддерживает полную замену стандартных шаблонов OpenAPI Generator.

Пример структуры:

```text
templates/
 ├─ api.mustache
 ├─ model.mustache
 ├─ ApiClient.mustache
 ├─ JSON.mustache
 └─ ...
```

Подключение:

```gradle
templateDir =
        "$rootDir/src/main/resources/templates"
```

---

# Постобработка файлов

После завершения OpenAPI Generator выполняется дополнительная обработка.

## Исправление имен файлов

OpenAPI Generator может создавать файлы:

```java
ActuatorApi.java
```

при этом внутри класса находится:

```java
public class AccountserviceActuatorApi
```

Плагин автоматически:

1. получает фактическое имя класса;
2. переименовывает файл;
3. устраняет случаи вида:

```java
AccountserviceUserApiApi
```

в

```java
AccountserviceUserApi
```

В результате имя файла всегда соответствует имени класса.

---

# Генерация Bean

Если указан параметр:

```gradle
testConfig
```

плагин автоматически добавляет Bean в указанный Spring-конфиг.

Пример:

```java
@Bean
public AccountserviceUserApi accountserviceUserApi() {
    ...
}
```

---

# Генерация Helper-классов

Если указан параметр:

```gradle
helpers
```

создаются дополнительные helper-классы для упрощения работы со сгенерированными клиентами.

---

# Архитектура плагина

```text
SwPlugin
 │
 ├── Download
 │
 ├── OpenApiConfig
 │
 ├── DoFirstInCreationTask
 │
 └── DoLastInCreationTask
      │
      ├── RenameGenerateFiles
      ├── CreateUtilClassForGeneratedModelAndClient
      ├── CreateBeanInTestConfig
      └── CreateHelpersClass
```

---

# Требования

* Java 17+
* Gradle 7+
* OpenAPI Specification 3.x

---

# Используемые технологии

* Gradle Plugin API
* OpenAPI Generator
* Swagger Parser
* Java 17
* SLF4J

---

# Что делает плагин автоматически

✅ Генерирует клиентов из Swagger/OpenAPI

✅ Генерирует модели

✅ Поддерживает кастомные шаблоны

✅ Генерирует util-классы

✅ Генерирует helper-классы

✅ Добавляет Bean в Spring-конфиг

✅ Удаляет deprecated методы

✅ Удаляет RabbitMQ-теги

✅ Исправляет проблемы именования файлов OpenAPI Generator

✅ Создает отдельную задачу генерации для каждой спецификации
