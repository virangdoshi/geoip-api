# IP Geolocation REST API
![travis-ci](https://travis-ci.org/shopping24/geoip-dbip-rest-api.svg)

This project provides a simple web service which returns geolocation information for a given IP address.

Geolocation data is loaded from the GeoIP2 City database (commercial) or
[GeoLite2](https://dev.maxmind.com/geoip/geoip2/geolite2/) database by Maxmind. The database file is not included in
this project. When running this service as a docker container, mount the database file as `/srv/GeoLite2-City.mmdb`
(or, if you mount the file at a different location inside the container, set the environment variable `DB_FILE` to the
location).

## Running the container

    docker run -p 8080:8080 \
        -v /path/to/GeoLite2-City.mmdb:/srv/GeoLite2-City.mmdb \
        shopping24/geoip-api

## Using the API

When the container is running, you can query it via simple HTTP GET requests:

    curl http://localhost:8080/8.8.8.8
    {
        "country":"US",
        "latitude":"37.751",
        "longitude":"-97.822"
    }

## Building the project

Build the container image by calling:

    mvn clean verify

## Contributing

We're looking forward to your comments, issues and pull requests!

## License

This project is licensed under the [Apache License, Version 2](http://www.apache.org/licenses/LICENSE-2.0.html).
