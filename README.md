# IP Geolocation REST API

![travis-ci](https://travis-ci.org/observabilitystack/geoip-api.svg?branch=master)
![docker-pulls](https://img.shields.io/docker/pulls/observabilitystack/geoip-api)

This project provides a simple REST web service which returns geolocation information for a given IP address. The service loads location information from [Maxminds GeoLite2](https://dev.maxmind.com/geoip/geoip2/geolite2/) or GeoIP2 City (commercial) database.

You can use this project in a microservice infrastructure if you have multiple services requesting geolocation data. This service can be used together with the [Logstash http filter plugin](https://www.elastic.co/guide/en/logstash/current/plugins-filters-http.html) to enrich log data.

## Running the container

```
$ docker run -p 8080:8080 observabilitystack/geoip-api:latest
```

### Using a custom (commercial) database

Using a commercial [Maxmind GeoIP2 City]

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
