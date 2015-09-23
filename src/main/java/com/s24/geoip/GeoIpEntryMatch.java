package com.s24.geoip;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.InetAddresses;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties
public class GeoIpEntryMatch {

   private final GeoIpEntry entry;
   private final InetAddress address;

   public GeoIpEntryMatch(String address, GeoIpEntry entry) {
      this(InetAddresses.forString(address), entry);
   }

   public GeoIpEntryMatch(InetAddress address, GeoIpEntry entry) {

      checkNotNull(address, "Pre-condition violated: address must not be null.");
      checkNotNull(entry, "Pre-condition violated: entry must not be null.");

      this.address = address;
      this.entry = entry;
   }

   @JsonProperty("address")
   public String getAddress() {
      return address.getHostAddress();
   }

   @JsonProperty("country")
   public String getCountry() {
      return entry.getCountry();
   }

   @JsonProperty("stateprov")
   @JsonInclude(Include.NON_EMPTY)
   public String getRegion() {
      return entry.getRegion();
   }

   @JsonProperty("city")
   @JsonInclude(Include.NON_EMPTY)
   public String getCity() {
      return entry.getCity();
   }

   @JsonProperty("cityDistrict")
   @JsonInclude(Include.NON_EMPTY)
   public String getCityDistrict() {
      return entry.getCityDistrict();
   }

   @JsonProperty("latitude")
   @JsonInclude(Include.NON_EMPTY)
   public String getLatitude() {
      return entry.getLatitude();
   }

   @JsonProperty("longitude")
   @JsonInclude(Include.NON_EMPTY)
   public String getLongitude() {
      return entry.getLongitude();
   }

   @JsonProperty("isp")
   @JsonInclude(Include.NON_EMPTY)
   public String getIspName() {
      return entry.getIspName();
   }

   @JsonProperty("timezone")
   @JsonInclude(Include.NON_EMPTY)
   public String getTimezoneName() {
      return entry.getTimezoneName();
   }

   @JsonProperty("timezoneOffset")
   @JsonInclude(Include.NON_EMPTY)
   public String getTimezoneOffset() {
      return entry.getTimezoneOffset();
   }

   @JsonProperty("connection")
   @JsonInclude(Include.NON_EMPTY)
   public String getConnectionType() {
      return entry.getConnectionType();
   }

   @JsonProperty("organization")
   @JsonInclude(Include.NON_EMPTY)
   public String getOrganizationName() {
      return entry.getOrganizationName();
   }

}
