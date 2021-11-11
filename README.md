# IP Geolocation REST API

[![geoip-api ci build](https://github.com/observabilitystack/geoip-api/actions/workflows/ci.yaml/badge.svg?branch=master)](https://github.com/observabilitystack/geoip-api/actions/workflows/ci.yaml)
[![docker-pulls](https://img.shields.io/docker/pulls/observabilitystack/geoip-api)](https://hub.docker.com/r/observabilitystack/geoip-api)
![GitHub Release Date](https://img.shields.io/github/release-date/observabilitystack/geoip-api)
![GitHub last commit](https://img.shields.io/github/last-commit/observabilitystack/geoip-api)
![apache license](https://img.shields.io/github/license/observabilitystack/geoip-api)

> ♻️ This is the official and maintained fork of the original [@shopping24](https://github.com/shopping24) repository maintained by [@tboeghk](https://thiswayup.de).

This project provides a simple REST web service which returns geolocation information for a given IP address. The service loads location information from [Maxminds GeoLite2](https://dev.maxmind.com/geoip/geoip2/geolite2/) or GeoIP2 City (commercial) database.

You can use this project in a microservice infrastructure if you have multiple services requesting geolocation data. This service can be used together with the [Logstash http filter plugin](https://www.elastic.co/guide/en/logstash/current/plugins-filters-http.html) to enrich log data.

* [Running the container](#running-the-container)
* [Using the API](#using-the-api)
* [Kubernetes & Docker-Compose Examples](#examples)

## Running the container

The Docker image available on Docker Hub comes bundled with a recent [GeoLite2 city database](https://dev.maxmind.com/geoip/geoip2/geolite2/). The container is built every week with a recent version of the database.

```
$ docker run -p 8080:8080 ghcr.io/observabilitystack/geoip-api:latest
```

```
$ docker run -p 8080:8080 observabilitystack/geoip-api:latest
```

### Available tags & repositories

> 💡 Although running containers tagged as _latest_ is
> not recommended in production, for geoip-api we highly
> recommend this to have the most up-to-data geoip
> data.

* Images are available on both [Docker Hub](https://hub.docker.com/repository/docker/observabilitystack/geoip-api) and [GitHub Container Registry](https://github.com/observabilitystack/geoip-api/pkgs/container/geoip-api).
* The images are tagged in `yyyy-VV` (year & week number) format.
* The most recent container is tagged as `latest`.
* Images are available for `amd64` and `arm64` architectures.
* Updates (data & code) are released weekly.

#### Experimental `native` project build

For faster startup times, you can use the `native` build. This
version of the project is not bundled with a JDK. The Java code
is compiled into native code providing faster launch times
(`5ms over 1.9s`) and better overall performance and smaller
resource footprint.

```
$ docker run -p 8080:8080 ghcr.io/observabilitystack/geoip-api:latest-native
```

> 💡 The native build will be the default build for this project
> in 2022!

### Using a custom (commercial) database

> ☝️ When running in production, using a commercial [Maxmind GeoIP2 City database](https://www.maxmind.com/en/geoip2-city) is highly recommeded due to it's increased
precision and general data quality.

You can mount the database in _mmdb_ format into the container. The location of the database can be customized using the following
variables:

| Variable | Description | Default value |
| -------- | ----------- | ------------- |
| CITY_DB_FILE | The location of the GeoIP2 City or GeoLite2 database file. | `/srv/GeoLite2-City.mmdb` |
| ASN_DB_FILE | The location of the GeoIP2 ASN database file. | `/srv/GeoLite2-ASN.mmdb` |
| ISP_DB_FILE | The location of the GeoIP2 ISP database file. | (none) |

### Examples

The [`examples`](examples/) folder contains examples how
to run _geoip-api_ in Docker-Compose or Kubernetes.

## Using the API

You can query the API via simple HTTP GET requests:

```bash
$ curl -s http://localhost:8080/8.8.8.8
{
  "country": "US",
  "latitude": "37.751",
  "longitude": "-97.822",
  "continent": "NA",
  "timezone": "America/Chicago",
  "asn": 15169,
  "asnOrganization": "GOOGLE"
}
$ curl -s "http://localhost:8080/$(curl -s https://ifconfig.me/ip)"
{
  "country": "DE",
  "stateprov": "Free and Hanseatic City of Hamburg",
  "stateprovCode": "HH",
  "city": "Hamburg",
  "latitude": "53.5742",
  "longitude": "10.0497",
  "continent": "EU",
  "timezone": "Europe/Berlin",
  "asn": 15943,
  "asnOrganization": "wilhelm.tel GmbH"
}
```

## Building the project

The project is built through multi stage Docker builds. You need
a valid Maxmind lincense key to download the Geoip2 database.

```shell
$ export MAXMIND_LICENSE_KEY=...
$ docker build \
    --build-arg MAXMIND_LICENSE_KEY=${MAXMIND_LICENSE_KEY} \
    -t geoip-api:latest .
$ docker build \
    --build-arg MAXMIND_LICENSE_KEY=${MAXMIND_LICENSE_KEY} \
    -t geoip-api:latest-native -f Dockerfile.native .
```

If you want to build (or test) a multi-platform build, use
the [Docker Buildx extension](https://docs.docker.com/buildx/working-with-buildx/):

```shell
$ docker buildx create --use --name multi-platform
$ docker buildx build \
    --platform linux/amd64,linux/arm64 \
    --build-arg MAXMIND_LICENSE_KEY=${MAXMIND_LICENSE_KEY} \
    -t geoip-api:latest-native -f Dockerfile.native .
```

## Contributing

We're looking forward to your comments, issues and pull requests!

## License

This project is licensed under the [Apache License, Version 2](http://www.apache.org/licenses/LICENSE-2.0.html).

This product includes GeoLite2 data created by MaxMind, available from
<a href="https://www.maxmind.com">https://www.maxmind.com</a>.
