package com.s24.geoip;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.primitives.Ints;

/**
 * Maps IP addresses to geolocation information.
 */
public class GeolocationIndex {

    private final TreeMap<Integer, GeoIpEntry> entries;
    private final TreeMap<Long, GeoIpEntry> ipv6entries;

    /**
     * Initializes a new, empty index.
     */
    public GeolocationIndex() {
        this.entries = new TreeMap<>();
        this.ipv6entries = new TreeMap<>();
    }

    /**
     * Returns the geolocation information for the given IP address, or {@code null} if no information is found for the
     * address.
     */
    public GeoIpEntry lookup(InetAddress inet) {
        checkNotNull(inet, "Pre-condition violated: inet must not be null.");

        Entry<?, GeoIpEntry> candidate;

        // find and check candiate
        if (inet instanceof Inet4Address) {
            candidate = entries.floorEntry(ipv4AddressToInt(inet));
        } else {
            candidate = ipv6entries.floorEntry(ipv6AddressToLong(inet));
        }

        if (candidate != null && candidate.getValue().isInRange(inet)) {
            return candidate.getValue();
        }

        return null;
    }

    /**
     * Adds the given entry to this index.
     */
    public void add(GeoIpEntry entry) {
        if (entry.isIpv6()) {
            // Assume that all ranges are at least /64
            long start = ipv6AddressToLong(entry.getStart());
            ipv6entries.put(start, entry);
        } else {
            int start = ipv4AddressToInt(entry.getStart());
            entries.put(start, entry);
        }
    }

    /**
     * Returns the number of address ranges for which this index has an entry.
     */
    public int numberOfEntries() {
        return entries.size() + ipv6entries.size();
    }

    /**
     * Returns the given IPv4 address as an integer.
     */
    private int ipv4AddressToInt(InetAddress addr) {
        return Ints.fromByteArray(addr.getAddress());
    }

    /**
     * Returns the 64 high bits of the given IPv6 address as a long.
     */
    private long ipv6AddressToLong(InetAddress addr) {
        return ByteBuffer.wrap(addr.getAddress(), 0, 8).getLong();
    }
}
