FROM openjdk:8-jre-alpine

COPY src/main/docker/sample.csv.gz /srv/dbip-sample.csv.gz
ENV DB_IP_FILE=/srv/dbip-sample.csv.gz

EXPOSE 8080

COPY target/dbip-api-0.0.1-SNAPSHOT.jar /opt/dbip-api.jar

CMD ["java", "-Xmx8G", "-jar", "/opt/dbip-api.jar"]
