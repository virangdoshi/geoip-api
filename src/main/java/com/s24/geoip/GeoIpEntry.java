package com.s24.geoip;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import com.google.common.primitives.Chars;

public class GeoIpEntry {

   private static final Pattern cityAndDistrictPattern = Pattern.compile("([^\\(]+)\\s+\\(([^\\)]+)\\)");

   private final InetAddress start, end;
   private final String country, region, city, cityDistrict, latitude, longitude, ispName, timezoneOffset,
         timezoneName,
         connectionType, organizationName;

   public static GeoIpEntry fromLine(String csv) {
      checkNotNull(csv, "Pre-condition violated: csv must not be null.");

      Iterator<String> parts = Splitter.on("\",\"").trimResults(CharMatcher.anyOf("\"'")).split(csv).iterator();
      InetAddress start = InetAddresses.forString(parts.next());
      InetAddress end = InetAddresses.forString(parts.next());
      String country = parts.next().intern();
      String region = null;
      String city = null;
      String cityDistrict = null;
      String latitude = null;
      String longitude = null;
      String timezoneOffset = null;
      String timezoneName = null;
      String ispName = null;
      String connectionType = null;
      String organizationName = null;

      // optional parts
      if (parts.hasNext()) {
         region = parts.next().intern();
      }
      if (parts.hasNext()) {
         String cad = parts.next();

         // check cheap first
         if (Chars.contains(cad.toCharArray(), '(')) {
            Matcher m = cityAndDistrictPattern.matcher(cad);
            if (m.matches()) {
               city = m.group(1).intern();
               cityDistrict = m.group(2).intern();
            }
         }

         if (city == null) {
            city = cad.intern();
            cityDistrict = null;
         }
      }
      if (parts.hasNext()) {
         latitude = parts.next().intern();
      }
      if (parts.hasNext()) {
         longitude = parts.next().intern();
      }
      if (parts.hasNext()) {
         timezoneOffset = parts.next().intern();
      }
      if (parts.hasNext()) {
         timezoneName = parts.next().intern();
      }
      if (parts.hasNext()) {
         ispName = parts.next().intern();
      }
      if (parts.hasNext()) {
         connectionType = parts.next().intern();
      }
      if (parts.hasNext()) {
         organizationName = parts.next().intern();
      }

      return new GeoIpEntry(start, end, country, region, city, cityDistrict, latitude, longitude, timezoneOffset,
            timezoneName, ispName, connectionType, organizationName);
   }

   public GeoIpEntry(InetAddress start, InetAddress end, String country, String region, String city,
         String cityDistrict,
         String latitude, String longitude, String timezoneOffset, String timezoneName, String ispName,
         String connectionType,
         String organizationName) {
      checkNotNull(start, "Pre-condition violated: start must not be null.");
      checkNotNull(end, "Pre-condition violated: end must not be null.");
      checkNotNull(country, "Pre-condition violated: country must not be null.");

      this.start = start;
      this.end = end;
      this.country = country;
      this.region = region;
      this.city = city;
      this.cityDistrict = cityDistrict;
      this.latitude = latitude;
      this.longitude = longitude;
      this.timezoneName = timezoneName;
      this.timezoneOffset = timezoneOffset;
      this.ispName = Strings.emptyToNull(ispName);
      this.connectionType = Strings.emptyToNull(connectionType);
      this.organizationName = Strings.emptyToNull(organizationName);
   }

   public boolean isInRange(InetAddress i) {
      checkNotNull(i, "Pre-condition violated: i must not be null.");

      int ord = InetAddresses.coerceToInteger(i);

      return ord >= InetAddresses.coerceToInteger(start)
            && ord <= InetAddresses.coerceToInteger(end);
   }

   public Integer getCoercedStart() {
      return InetAddresses.coerceToInteger(start);
   }

   public InetAddress getStart() {
      return start;
   }

   public InetAddress getEnd() {
      return end;
   }

   public String getCountry() {
      return country;
   }

   public String getRegion() {
      return region;
   }

   public String getCity() {
      return city;
   }

   public String getLatitude() {
      return latitude;
   }

   public String getLongitude() {
      return longitude;
   }

   public String getIspName() {
      return ispName;
   }

   public String getConnectionType() {
      return connectionType;
   }

   public String getOrganizationName() {
      return organizationName;
   }

   public String getTimezoneOffset() {
      return timezoneOffset;
   }

   public String getTimezoneName() {
      return timezoneName;
   }

   public String getCityDistrict() {
      return cityDistrict;
   }

}
