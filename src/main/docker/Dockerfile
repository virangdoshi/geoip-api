FROM openjdk:8-jre-alpine

RUN apk add --no-cache curl

COPY dbip-sample.csv.gz /srv/dbip-sample.csv.gz
ENV DB_IP_FILE=/srv/dbip-sample.csv.gz

EXPOSE 8080

COPY target/dbip-api-${project.version}.jar /opt/dbip-api.jar

HEALTHCHECK --start-period=5m CMD curl -f http://localhost:8080/0.0.0.1

CMD ["java", "-Xmx8G", "-jar", "/opt/dbip-api.jar"]
