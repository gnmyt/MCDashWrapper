FROM maven:3.8.7-openjdk-18-slim AS build
WORKDIR /app

RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs
COPY pom.xml .

COPY src ./src
COPY webui ./webui

RUN mvn compile assembly:single

FROM openjdk:18-slim
WORKDIR /app

COPY --from=build /app/target/MCDash-Wrapper.jar ./server.jar

EXPOSE 7865

CMD ["java", "-jar", "server.jar"]