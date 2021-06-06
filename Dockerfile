# download stage
ARG MAXMIND_LICENSE_KEY
ARG LOCAL_REGISTRY
FROM debian:buster-slim AS downloader

WORKDIR /srv
RUN apt-get update && \
    apt-get -y install curl && \
    curl -sfSL "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&suffix=tar.gz&license_key=${MAXMIND_LICENSE_KEY}" | tar -xz && \
    mv GeoLite2-City_*/GeoLite2-City.mmdb /srv/GeoLite2-City.mmdb

# Extend existing native image
FROM ${LOCAL_REGISTRY}/observabilitystack/geoip-api:0-SNAPSHOT
COPY --from=downloader "/srv/GeoLite2-City.mmdb" /srv/GeoLite2-City.mmdb
ENV CITY_DB_FILE /srv/GeoLite2-City.mmdb
