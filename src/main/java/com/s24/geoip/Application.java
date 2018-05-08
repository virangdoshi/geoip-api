package com.s24.geoip;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public GeolocationProvider geolocationProvider(@Value("${DB_IP_FILE}") String dbIpFileName) {
        GeolocationIndex service = new GeolocationIndex();
        DbIpFileParser parser = new DbIpFileParser(new StringPool());
        try (InputStream fis = Files.newInputStream(Paths.get(dbIpFileName), StandardOpenOption.READ);
             InputStream gis = new GZIPInputStream(fis);
             Reader reader = new InputStreamReader(gis, StandardCharsets.UTF_8)) {

            logger.info("Loading IP geolocation data from {} ...", dbIpFileName);
            parser.parse(reader, service::add);
            logger.info("Loaded geolocation data for {} IP address ranges", service.numberOfEntries());
        } catch (Exception e) {
            logger.warn("Could not load file from path {}.", dbIpFileName, e);
        }

        return service;
    }
}
