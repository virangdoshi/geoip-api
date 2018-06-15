package com.s24.geoip;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.IspResponse;

/**
 * Implements {@code GeolocationProvider} by using the Maxmind GeoIP database.
 */
final class MaxmindGeolocationDatabase implements GeolocationProvider {

    private final DatabaseReader cityDatabaseReader;
    private final DatabaseReader ispDatabaseReader;

    MaxmindGeolocationDatabase(DatabaseReader cityDatabaseReader, DatabaseReader ispDatabaseReader) {
        if (cityDatabaseReader == null && ispDatabaseReader == null) {
            throw new NullPointerException("At least one of cityDatabaseReader or ispDatabaseReader must be non-null");
        }
        this.cityDatabaseReader = cityDatabaseReader;
        this.ispDatabaseReader = ispDatabaseReader;
    }

    @Override
    public GeoIpEntry lookup(InetAddress addr) {
        GeoIpEntry.Builder responseBuilder = new GeoIpEntry.Builder();
        // boolean logical or in next line, not conditional or (always run both methods)
        boolean hasData = lookupCityData(addr, responseBuilder) | lookupIspData(addr, responseBuilder);
        return hasData ? responseBuilder.build() : null;
    }

    private boolean lookupCityData(InetAddress addr, GeoIpEntry.Builder builder) {
        if (cityDatabaseReader == null) {
            return false;
        }
        try {
            CityResponse response = cityDatabaseReader.city(addr);

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

            builder.setCountry(country)
                    .setStateprov(state)
                    .setCity(city)
                    .setLatitude(latitude)
                    .setLongitude(longitude)
                    .setTimezone(timezone);
            return true;

        } catch (AddressNotFoundException e) {
            // no city information found, this is not an error
            return false;
        } catch (IOException | GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean lookupIspData(InetAddress addr, GeoIpEntry.Builder builder) {
        if (ispDatabaseReader == null) {
            return false;
        }
        try {
            IspResponse response = ispDatabaseReader.isp(addr);

            builder.setIsp(response.getIsp())
                    .setOrganization(response.getOrganization())
                    .setAsn(response.getAutonomousSystemNumber())
                    .setAsnOrganization(response.getAutonomousSystemOrganization());
            return true;

        } catch (AddressNotFoundException e) {
            // no ISP information found, this is not an error
            return false;
        } catch (IOException | GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
    }
}
