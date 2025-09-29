# Этап сборки
# Используем более универсальный тег для Maven с JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Собираем JAR без тестов
RUN mvn clean package -DskipTests

# Этап запуска
# Исправлено имя образа: используем надежный тег 17-jre-focal (самый маленький)
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
