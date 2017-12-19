package com.s24.geoip;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.Inet6Address;
import java.net.InetAddress;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;

public class GeoIpEntry {

    private final InetAddress start, end;
    private final String country, region, city, cityDistrict, latitude, longitude, ispName, timezoneOffset,
            timezoneName,
            connectionType, organizationName;

    public GeoIpEntry(InetAddress start, InetAddress end, String country, String region, String city,
            String cityDistrict,
            String latitude, String longitude, String timezoneOffset, String timezoneName, String ispName,
            String connectionType,
            String organizationName) {
        checkNotNull(start, "Pre-condition violated: start must not be null.");
        checkNotNull(end, "Pre-condition violated: end must not be null.");
        checkNotNull(country, "Pre-condition violated: country must not be null.");

        this.start = start;
        this.end = end;
        this.country = country;
        this.region = region;
        this.city = city;
        this.cityDistrict = cityDistrict;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezoneName = timezoneName;
        this.timezoneOffset = timezoneOffset;
        this.ispName = Strings.emptyToNull(ispName);
        this.connectionType = Strings.emptyToNull(connectionType);
        this.organizationName = Strings.emptyToNull(organizationName);
    }

    public boolean isInRange(InetAddress i) {
        checkNotNull(i, "Pre-condition violated: i must not be null.");

        int ord = InetAddresses.coerceToInteger(i);

        int startAsInt = InetAddresses.coerceToInteger(start);
        int endAsInt = InetAddresses.coerceToInteger(end);

        return startAsInt <= endAsInt ? ord >= startAsInt && ord <= endAsInt : ord <= startAsInt && ord >= endAsInt;
    }

    public boolean isIpv6() {
        return start instanceof Inet6Address;
    }

    public Integer getCoercedStart() {
        return InetAddresses.coerceToInteger(start);
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

    public String getRegion() {
        return region;
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

    public String getIspName() {
        return ispName;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    public String getTimezoneName() {
        return timezoneName;
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
                .add("region", region)
                .add("city", city)
                .add("cityDistrict", cityDistrict)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("ispName", ispName)
                .add("timezoneOffset", timezoneOffset)
                .add("timezoneName", timezoneName)
                .add("connectionType", connectionType)
                .add("organizationName", organizationName)
                .toString();
    }

}
