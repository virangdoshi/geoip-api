package com.s24.geoip;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import com.google.common.primitives.Ints;

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

        if ((start instanceof Inet4Address && end instanceof Inet6Address)
                || (start instanceof Inet6Address && end instanceof Inet4Address)) {
            throw new IllegalArgumentException("start and end address must have the same IP version");
        }

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
