FROM openjdk:12.0.2

ENV CITY_DB_FILE /srv/GeoLite2-City.mmdb

# download current maxmind databases
WORKDIR /srv
RUN curl -sSL https://geolite.maxmind.com/download/geoip/database/GeoLite2-City.tar.gz | tar -xz && \
    ln -s GeoLite2-City_*/GeoLite2-City.mmdb .

# place app 
COPY target/*.jar /opt/geoip-api.jar

HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health
EXPOSE 8080
CMD exec java ${JAVA_GC_OPTS} ${JAVA_OPTS} -jar /opt/geoip-api.jar
