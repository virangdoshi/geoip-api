package com.s24.geoip;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

/**
 * A geolocation database entry.
 */
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, isGetterVisibility = NONE)
@JsonInclude(NON_EMPTY)
public class GeoIpEntry {

    private final String country;
    private final String stateprov;
    private final String city;
    private final String latitude;
    private final String longitude;
    private final String timezone;

    public GeoIpEntry(String country, String stateprov, String city,
            String latitude, String longitude, String timezone) {
        this.country = country;
        this.stateprov = stateprov;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
    }

    public String getCountry() {
        return country;
    }

    public String getStateprov() {
        return stateprov;
    }

    public String getCity() {
        return city;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("country", country)
                .add("stateprov", stateprov)
                .add("city", city)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("timezone", timezone)
                .toString();
    }
}
