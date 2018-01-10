FROM openjdk:8-jre-alpine

RUN apk add --no-cache curl

COPY dbip-sample.csv.gz /srv/dbip.csv.gz
ENV DB_IP_FILE="/srv/dbip.csv.gz" \
    JAVA_GC_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -XX:+UseG1GC" \
    JAVA_HEAP="8G" \
    JAVA_OPTS=""

EXPOSE 8080

COPY target/geoip-api.jar /opt/geoip-api.jar

HEALTHCHECK --start-period=5m CMD curl -f http://localhost:8080/0.0.0.1

CMD exec java "-Xmx${JAVA_HEAP}" ${JAVA_GC_OPTS} ${JAVA_OPTS} -jar /opt/geoip-api.jar
