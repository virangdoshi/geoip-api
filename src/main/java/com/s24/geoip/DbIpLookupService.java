package com.s24.geoip;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import com.google.common.annotations.VisibleForTesting;
import com.s24.geoip.StringPool;
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
    private final TreeMap<Integer, GeoIpEntry> ipv6entries;
    private final StringPool stringPool;

    public DbIpLookupService(String dbip) {
        this(Paths.get(dbip));
    }

    public DbIpLookupService(Resource dbip) throws IOException {
        this(Paths.get(dbip.getFile().getAbsolutePath()));
    }

    /**
     * Loads the DB-IP database entries from the given file.
     */
    public DbIpLookupService(Path dbip) {
        this();
        checkNotNull(dbip, "Pre-condition violated: db must not be null.");

        try (InputStream fis = Files.newInputStream(dbip, StandardOpenOption.READ);
             InputStream gis = new GZIPInputStream(fis);
             Reader decorator = new InputStreamReader(gis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(decorator);) {

            logger.info("Reading dbip data from {}", dbip);
            String line = null;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                GeoIpEntry e = GeoIpEntry.fromLine(line, stringPool);
                addEntry(e);
                i++;

                if (i % 100000 == 0) {
                    logger.info("Loaded {} ipv4 entries from {} ...", entries.size(), dbip);
                    logger.info("Loaded {} ipv6 entries from {} ...", ipv6entries.size(), dbip);
                }
            }

            logger.info("Finished reading {} ipv4 entries from {} ...", entries.size(), dbip);
            logger.info("Finished reading {} ipv6 entries from {} ...", ipv6entries.size(), dbip);
        } catch (Exception e) {
            logger.warn("Could not load file from path {}.", dbip, e);
        }
    }

    @Override
    public GeoIpEntry lookup(InetAddress inet) {
        checkNotNull(inet, "Pre-condition violated: inet must not be null.");

        Entry<Integer, GeoIpEntry> candidate;

        // find and check candiate
        if (inet instanceof Inet4Address) {
            candidate = entries.floorEntry(InetAddresses.coerceToInteger(inet));
        } else {
            candidate = ipv6entries.ceilingEntry(InetAddresses.coerceToInteger(inet));
        }

        if (candidate != null && candidate.getValue().isInRange(inet)) {
            return candidate.getValue();
        }

        return null;
    }

    private void addEntry(GeoIpEntry entry) {
        TreeMap<Integer, GeoIpEntry> target = entry.isIpv6() ? ipv6entries : entries;
        target.put(entry.getCoercedStart(), entry);
    }

    /**
     * Constructor for tests.
     */
    @VisibleForTesting
    public DbIpLookupService(GeoIpEntry... entries) {
        this.entries = new TreeMap<>();
        this.ipv6entries = new TreeMap<>();
        this.stringPool = new StringPool();
        for (GeoIpEntry entry : entries) {
            addEntry(entry);
        }
    }
}
