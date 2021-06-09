package com.s24.geoip.web;

import static java.util.Objects.requireNonNull;

import java.net.InetAddress;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.s24.geoip.GeolocationProvider;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a Geo Lookup service for IPv4 and IPv6 addresses with the help of DB-IP.
 *
 * @author shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
@ApplicationScoped
public class GeoIpRestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GeolocationProvider geolocations;

    /**
     * Creates a controller that serves the geolocations from the given provider.
     *
     * @param geolocations
     *            the geolocation provider.
     */
    public GeoIpRestController(GeolocationProvider geolocations) {
        this.geolocations = requireNonNull(geolocations);
    }

    @Path("/")
    @GET
    public Response handleRootNotFound() {
        return Response.status(Status.NOT_FOUND).build();
    }

    @Path("/favicon.ico")
    @GET
    public Response handleFaviconNotFound() {
        return Response.status(Status.NOT_FOUND).build();
    }

    @Path("/robots.txt")
    @GET
    public Response handleRobotsNotFound() {
        return Response.status(Status.NOT_FOUND).build();
    }

    /**
     * Lookup the geolocation information for an ip address.
     */
    @Path("/{address:.+}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookup(InetAddress address) {
        return Response.ok(geolocations.lookup(address)).build();
    }

    @ServerExceptionMapper
    public Response handleInvalidIpAddress(InvalidIpAddressException e) {
        return Response.status(Status.BAD_REQUEST).build();
    }

    @ServerExceptionMapper
    public Response handleException(Exception e) {
        logger.error(e.getMessage(), e);

        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("We ran into an error: " + e.getMessage()).build();
    }
}
