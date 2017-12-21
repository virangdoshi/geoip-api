package com.s24.geoip.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.common.net.InetAddresses;
import com.s24.geoip.GeoIpEntry;
import com.s24.geoip.GeolocationIndex;

public class GeoIpRestControllerTest {

    private MockMvc mockMvc;
    private GeolocationIndex index;
    private GeoIpRestController restController;

    @Before
    public void setUp() throws Exception {
        index = new GeolocationIndex();
        index.add(new GeoIpEntry(InetAddresses.forString("192.168.1.0"), InetAddresses.forString("192.168.1.255"),
                "ZZ", null, null, null, null, null, null, null, null, null, null));
        restController = new GeoIpRestController(index);
        mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
    }

    @Test
    public void testNotFound() throws Exception {
        mockMvc.perform(get("/192.168.42.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFound() throws Exception {
        mockMvc.perform(get("/192.168.1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"country\":\"ZZ\"}"));
    }
}
