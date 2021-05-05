FROM openjdk:11.0.7-jre-slim

# setting up non-root user
RUN addgroup spring && adduser --ingroup spring --disabled-password spring
USER spring

# JAR_FILE is configured with maven plugin
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]