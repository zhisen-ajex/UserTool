FROM openjdk:17-alpine
#Install curl
RUN  apk add curl
WORKDIR /workdir/
COPY out/libs /workdir/java/libs
COPY src/main/resources/ /workdir/java/configs/
COPY out/verify-gke-sql.jar /workdir/java/verify-gke-sql.jar
ENTRYPOINT ["sh","-c","java  -jar /workdir/java/verify-gke-sql.jar"]