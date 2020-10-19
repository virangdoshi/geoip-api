#!/bin/bash
set -e

# clean up after test
function shutdown {
  echo "Shutting down geoip-native-test"
  docker rm -f geoip-native-test
}
trap shutdown EXIT

# launch docker
echo "Launching observabilitystack/geoip-api:latest as geoip-native-test"
docker run -dp 18080:8080 --name geoip-native-test observabilitystack/geoip-api:latest
sleep 1

# execute some curls
function test_url {
  echo "Testing ${1}"
  curl -fso /dev/null "${1}"
}

test_url "http://localhost:18080/actuator"
test_url "http://localhost:18080/actuator"
test_url "http://localhost:18080/actuator/health"
test_url "http://localhost:18080/actuator/prometheus"
test_url "http://localhost:18080/$(curl -s https://ifconfig.me/ip)"
test_url "http://localhost:18080/8.8.4.4"
test_url "http://localhost:18080/8.8.8.8"
test_url "http://localhost:18080/206.80.238.253"
test_url "http://localhost:18080/2.161.45.64"
test_url "http://localhost:18080/5.4.55.34"
