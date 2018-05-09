package com.s24.geoip.web;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.common.net.InetAddresses;
import com.s24.geoip.GeoIpEntry;
import com.s24.geoip.GeolocationProvider;

public class GeoIpRestControllerTest {

    private static final InetAddress IPV4_ADDR = InetAddresses.forString("192.168.1.1");
    private static final InetAddress IPV6_ADDR = InetAddresses.forString("2001:db8:1::1");

    private MockMvc mockMvc;
    private GeolocationProvider provider;
    private GeoIpRestController restController;

    @Before
    public void setUp() throws Exception {
        provider = mock(GeolocationProvider.class);
        when(provider.lookup(eq(IPV4_ADDR))).thenReturn(new GeoIpEntry(
                "ZZ",
                null,
                null,
                null,
                null,
                null));
        when(provider.lookup(eq(IPV6_ADDR))).thenReturn(new GeoIpEntry(
                "ZZ",
                null,
                null,
                null,
                null,
                null));
        restController = new GeoIpRestController(provider);
        mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
    }

    @Test
    public void testIpAddressNotFound() throws Exception {
        mockMvc.perform(get("/192.168.42.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testKnownNotFounds() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isNotFound());
        mockMvc.perform(get("/robots.txt")).andExpect(status().isNotFound());
        mockMvc.perform(get("/favicon.ico")).andExpect(status().isNotFound());
    }

    @Test
    public void testIpv4Address() throws Exception {
        mockMvc.perform(get("/192.168.1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"country\":\"ZZ\"}"));
    }

    @Test
    public void testIpv6Address() throws Exception {
        mockMvc.perform(get("/2001:db8:1::1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"country\":\"ZZ\"}"));
    }

    @Test
    public void testInvalidIpAddresses() throws Exception {
        mockMvc.perform(get("/1.2.3.4.5")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/example.com")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/something")).andExpect(status().isBadRequest());
    }
}
