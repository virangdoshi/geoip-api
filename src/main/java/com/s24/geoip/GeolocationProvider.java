package com.s24.geoip;

import java.net.InetAddress;

/**
 * Provides location information for an IP address.
 */
public interface GeolocationProvider {

    /**
     * Returns the location information for the given IP address, or {@code null} if no information was found for the
     * given address.
     * 
     * @param addr
     *            the IP address.
     */
    GeoIpEntry lookup(InetAddress addr);
}
