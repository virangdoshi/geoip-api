# Geoip-API as central geoip lookup for Nginx

This example is suitable for larger scale environments, where you do not
want to use the native [Nginx Geoip module](http://nginx.org/en/docs/http/ngx_http_geoip_module.html). Using this example, you have to update the
geoip data only at a single location - by pulling a more recent Docker
image weekly.

## Launching the example

The Nginx can be queried on port `8080`. For every request, it will issue
an internal subrequest to the Geoip-API and enrich the request (to the
client or reverse proxy target) with geoip information in headers.

```
         ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─
               ┌─────────────┐                        │
         │     │  Geoip-API  │
               └──▲───────┬──┘                        │
         │        │       │
                  │       │                           │
   http  │        │       │           ┌─────────────┐
  (8080)       ┌──┴───────▼──┐        │Reverse proxy│ │
─────────┼────▶│    Nginx    │───────▶│   target    │
               └─────────────┘        │             │ │
         │                            └─────────────┘
          ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
```

Launch the Docker Compose example and start querying Geoip-API via Nginx:

```bash
$ docker-compose up
```

## Querying Nginx

Nginx uses the clients ip address for it's geoip lookup. As you are
querying on your machine, your ip address falls in the
[RFC1918](https://en.wikipedia.org/wiki/Private_network) range and
will not return any results.

Nginx is configured to recognize the `X-Forwarded-For` sent by reverse
proxies to contain the original client ip. We can use this behaviour
to query the Geoip-API using custom ip addresses, in this case our
public ip address:

```bash
$ curl -sI -H "X-Forwarded-For: $(curl -s https://ifconfig.me/ip)" "http://localhost:8080/"
HTTP/1.1 200 OK
Server: nginx/1.21.4
Content-Type: text/html
Connection: keep-alive
X-Geoip-Country: DE
X-Geoip-StateProv: Free and Hanseatic City of Hamburg
X-Geoip-City: Hamburg
X-Geoip-Latitude: 53.6042
X-Geoip-Longitude: 10.0596
X-Geoip-Continent: EU
X-Geoip-Timezone: Europe/Berlin
Accept-Ranges: bytes
```

## Querying Geoip-API using headers

Internally, the Geoip-API is queried by Nginx using this request:

```bash
$ curl -sI -H "X-Geoip-Address: $(curl -s https://ifconfig.me/ip)" "http://localhost:8081/"
HTTP/1.1 204
X-Geoip-Country: DE
X-Geoip-StateProv: Free and Hanseatic City of Hamburg
X-Geoip-City: Hamburg
X-Geoip-Latitude: 53.6042
X-Geoip-Longitude: 10.0596
X-Geoip-Continent: EU
X-Geoip-Timezone: Europe/Berlin
```
