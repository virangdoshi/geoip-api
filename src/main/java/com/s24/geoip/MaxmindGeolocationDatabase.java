package com.s24.geoip;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

/**
 * Implements {@code GeolocationProvider} by using the Maxmind GeoIP database.
 */
class MaxmindGeolocationDatabase implements GeolocationProvider {

    private final DatabaseReader reader;

    @Autowired
    MaxmindGeolocationDatabase(DatabaseReader reader) {
        this.reader = requireNonNull(reader);
    }

    @Override
    public GeoIpEntry lookup(InetAddress addr) {
        try {
            CityResponse response = reader.city(addr);

            // Null-safe conversions
            String country = response.getCountry() != null ? response.getCountry().getIsoCode() : null;
            String state = response.getMostSpecificSubdivision() != null
                    ? response.getMostSpecificSubdivision().getName()
                    : null;
            String city = response.getCity() != null ? response.getCity().getName() : null;
            String latitude = response.getLocation() != null
                    ? Objects.toString(response.getLocation().getLatitude(), null)
                    : null;
            String longitude = response.getLocation() != null
                    ? Objects.toString(response.getLocation().getLongitude(), null)
                    : null;
            String timezone = response.getLocation() != null ? response.getLocation().getTimeZone() : null;

            return new GeoIpEntry(country, state, city, latitude, longitude, timezone);
        } catch (AddressNotFoundException e) {
            return null;
        } catch (IOException | GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
    }
}
