# ---- Build stage: Maven + Java 21 ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy sources and build
COPY . .
RUN mvn -q -DskipTests package

# ---- Runtime stage: slim JRE 21 ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# For container healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Copy fat jar from build stage
COPY --from=build /app/target/*.jar app.jar

# JVM tuning for containers
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]