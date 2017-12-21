package com.s24.geoip.web;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import com.s24.geoip.GeolocationIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.net.InetAddresses;

/**
 * Provides a Geo Lookup service for IPv4 and IPv6 addresses with the help of DB-IP.
 *
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
@Controller
public class GeoIpRestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GeolocationIndex geolocations;

    @RequestMapping(value = {"/", "/favicon.ico", "/robots.txt"})
    public ResponseEntity handleKnownNotFounds() {
        return ResponseEntity.notFound().build();
    }

    /**
     * This is a pretty fuckup, but path matching with ip adresses is a horror in spring. So we use this for now ...
     */
    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public ResponseEntity handleLookup(HttpServletRequest request) {
        checkNotNull(request, "Pre-condition violated: ip must not be null.");

        InetAddress ip = InetAddresses.forString(
                Iterables.getLast(
                        Splitter.on('/').omitEmptyStrings().split(request.getRequestURI())));

        if (geolocations.lookup(ip) != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GeoIpEntryDocument(ip, geolocations.lookup(ip)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        logger.warn(e.getMessage(), e);
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
