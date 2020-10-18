#!/bin/bash

if [ "${TRAVIS_PULL_REQUEST_BRANCH:-$TRAVIS_BRANCH}" == "master" ]; then
    docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
    docker push observabilitystack/geoip-api:latest
    docker push observabilitystack/geoip-api:${TRAVIS_TAG}
else
    echo "Not on master branch, not pushing to Docker Hub ..."
fi