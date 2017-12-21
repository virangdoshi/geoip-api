package com.s24.geoip.web;

import static java.util.Objects.requireNonNull;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.s24.geoip.GeoIpEntry;
import com.s24.geoip.GeolocationIndex;

/**
 * Provides a Geo Lookup service for IPv4 and IPv6 addresses with the help of DB-IP.
 *
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
@Controller
public class GeoIpRestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GeolocationIndex geolocations;

    /**
     * Creates a controller that serves the geolocations from the given index.
     * 
     * @param geolocations
     *            the geolocation index.
     */
    @Autowired
    public GeoIpRestController(GeolocationIndex geolocations) {
        this.geolocations = requireNonNull(geolocations);
    }

    @RequestMapping(value = { "/", "/favicon.ico", "/robots.txt" })
    public ResponseEntity handleKnownNotFounds() {
        return ResponseEntity.notFound().build();
    }

    /**
     * Lookup the geolocation information for an ip address.
     */
    @RequestMapping(value = "/{address:.+}", method = RequestMethod.GET)
    public ResponseEntity lookup(@PathVariable InetAddress address) {
        GeoIpEntry result = geolocations.lookup(address);
        if (result != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GeoIpEntryDocument(address, result));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(InvalidIpAddressException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidIpAddress(Exception e) {
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return "We ran into an error: " + e.getMessage();
    }

    /**
     * Initializes data binding.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(InetAddress.class, new InetAdressPropertyEditor());
    }
}
