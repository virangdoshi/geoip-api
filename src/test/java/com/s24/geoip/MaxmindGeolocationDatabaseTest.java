package com.s24.geoip;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.IspResponse;
import com.maxmind.geoip2.record.*;

public class MaxmindGeolocationDatabaseTest {

    private DatabaseReader cityDatabaseReader = Mockito.mock(DatabaseReader.class);
    private DatabaseReader ispDatabaseReader = Mockito.mock(DatabaseReader.class);
    private MaxmindGeolocationDatabase db = new MaxmindGeolocationDatabase(cityDatabaseReader, ispDatabaseReader);

    @Before
    public void setUp() throws Exception {
        List<String> locales = Collections.singletonList("DE");
        Country country = new Country(locales, 0, 0, "DE", ImmutableMap.of("DE", "Deutschland"));
        CityResponse cityResponse = new CityResponse(
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
        IspResponse ispResponse = new IspResponse(64512, "private use range", "192.168.1.1", "local network", "foobar");
        when(cityDatabaseReader.city(any(InetAddress.class))).thenReturn(cityResponse);
        when(ispDatabaseReader.isp(any(InetAddress.class))).thenReturn(ispResponse);
    }

    @Test
    public void testEmptyResponseIsConvertedCorrectly() throws Exception {
        CityResponse emptyCityResponse = new CityResponse(null, null, null, null, null, null, null, null, null, null);
        IspResponse emtpyIspResponse = new IspResponse(null, null, null, null, null);
        when(cityDatabaseReader.city(any(InetAddress.class))).thenReturn(emptyCityResponse);
        when(ispDatabaseReader.isp(any(InetAddress.class))).thenReturn(emtpyIspResponse);

        GeoIpEntry result = db.lookup(InetAddresses.forString("192.168.1.1"));
        assertNotNull(result);
        assertNull(result.getCountry());
        assertNull(result.getStateprov());
        assertNull(result.getCity());
        assertNull(result.getLatitude());
        assertNull(result.getLongitude());
        assertNull(result.getTimezone());
        assertNull(result.getIsp());
        assertNull(result.getOrganization());
        assertNull(result.getAsn());
        assertNull(result.getAsnOrganization());
    }

    @Test
    public void testResponseWithDataIsConvertedCorrectly() throws Exception {
        GeoIpEntry result = db.lookup(InetAddresses.forString("192.168.1.1"));
        assertNotNull(result);
        assertEquals("DE", result.getCountry());
        assertEquals("Hamburg", result.getStateprov());
        assertEquals("Hamburg", result.getCity());
        assertEquals("53.5854", result.getLatitude());
        assertEquals("10.0073", result.getLongitude());
        assertEquals("Europe/Berlin", result.getTimezone());
        assertEquals("local network", result.getIsp());
        assertEquals("foobar", result.getOrganization());
        assertEquals(Integer.valueOf(64512), result.getAsn());
        assertEquals("private use range", result.getAsnOrganization());
    }

    @Test
    public void testDatabaseWithOnlyCityDatabase() throws Exception {
        db = new MaxmindGeolocationDatabase(cityDatabaseReader, null);
        GeoIpEntry result = db.lookup(InetAddresses.forString("192.168.1.1"));
        assertNotNull(result);
        assertEquals("DE", result.getCountry());
        assertEquals("Hamburg", result.getStateprov());
        assertEquals("Hamburg", result.getCity());
        assertEquals("53.5854", result.getLatitude());
        assertEquals("10.0073", result.getLongitude());
        assertEquals("Europe/Berlin", result.getTimezone());
        assertNull(result.getIsp());
        assertNull(result.getOrganization());
        assertNull(result.getAsn());
        assertNull(result.getAsnOrganization());
    }

    @Test
    public void testDatabaseWithOnlyIspDatabase() throws Exception {
        db = new MaxmindGeolocationDatabase(null, ispDatabaseReader);
        GeoIpEntry result = db.lookup(InetAddresses.forString("192.168.1.1"));
        assertNotNull(result);
        assertNull(result.getCountry());
        assertNull(result.getStateprov());
        assertNull(result.getCity());
        assertNull(result.getLatitude());
        assertNull(result.getLongitude());
        assertNull(result.getTimezone());
        assertEquals("local network", result.getIsp());
        assertEquals("foobar", result.getOrganization());
        assertEquals(Integer.valueOf(64512), result.getAsn());
        assertEquals("private use range", result.getAsnOrganization());
    }
}
