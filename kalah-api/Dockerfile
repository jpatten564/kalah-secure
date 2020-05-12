FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dkeycloak.auth-server-url=http://${KEYCLOAK_HOST}:${KEYCLOAK_PORT}/auth/", "/app.jar"]
