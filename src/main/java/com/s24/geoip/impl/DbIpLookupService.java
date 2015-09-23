package com.s24.geoip.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.common.collect.Maps;
import com.google.common.net.InetAddresses;
import com.s24.geoip.GeoIpEntry;
import com.s24.geoip.GeoIpLookupService;

/**
 * Reads a db-ip database and stores it queryable into a tree map. 
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public class DbIpLookupService implements GeoIpLookupService {

   private final Logger logger = LoggerFactory.getLogger(getClass());
   private final TreeMap<Integer, GeoIpEntry> entries;

   public DbIpLookupService(String dbip) {
      this(Paths.get(dbip));
   }

   public DbIpLookupService(Resource dbip) throws IOException {
      this(Paths.get(dbip.getFile().getAbsolutePath()));
   }
   
   public DbIpLookupService(Path dbip) {
      checkNotNull(dbip, "Pre-condition violated: db must not be null.");

      entries = Maps.newTreeMap();
            
      try (InputStream fis = Files.newInputStream(dbip, StandardOpenOption.READ);
            InputStream gis = new GZIPInputStream(fis);
            Reader decorator = new InputStreamReader(gis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(decorator);) {

         logger.info("Reading dbip data from {}", dbip);
         String line = null;
         int i = 0;
         while ((line = reader.readLine()) != null) {
            GeoIpEntry e = GeoIpEntry.fromLine(line);
            entries.put(e.getCoercedStart(), e);
            i++;

            if (i % 100000 == 0) {
               logger.info("Loaded {} entries from {} ...", entries.size(), dbip);
            }
         }

         logger.info("Finished reading {} entries from {} ...", entries.size(), dbip);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public GeoIpEntry lookup(InetAddress inet) {
      checkNotNull(inet, "Pre-condition violated: inet must not be null.");

      // find and check candiate
      Entry<Integer, GeoIpEntry> candidate = entries.floorEntry(InetAddresses.coerceToInteger(inet));
      if (candidate != null && candidate.getValue().isInRange(inet)) {
         return candidate.getValue();
      }

      return null;
   }

}
