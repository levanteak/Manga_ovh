# ---- Stage 1: Build ----
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Копируем Maven wrapper и pom.xml отдельно — кешируем зависимости
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Даём права на выполнение mvnw
RUN chmod +x mvnw

# Скачиваем зависимости отдельным слоем (кеш Docker)
RUN ./mvnw dependency:go-offline -q

# Копируем исходники и собираем JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests -q

# ---- Stage 2: Run ----
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
