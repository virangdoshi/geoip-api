FROM openjdk:12.0.2

EXPOSE 8080

COPY target/*.jar /opt/geoip-api.jar

HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health

CMD exec java ${JAVA_GC_OPTS} ${JAVA_OPTS} -jar /opt/geoip-api.jar
