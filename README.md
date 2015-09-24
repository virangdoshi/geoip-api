db-ip JSON REST api
========================
![travis-ci](https://travis-ci.org/shopping24/geoip-dbip-rest-api.svg)

This project leverages a simple Java webapp that acts as a wrapper around the [db-ip.com geoip databases](https://db-ip.com/db/).
The database is available at different levels of details (depends on how muc you spend). The basic databases are available
for free. Compared to other geoip services we considered the [db-ip.com geoip databases](https://db-ip.com/db/) very accurate
and complete.

## Use this project

Drop the war into you favorite servlet container (like Tomcat). The REST interface
is straightforward, just put the ip to query data for in the path:

    $ curl -i http://localhost:8080/dbip-api/80.85.196.23
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

To configure the location of the db-ip database, pass along the java
system property `-Ddbip-path=` or configure the JNDI environment 
key `dbip-path`.

The response above is a response rendered with the full dp-ip database. Use 
`curl -i http://localhost:8080/dbip-api/ping` as availability monitoring endpoint.

### ØMQ bindings

The project offers ØMQ bindings as well. By default, ØMQ binds to `tcp://*:19000`.
You can configure the binding by passing the java system property `-Dzmq-binding`
or configuring the JNDI environment key `zmq-binding`.

## Building the project

This should install the current version into your local repository

    $ mvn clean verify
    
### Releasing the project to maven central
    
Define new versions
    
    $ export NEXT_VERSION=<version>
    $ export NEXT_DEVELOPMENT_VERSION=<version>-SNAPSHOT

Then execute the release chain

    $ mvn org.codehaus.mojo:versions-maven-plugin:2.0:set -DgenerateBackupPoms=false -DnewVersion=$NEXT_VERSION
    $ git commit -a -m "pushes to release version $NEXT_VERSION"
    $ mvn -P release
    
Then, increment to next development version:
    
    $ git tag -a v$NEXT_VERSION -m "`curl -s http://whatthecommit.com/index.txt`"
    $ mvn org.codehaus.mojo:versions-maven-plugin:2.0:set -DgenerateBackupPoms=false -DnewVersion=$NEXT_DEVELOPMENT_VERSION
    $ git commit -a -m "pushes to development version $NEXT_DEVELOPMENT_VERSION"
    $ git push origin tag v$NEXT_VERSION && git push origin

## Contributing

We're looking forward to your comments, issues and pull requests!

## License

This project is licensed under the [Apache License, Version 2](http://www.apache.org/licenses/LICENSE-2.0.html).
