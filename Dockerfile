# build stage
FROM maven:3.8.5-openjdk-17-slim AS builder
ARG VERSION=0-SNAPSHOT

ADD . /build
WORKDIR /build

# build image
RUN mvn --batch-mode --no-transfer-progress clean package \
        -DskipTests=true -Drevision=${VERSION} && \
    mv target/geoip-api-*.jar target/geoip-api.jar

# run stage
FROM openjdk:17-jdk-slim
ARG MAXMIND_LICENSE_KEY
ARG CREATED_AT
ARG VERSION
ARG GIT_REVISION

# Add labels to identify release
LABEL org.opencontainers.image.authors="Torsten B. Köster <tbk@thiswayup.de>" \
      org.opencontainers.image.url="https://github.com/observabilitystack/geoip-api" \
      org.opencontainers.image.licenses="Apache-2.0" \
      org.opencontainers.image.title="Geoip-API" \
      org.opencontainers.image.description="A JSON REST API for Maxmind GeoIP databases" \
      org.opencontainers.image.created="${CREATED_AT}" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.revision="${GIT_REVISION}"

# download current maxmind databases
WORKDIR /srv
RUN apt-get update && apt-get -y install curl && \
    curl -sfSL "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&suffix=tar.gz&license_key=${MAXMIND_LICENSE_KEY}" | tar -xz && \
    curl -sfSL "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-ASN&suffix=tar.gz&license_key=${MAXMIND_LICENSE_KEY}" | tar -xz && \
    ln -s GeoLite2-City_*/GeoLite2-City.mmdb . && \
    ln -s GeoLite2-ASN_*/GeoLite2-ASN.mmdb .

# place app
COPY --from=builder "/build/target/geoip-api.jar" /srv/geoip-api.jar

ENV CITY_DB_FILE /srv/GeoLite2-City.mmdb
ENV ASN_DB_FILE  /srv/GeoLite2-ASN.mmdb
HEALTHCHECK --interval=5s --timeout=1s CMD curl -f http://localhost:8080/actuator/health
EXPOSE 8080
CMD exec java -jar /srv/geoip-api.jar
