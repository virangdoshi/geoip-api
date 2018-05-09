package com.s24.geoip;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeoIpEntryTest {

    @Test
    public void testGetters() throws Exception {
        GeoIpEntry entry = new GeoIpEntry(
                "country",
                "region",
                "city",
                "latitude",
                "longitude",
                "timezoneName"
        );

        assertEquals("country", entry.getCountry());
        assertEquals("region", entry.getStateprov());
        assertEquals("city", entry.getCity());
        assertEquals("latitude", entry.getLatitude());
        assertEquals("longitude", entry.getLongitude());
        assertEquals("timezoneName", entry.getTimezone());
    }
}
