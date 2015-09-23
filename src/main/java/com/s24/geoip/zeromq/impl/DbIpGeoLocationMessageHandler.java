package com.s24.geoip.zeromq.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.net.InetAddresses;
import com.s24.geoip.GeoIpEntry;
import com.s24.geoip.GeoIpEntryMatch;
import com.s24.geoip.GeoIpLookupService;
import com.s24.geoip.zeromq.MessageHandler;

/**
 * Decodes a tracking pixel and returns valid JSON.
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public class DbIpGeoLocationMessageHandler implements MessageHandler {

   @Autowired
   private GeoIpLookupService lookupService;

   private final ObjectMapper mapper = new ObjectMapper();
   private final String binding;
   
   public DbIpGeoLocationMessageHandler(String binding) {
      checkNotNull(binding, "Pre-condition violated: binding must not be null.");
      
      this.binding = binding;
   }

   @Override
   public String getBinding() {
      return binding;
   }
   
   @Override
   public String reply(String message) {
      checkNotNull(message, "Pre-condition violated: message must not be null.");
      checkArgument(!Strings.isNullOrEmpty(message),
            "Pre-condition violated: expression message must not be empty.");
      String response = null;

      // mix multiple proxy ips
      message = Iterables.getLast(Splitter.on(',').trimResults().omitEmptyStrings().split(message));

      // lookup
      if (lookupService != null) {
         GeoIpEntry entry = lookupService.lookup(InetAddresses.forString(message));

         // build response
         if (entry != null) {
            try {
               response = mapper.writeValueAsString(new GeoIpEntryMatch(message, entry));
            } catch (JsonProcessingException e) {
               throw new RuntimeException(e);
            }
         }
      }

      return response;
   }
}
