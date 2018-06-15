package com.s24.geoip;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.net.InetAddresses;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestApiIT {

    private static final String REST_URL = "/{address}";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean(name = "cityDatabaseReader")
    private DatabaseReader cityDatabaseReader;

    @MockBean(name = "ispDatabaseReader")
    private DatabaseReader ispDatabaseReader;

    @Before
    public void setUp() throws Exception {
        when(cityDatabaseReader.city(eq(InetAddresses.forString("192.0.2.1")))).thenReturn(
                new CityResponse(null, null,
                        new Country(null, 0, 0, "ZZ", null),
                        null, null, null, null, null, null, null));
        when(cityDatabaseReader.city(not(eq(InetAddresses.forString("192.0.2.1")))))
                .thenThrow(new AddressNotFoundException("test"));
        when(ispDatabaseReader.isp(any(InetAddress.class))).thenThrow(new AddressNotFoundException("test"));
    }

    @Test
    public void testRestApiRunningInContainer() throws Exception {
        ResponseEntity<Map> response = restTemplate.getForEntity(REST_URL, Map.class, "192.0.2.1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("ZZ", response.getBody().get("country"));
    }

    @Test
    public void test404ResponseForIpAddressWithoutEntry() throws Exception {
        ResponseEntity<Map> response = restTemplate.getForEntity(REST_URL, Map.class, "192.0.2.2");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test400ResponseForInvalidInput() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(REST_URL, String.class, "invalid");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
