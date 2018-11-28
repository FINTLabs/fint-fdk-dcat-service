FROM gradle:4.8.0-jdk8-alpine as builder
USER root
COPY . .
RUN gradle --no-daemon build

FROM openjdk:8-jre-alpine
COPY --from=builder /home/gradle/build/deps/external/*.jar /data/
COPY --from=builder /home/gradle/build/deps/fint/*.jar /data/
COPY --from=builder /home/gradle/build/libs/fint-fdk-dcat-service-*.jar /data/fint-fdk-dcat-service.jar
ENTRYPOINT ["java", "-jar", "/data/fint-fdk-dcat-service.jar"]
