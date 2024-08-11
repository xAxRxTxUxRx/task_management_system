# Локальный запуск проекта с помощью Docker compose.

## 1. Получение исходного кода проекта.
`git clone https://github.com/xAxRxTxUxRx/task_management_system`

`cd task_management_system`

`git checkout prod`

## 2. Получение исполняемого файла и документации.
*Если на этом этапе возникают ошибки его можно пропустить

Проверьте есть ли у вас на устройстве mvn командой:

`mvn -version`

Если нет то нужно его установить для сборки проекта:
- Установка для Linux: sudo apt install mvn
- Установка для Widnows: https://phoenixnap.com/kb/install-maven-windows

С установленным mvn на устройстве: 

`mvn clean install -DskipTests`

Появятся новая папка.
Внутри папки ./target находится испольняемый файл проекта

Для создание документации проекта, воспользуйтесь командой:

`mvn javadoc:javadoc`

В ./docs находится документация сгенериронная Javadoc (index.html)

## 3 Получение docker image.
Для этого этапа должен быть установлен Docker.
- Если предыдущий этап прошел успешно:

`docker build -t artur0khram/task_management_system:latest .`

Если возникают ошибки:

Зарегистрируйтесь на Docker hub.

Войдите в аккаунт на устройстве:
`docker login`

`docker pull artur0khram/task_management_system:latest`

## 4. Запуск docker compose.
`docker-compose up --build`

## 5. Приложение запущено по адресу: http://localhost:8080

## Документация апи: http://localhost:8080/swagger-ui/index.html#/

# Список использованных технологий:
- Spring Boot: Version 3.3.1
- Java: Version 21
- MapStruct: Version 1.5.5.Final
- Lombok: Version 1.18.30
- PostgreSQL: Version 16.3
- Spring security: latest
- Spring web: latest
- Spring Boot Starter Mail: latest
- Spring Boot Starter Validation: latest
- Lombok: Version 1.18.30 (specified)
- MapStruct: Version 1.5.5.Final (specified)
- Lombok MapStruct Binding: Version 0.2.0
- JWT API: Version 0.12.3
- JWT Impl: Version 0.12.3
- JWT Jackson: Version 0.12.3
- Springdoc OpenAPI Starter Web MVC UI: Version 2.3.0
- Mockito Core: Version 4.1.0, test scope
- Maven Javadoc Plugin: Version 3.8.0
- Maven Compiler Plugin: Version 3.8.1
