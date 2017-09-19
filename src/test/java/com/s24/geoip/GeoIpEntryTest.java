package com.s24.geoip;

import org.junit.Test;

import java.net.Inet4Address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GeoIpEntryTest {

    private StringPool stringPool = new StringPool();

    @Test
    public void fromExampleLines() throws Exception {
        GeoIpEntry entry = GeoIpEntry.fromLine("\"0.0.0.0\",\"0.255.255.255\",\"ZZ\",\"\",\"\",\"0\",\"0\",\"\",\"\",\"Current network\",\"\",\"RFC 6890\"", stringPool);

        assertEquals(Inet4Address.getByAddress(new byte[]{0, 0, 0, 0}), entry.getStart());
        assertEquals(Inet4Address.getByAddress(new byte[]{0, (byte) 255, (byte) 255, (byte) 255}), entry.getEnd());
        assertEquals("ZZ", entry.getCountry());
        assertEquals("", entry.getRegion());
        assertEquals("", entry.getCity());
        assertNull(entry.getCityDistrict());
        assertEquals("0", entry.getLatitude());
        assertEquals("0", entry.getLongitude());
        assertEquals("", entry.getTimezoneOffset());
        assertEquals("", entry.getTimezoneName());
        assertEquals("Current network", entry.getIspName());
        assertNull(entry.getConnectionType());
        assertEquals("RFC 6890", entry.getOrganizationName());

        // This is a nice example line with escaped quotes and a city district
        entry = GeoIpEntry.fromLine("\"95.190.0.0\",\"95.190.31.255\",\"RU\",\"Novosibirsk\",\"Novosibirsk (Tsentralnyy)\",\"55.0306\",\"82.9176\",\"7\",\"Asia/Novosibirsk\",\"OJSC \\\"Sibirtelecom\\\"\",\"\",\"OJSC Sibirtelecom\"", stringPool);

        assertEquals(Inet4Address.getByAddress(new byte[]{95, (byte) 190, 0, 0}), entry.getStart());
        // -1 is 255, except Java has a signed byte type
        assertEquals(Inet4Address.getByAddress(new byte[]{95, (byte) 190, 31, (byte) 255}), entry.getEnd());
        assertEquals("RU", entry.getCountry());
        assertEquals("Novosibirsk", entry.getRegion());
        assertEquals("Novosibirsk", entry.getCity());
        assertEquals("Tsentralnyy", entry.getCityDistrict());
        assertEquals("55.0306", entry.getLatitude());
        assertEquals("82.9176", entry.getLongitude());
        assertEquals("7", entry.getTimezoneOffset());
        assertEquals("Asia/Novosibirsk", entry.getTimezoneName());
        assertEquals("OJSC \"Sibirtelecom\"", entry.getIspName());
        assertNull(entry.getConnectionType());
        assertEquals("OJSC Sibirtelecom", entry.getOrganizationName());
    }
}
