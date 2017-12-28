# IP Geolocation REST API
![travis-ci](https://travis-ci.org/shopping24/geoip-dbip-rest-api.svg)

This project provides a simple web service which returns geolocation information for a given IP address.

Geolocation data is loaded from a [db-ip.com geoip database](https://db-ip.com/db/) file. The service requires the
"full" database (IP address to location + ISP), which you need to purchase from db-ip. The database file is loaded from
the file set in the environment variable `DB_IP_FILE`, `/srv/dbip.csv.gz` by default. You can use a volume mount to
mount the database file into the running container.

To test the image, you can run it without a mount. The image includes a sample file with a single entry for the IPv4
address range 0.0.0.0/8.

## Running the container

    docker run -p 8080:8080 \
        -v /path/to/dbip-full.csv.gz:/srv/dbip.csv.gz \
        shopping24/geoip-api

## Using the API

When the container is running, you can query it via simple HTTP GET requests:

    curl http://localhost:8080/0.0.0.1
    {
      "country": "ZZ",
      "latitude": "0",
      "longitude": "0",
      "isp": "Current network",
      "organization": "RFC 6890"
    }


## Building the project

Build the container image by calling:

    mvn clean verify

## Contributing

We're looking forward to your comments, issues and pull requests!

## License

This project is licensed under the [Apache License, Version 2](http://www.apache.org/licenses/LICENSE-2.0.html).
