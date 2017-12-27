#!/bin/sh

set -e

error() {
    if [ -n "$1" ]; then
        echo "$1" 1>&2
    fi
    exit 1
}

VERSION=$1

if [ -z "${VERSION}" ]; then
    error "You must specify the version as a parameter to this script"
fi

if [ -n "$(git status --porcelain)" ]; then
    error "There are uncommitted local changes, commit them before making a release"
fi

# Make sure that we have all tags from remote (to detect duplicates)
git fetch --tags

if [ $(git rev-parse -q --verify "refs/tags/v${VERSION}") ]; then
    error "A git tag for version ${VERSION} already exists"
fi

# Build & publish the release
mvn "-Drevision=${VERSION}" clean package scm:tag
docker push "shopping24/geoip-api:latest"
docker push "shopping24/geoip-api:${VERSION}"
