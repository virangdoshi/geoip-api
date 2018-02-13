package com.s24.geoip;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

/**
 * A geolocation database entry.
 */
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, isGetterVisibility = NONE)
@JsonInclude(NON_EMPTY)
public class GeoIpEntry {

    @JsonIgnore
    private final InetAddress start;
    @JsonIgnore
    private final InetAddress end;

    private final String country;
    private final String stateprov;
    private final String city;
    private final String cityDistrict;
    private final String latitude;
    private final String longitude;
    private final String isp;
    private final String timezoneOffset;
    private final String timezone;
    private final String connection;
    private final String organization;

    public GeoIpEntry(InetAddress start, InetAddress end, String country, String stateprov, String city,
            String cityDistrict,
            String latitude, String longitude, String timezoneOffset, String timezone, String isp,
            String connection,
            String organization) {
        checkNotNull(start, "Pre-condition violated: start must not be null.");
        checkNotNull(end, "Pre-condition violated: end must not be null.");
        checkNotNull(country, "Pre-condition violated: country must not be null.");

        if ((start instanceof Inet4Address && end instanceof Inet6Address)
                || (start instanceof Inet6Address && end instanceof Inet4Address)) {
            throw new IllegalArgumentException("start and end address must have the same IP version");
        }

        this.start = start;
        this.end = end;
        this.country = country;
        this.stateprov = stateprov;
        this.city = city;
        this.cityDistrict = cityDistrict;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.timezoneOffset = timezoneOffset;
        this.isp = Strings.emptyToNull(isp);
        this.connection = Strings.emptyToNull(connection);
        this.organization = Strings.emptyToNull(organization);
    }

    public boolean isInRange(InetAddress i) {
        checkNotNull(i, "Pre-condition violated: i must not be null.");

        if (isIpv6()) {
            if (!(i instanceof Inet6Address)) {
                return false;
            }
            // Assumes that all IPv6 ranges are at least /64 ranges
            long start = ByteBuffer.wrap(this.start.getAddress(), 0, 8).getLong();
            long end = ByteBuffer.wrap(this.end.getAddress(), 0, 8).getLong();
            long address = ByteBuffer.wrap(i.getAddress(), 0, 8).getLong();
            return address >= start && address <= end;
        } else {
            if (!(i instanceof Inet4Address)) {
                return false;
            }
            int start = Ints.fromByteArray(this.start.getAddress());
            int end = Ints.fromByteArray(this.end.getAddress());
            int address = Ints.fromByteArray(i.getAddress());
            return address >= start && address <= end;
        }
    }

    public boolean isIpv6() {
        return start instanceof Inet6Address;
    }

    public InetAddress getStart() {
        return start;
    }

    public InetAddress getEnd() {
        return end;
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

    public String getIsp() {
        return isp;
    }

    public String getConnection() {
        return connection;
    }

    public String getOrganization() {
        return organization;
    }

    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getCityDistrict() {
        return cityDistrict;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("start", start)
                .add("end", end)
                .add("country", country)
                .add("stateprov", stateprov)
                .add("city", city)
                .add("cityDistrict", cityDistrict)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("isp", isp)
                .add("timezoneOffset", timezoneOffset)
                .add("timezone", timezone)
                .add("connection", connection)
                .add("organization", organization)
                .toString();
    }
}
