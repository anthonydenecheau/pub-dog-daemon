FROM openjdk:8-jdk-alpine AS build-env
RUN  apk update && apk upgrade && apk add netcat-openbsd && apk add ca-certificates
RUN mkdir -p /usr/local/pub-dogdaemon
ADD @project.build.finalName@.jar /usr/local/pub-dogdaemon/

# GRPC Java not compatible with Alpine Linux
# Notes:
# les arguments au jar sont transmis par le docker-compose
# le test de d'accessibilité
FROM gcr.io/distroless/java
COPY --from=build-env /usr/local/pub-dogdaemon /usr/local/pub-dogdaemon
WORKDIR /usr/local/pub-dogdaemon
CMD ["@project.build.finalName@.jar"]
