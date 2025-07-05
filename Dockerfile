# Stage 1: Build the fat JAR using JDK 21
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Install findutils (provides xargs)
RUN apt-get update && \
    apt-get install -y --no-install-recommends findutils && \
    rm -rf /var/lib/apt/lists/*

COPY gradlew gradlew
COPY gradle gradle
COPY . .

RUN chmod +x gradlew && \
    ./gradlew clean shadowJar -x test

# Stage 2: Runtime environment
FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app

# Install minimal utilities
RUN apt-get update && \
    apt-get install -y --no-install-recommends bash curl findutils && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["bash", "-c", "if [ \"$DEBUG\" = \"1\" ]; then exec bash; else exec java -jar /app/app.jar; fi"]

EXPOSE 8080
