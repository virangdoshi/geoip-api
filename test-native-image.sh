#!/bin/bash
set -e

# clean up after test
function shutdown {
  docker rm -f geoip-native-test
}
trap shutdown EXIT

# launch docker
docker run -dp 18080:8080 --name geoip-native-test observabilitystack/geoip-api:native
sleep 1

# execute some curls
curl -fso /dev/null "http://localhost:18080/actuator"
curl -fso /dev/null "http://localhost:18080/actuator/health"
curl -fso /dev/null "http://localhost:18080/actuator/prometheus"
curl -fso /dev/null "http://localhost:18080/$(curl -s https://ifconfig.me/ip)"
curl -fso /dev/null "http://localhost:18080/8.8.4.4"
curl -fso /dev/null "http://localhost:18080/8.8.8.8"
curl -fso /dev/null "http://localhost:18080/206.80.238.253"
curl -fso /dev/null "http://localhost:18080/2.161.45.64"
curl -fso /dev/null "http://localhost:18080/5.4.55.34"
