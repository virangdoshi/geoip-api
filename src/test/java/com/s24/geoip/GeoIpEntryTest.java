package com.s24.geoip;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeoIpEntryTest {

    @Test
    public void testGetters() throws Exception {
        GeoIpEntry entry = new GeoIpEntry.Builder()
                .setCountry("country")
                .setStateprov("region")
                .setCity("city")
                .setLatitude("latitude")
                .setLongitude("longitude")
                .setTimezone("timezoneName")
                .setIsp("isp")
                .setOrganization("organization")
                .setAsn(64512)
                .setAsnOrganization("asnOrganization")
                .build();

        assertEquals("country", entry.getCountry());
        assertEquals("region", entry.getStateprov());
        assertEquals("city", entry.getCity());
        assertEquals("latitude", entry.getLatitude());
        assertEquals("longitude", entry.getLongitude());
        assertEquals("timezoneName", entry.getTimezone());
        assertEquals("isp", entry.getIsp());
        assertEquals("organization", entry.getOrganization());
        assertEquals(Integer.valueOf(64512), entry.getAsn());
        assertEquals("asnOrganization", entry.getAsnOrganization());
    }
}
