IP Geolocation REST API
=======================
![travis-ci](https://travis-ci.org/shopping24/geoip-dbip-rest-api.svg)

This project provides a simple web service which returns geolocation information for a given IP address. Geolocation
data is loaded from a [db-ip.com geoip database](https://db-ip.com/db/) file. The service requires the "full" database
(IP address to location + ISP).

The application is built as a docker image. The application by default expects to find the db-ip database file as
`/srv/dbip.csv.gz`. You can use a volume mount to mount the database file into the running container. If you mount the
file to a different location, you must also set the environment variable `DB_IP_FILE`.

## Example usage

    docker run -p 8080:8080 -v /path/to/dbip-full.csv.gz:/srv/dbip.csv.gz s24/geoip-api

## How to call the service

Simply make an HTTP GET request with the IP address:

    $ curl -i http://localhost:8080/80.85.196.23
    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=UTF-8
    
    { "address":"80.85.196.23",
      "country":"DE",
      "stateprov":"Hamburg",
      "city":"Hamburg",
      "latitude":"53.5986",
      "longitude":"10.0707",
      "isp":"OTTO GmbH & Co. KG",
      "organization":"Otto Versand Hamburg GmbH",
      "timezone":"Europe/Berlin",
      "cityDistrict":"Wandsbek",
      "timezoneOffset":"2" }

## Building the project

Build the container image by calling:

    $ mvn clean verify

## Contributing

We're looking forward to your comments, issues and pull requests!

## License

This project is licensed under the [Apache License, Version 2](http://www.apache.org/licenses/LICENSE-2.0.html).
