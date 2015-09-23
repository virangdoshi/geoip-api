package com.s24.geoip.web;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.s24.geoip.GeoIpEntryMatch;
import com.s24.geoip.GeoIpLookupService;

/**
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
@Controller
public class GeoIpRestController {

   private final Logger logger = LoggerFactory.getLogger(getClass());

   @Autowired
   private GeoIpLookupService lookupService;

   /**
    * This is a pretty fuckup, but path matching with ip adresses is a horror in
    * spring. So we use this for now ...
    */
   @RequestMapping(value = "/**", method = RequestMethod.GET)
   @ResponseStatus(value = HttpStatus.OK)
   @ResponseBody
   public GeoIpEntryMatch handleLookup(HttpServletRequest request) {
      checkNotNull(request, "Pre-condition violated: ip must not be null.");

      InetAddress ip = InetAddresses.forString(
            Iterables.getLast(
                  Splitter.on('/').omitEmptyStrings().split(request.getRequestURI())));

      return new GeoIpEntryMatch(ip, lookupService.lookup(ip));
   }

   @ExceptionHandler(Exception.class)
   @ResponseBody
   public ResponseEntity<String> handleException(Exception e) {
      logger.warn(e.getMessage(), e);
      return new ResponseEntity<String>("We ran into an error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
   }

   /**
    * Initializes data binding.
    */
   @InitBinder
   public void initBinder(WebDataBinder binder) {
      binder.registerCustomEditor(InetAddress.class, new InetAdressPropertyEditor());
   }
}
