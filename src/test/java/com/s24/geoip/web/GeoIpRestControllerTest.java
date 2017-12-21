package com.s24.geoip.web;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.s24.geoip.GeoIpEntry;
import com.s24.geoip.GeoIpLookupService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.common.net.InetAddresses;

public class GeoIpRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GeoIpLookupService lookupService;

    @InjectMocks
    private GeoIpRestController restController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
    }

    @Test
    public void testNotFound() throws Exception {
        when(lookupService.lookup(any())).thenReturn(null);
        mockMvc.perform(get("/1.2.3.4").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFound() throws Exception {
        GeoIpEntry result = new GeoIpEntry(InetAddresses.forString("1.2.3.0"), InetAddresses.forString("1.2.3.255"),
                "ZZ", "", "", "", "", "", "", "", "", "", "");
        when(lookupService.lookup(any())).thenReturn(result);
        mockMvc.perform(get("/1.2.3.4").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
