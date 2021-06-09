package com.s24.geoip;

import java.io.File;
import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.properties.UnlessBuildProperty;
import io.quarkus.runtime.Quarkus;

public class GeoIpApiApplication{

    private static final Logger logger = LoggerFactory.getLogger(GeoIpApiApplication.class);

    public static void main(String[] args) {
        Quarkus.run(args);
    }

    @ApplicationScoped
    public GeolocationProvider cityProvider(
            DatabaseReader cityDatabaseReader,
            DatabaseReader ispDatabaseReader) {
        if (cityDatabaseReader == null && ispDatabaseReader == null) {
            throw new IllegalArgumentException("Neither CITY_DB_FILE nor ISP_DB_FILE given");
        }
        return new MaxmindGeolocationDatabase(cityDatabaseReader, ispDatabaseReader);
    }

    @UnlessBuildProperty(name = "CITY_DB_FILE", stringValue = "")
    @Produces
    @Named("cityDatabaseReader")
    public DatabaseReader cityDatabaseReader(@ConfigProperty(name = "CITY_DB_FILE") String dbFileName) throws IOException {
        return buildDatabaseReader(dbFileName);
    }

    @UnlessBuildProperty(name = "ISP_DB_FILE", stringValue = "")
    @Produces
    @Named("ispDatabaseReader")
    public DatabaseReader ispDatabaseReader(@ConfigProperty(name = "ISP_DB_FILE") String dbFileName) throws IOException {
        return buildDatabaseReader(dbFileName);
    }

    private DatabaseReader buildDatabaseReader(String fileName) throws IOException {
        File file = new File(fileName);
        DatabaseReader bean = new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
        logger.info("Loaded database file {} (build date: {})", file, bean.getMetadata().getBuildDate());
        return bean;
    }
}
