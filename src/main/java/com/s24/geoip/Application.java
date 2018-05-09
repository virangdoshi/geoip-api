package com.s24.geoip;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public GeolocationProvider geolocationProvider(DatabaseReader reader) {
        return new MaxmindGeolocationDatabase(reader);
    }

    @Bean
    public DatabaseReader maxmindDatabaseReader(@Value("${DB_FILE}") String dbFileName) throws IOException {
        File file = new File(dbFileName);
        return new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
    }
}
