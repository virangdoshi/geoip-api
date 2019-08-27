package com.s24.geoip;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class GeoIpApi {

    private static final Logger logger = LoggerFactory.getLogger(GeoIpApi.class);

    public static void main(String[] args) {
        SpringApplication.run(GeoIpApi.class, args);
    }

    @Bean
    public GeolocationProvider cityProvider(
            @Autowired(required = false) @Qualifier("cityDatabaseReader") DatabaseReader cityDatabaseReader,
            @Autowired(required = false) @Qualifier("ispDatabaseReader") DatabaseReader ispDatabaseReader) {
        if (cityDatabaseReader == null && ispDatabaseReader == null) {
            throw new BeanInitializationException("Neither CITY_DB_FILE nor ISP_DB_FILE given");
        }
        return new MaxmindGeolocationDatabase(cityDatabaseReader, ispDatabaseReader);
    }

    @Bean(name = "cityDatabaseReader")
    @ConditionalOnProperty("CITY_DB_FILE")
    public DatabaseReader cityDatabaseReader(@Value("${CITY_DB_FILE}") String dbFileName) throws IOException {
        return buildDatabaseReader(dbFileName);
    }

    @Bean(name = "ispDatabaseReader")
    @ConditionalOnProperty("ISP_DB_FILE")
    public DatabaseReader ispDatabaseReader(@Value("${ISP_DB_FILE}") String dbFileName) throws IOException {
        return buildDatabaseReader(dbFileName);
    }

    private DatabaseReader buildDatabaseReader(String fileName) throws IOException {
        File file = new File(fileName);
        DatabaseReader bean = new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
        logger.info("Loaded database file {} (build date: {})", file, bean.getMetadata().getBuildDate());
        return bean;
    }
}
