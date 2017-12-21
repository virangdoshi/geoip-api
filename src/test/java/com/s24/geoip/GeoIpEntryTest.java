package com.s24.geoip;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.net.InetAddresses;

public class GeoIpEntryTest {

    @Test
    public void testGetters() throws Exception {
        GeoIpEntry entry = new GeoIpEntry(
                InetAddresses.forString("192.168.0.0"),
                InetAddresses.forString("192.168.255.255"),
                "country",
                "region",
                "city",
                "cityDistrict",
                "latitude",
                "longitude",
                "timezoneOffset",
                "timezoneName",
                "ispName",
                "connectionType",
                "organizationName");

        assertEquals(InetAddresses.forString("192.168.0.0"), entry.getStart());
        assertEquals(InetAddresses.forString("192.168.255.255"), entry.getEnd());
        assertEquals("country", entry.getCountry());
        assertEquals("region", entry.getRegion());
        assertEquals("city", entry.getCity());
        assertEquals("cityDistrict", entry.getCityDistrict());
        assertEquals("latitude", entry.getLatitude());
        assertEquals("longitude", entry.getLongitude());
        assertEquals("timezoneOffset", entry.getTimezoneOffset());
        assertEquals("timezoneName", entry.getTimezoneName());
        assertEquals("ispName", entry.getIspName());
        assertEquals("connectionType", entry.getConnectionType());
        assertEquals("organizationName", entry.getOrganizationName());
    }

    @Test
    public void testIsIpv6() throws Exception {
        GeoIpEntry ipv4 = createEntryWithIpAddresses("192.168.0.0", "192.168.255.255");
        GeoIpEntry ipv6 = createEntryWithIpAddresses("2001:db8:1::", "2001:db8:1:ffff:ffff:ffff:ffff:ffff");
        assertFalse(ipv4.isIpv6());
        assertTrue(ipv6.isIpv6());
    }

    @Test
    public void testIsInRangeForIpv4Addresses() throws Exception {
        GeoIpEntry entry = createEntryWithIpAddresses("192.168.0.0", "192.168.255.255");
        assertTrue(entry.isInRange(InetAddresses.forString("192.168.0.1")));
        assertTrue(entry.isInRange(InetAddresses.forString("192.168.255.254")));
        assertFalse(entry.isInRange(InetAddresses.forString("192.167.255.254")));
        assertFalse(entry.isInRange(InetAddresses.forString("192.169.0.1")));
    }

    @Test
    public void testIsInRangeForIpv6Addresses() throws Exception {
        GeoIpEntry entry = createEntryWithIpAddresses("2001:db8:1::", "2001:db8:1:ffff:ffff:ffff:ffff:ffff");
        assertTrue(entry.isInRange(InetAddresses.forString("2001:db8:1::1")));
        assertTrue(entry.isInRange(InetAddresses.forString("2001:db8:1:ffff:ffff:ffff:ffff:fffe")));
        assertFalse(entry.isInRange(InetAddresses.forString("2001:db8:0::1")));
        assertFalse(entry.isInRange(InetAddresses.forString("2001:db8:0:ffff:ffff:ffff:ffff:fffe")));
        assertFalse(entry.isInRange(InetAddresses.forString("2001:db8:2::1")));
        assertFalse(entry.isInRange(InetAddresses.forString("2001:db8:88:bea::1")));
        assertFalse(entry.isInRange(InetAddresses.forString("2001:db8:1b4f:9a60::1")));
        assertFalse(entry.isInRange(InetAddresses.forString("2001:db8:2786:a492::1")));
    }

    /**
     * Helper method to create a valid entry from two IP addresses provided as strings.
     */
    private GeoIpEntry createEntryWithIpAddresses(String addrStart, String addrEnd) {
        return new GeoIpEntry(
                InetAddresses.forString(addrStart),
                InetAddresses.forString(addrEnd),
                "ZZ", null, null, null, null, null, null, null, null, null, null);
    }
}
