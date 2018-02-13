package com.s24.geoip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.Wait;

/**
 * Integration test which runs the REST API in a container.
 */
public class RestApiIT {

    @ClassRule
    public static GenericContainer geoIpApi = new GenericContainer("shopping24/geoip-api:latest")
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/0.0.0.1").forStatusCode(200));

    private String restUrl;
    private RestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        String host = geoIpApi.getContainerIpAddress();
        int port = geoIpApi.getMappedPort(8080);
        restUrl = String.format("http://%s:%d/{address}", host, port);
        restTemplate = new RestTemplate();
    }

    @Test
    public void testRestApiRunningInContainer() throws Exception {
        ResponseEntity<Map> response = restTemplate.getForEntity(restUrl, Map.class, "0.0.0.1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("ZZ", response.getBody().get("country"));
        assertEquals("Current network", response.getBody().get("isp"));
        assertEquals("RFC 6890", response.getBody().get("organization"));
    }

    @Test
    public void test404ResponseForIpAddressWithoutEntry() throws Exception {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(restUrl, Map.class, "192.0.2.1");
            fail("Expected response with status code 404, but got " + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                fail("Expected response with status code 404, but got " + e.getStatusCode());
            }
        }
    }

    @Test
    public void test400ResponseForInvalidInput() throws Exception {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(restUrl, Map.class, "invalid");
            fail("Expected response with status code 400, but got " + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.BAD_REQUEST) {
                fail("Expected response with status code 400, but got " + e.getStatusCode());
            }
        }
    }
}
