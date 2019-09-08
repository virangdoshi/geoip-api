# IP Geolocation REST API

![travis-ci](https://travis-ci.org/observabilitystack/geoip-api.svg?branch=master)
![docker-pulls](https://img.shields.io/docker/pulls/observabilitystack/geoip-api)
![apache license](https://img.shields.io/github/license/observabilitystack/geoip-api)

This project provides a simple REST web service which returns geolocation information for a given IP address. The service loads location information from [Maxminds GeoLite2](https://dev.maxmind.com/geoip/geoip2/geolite2/) or GeoIP2 City (commercial) database.

You can use this project in a microservice infrastructure if you have multiple services requesting geolocation data. This service can be used together with the [Logstash http filter plugin](https://www.elastic.co/guide/en/logstash/current/plugins-filters-http.html) to enrich log data.

## Running the container

The Docker image available on Docker Hub comes with a recent [GeoLite2 city database](https://dev.maxmind.com/geoip/geoip2/geolite2/). The container is built every week with a recent version of the database.

```
$ docker run -p 8080:8080 observabilitystack/geoip-api:latest
```

### Using a custom (commercial) database

When running in production, using a commercial [Maxmind GeoIP2 City database](https://www.maxmind.com/en/geoip2-city) is highly recommeded. You can mount the database in _mmdb_ format into the container. The location of the database can be customized using the following variables:

| Variable | Description | Default value |
| -------- | ----------- | ------------- |
| CITY_DB_FILE | The location of the GeoIP2 City or GeoLite2 database file. | `/srv/GeoLite2-City.mmdb` |
| ISP_DB_FILE | The location of the GeoIP2 ISP database file. | (none) |


## Using the API

When the container is running, you can query it via simple HTTP GET requests:

    curl http://localhost:8080/8.8.8.8
    {
        "country":"US",
        "latitude":"37.751",
        "longitude":"-97.822"
    }



## Contributing

We're looking forward to your comments, issues and pull requests!

## License

This project is licensed under the [Apache License, Version 2](http://www.apache.org/licenses/LICENSE-2.0.html).

This product includes GeoLite2 data created by MaxMind, available from
<a href="https://www.maxmind.com">https://www.maxmind.com</a>.
