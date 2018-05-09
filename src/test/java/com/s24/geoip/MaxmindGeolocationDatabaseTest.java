package com.s24.geoip;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class MaxmindGeolocationDatabaseTest {

    private DatabaseReader reader = Mockito.mock(DatabaseReader.class);
    private MaxmindGeolocationDatabase db = new MaxmindGeolocationDatabase(reader);

    @Test
    public void testEmptyCityResponseIsConvertedCorrectly() throws Exception {
        CityResponse emptyCityResponse = new CityResponse(null, null, null, null, null, null, null, null, null, null);
        when(reader.city(any(InetAddress.class))).thenReturn(emptyCityResponse);

        GeoIpEntry result = db.lookup(InetAddresses.forString("192.168.1.1"));
        assertNotNull(result);
        assertNull(result.getCountry());
        assertNull(result.getStateprov());
        assertNull(result.getCity());
        assertNull(result.getLatitude());
        assertNull(result.getLongitude());
        assertNull(result.getTimezone());
    }

    @Test
    public void testCityResponseWithDataIsConvertedCorrectly() throws Exception {
        List<String> locales = Arrays.asList("DE");
        Country country = new Country(locales, 0, 0, "DE", ImmutableMap.of("DE", "Deutschland"));
        CityResponse response = new CityResponse(
                new City(locales, 0, 0, ImmutableMap.of("DE", "Hamburg")),
                new Continent(locales, null, 0, ImmutableMap.of("DE", "Europa")),
                country,
                new Location(0, 0, 53.5854, 10.0073, 0, 0, "Europe/Berlin"),
                new MaxMind(),
                new Postal("22301", 0),
                null,
                null,
                Lists.newArrayList(new Subdivision(locales, 0, 0, "DE", ImmutableMap.of("DE", "Hamburg"))),
                new Traits());
        when(reader.city(any(InetAddress.class))).thenReturn(response);

        GeoIpEntry result = db.lookup(InetAddresses.forString("192.168.1.1"));
        assertNotNull(result);
        assertEquals("DE", result.getCountry());
        assertEquals("Hamburg", result.getStateprov());
        assertEquals("Hamburg", result.getCity());
        assertEquals("53.5854", result.getLatitude());
        assertEquals("10.0073", result.getLongitude());
        assertEquals("Europe/Berlin", result.getTimezone());
    }
}
