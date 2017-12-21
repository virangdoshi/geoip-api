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
    private final DbIpFileParser parser;

    /**
     * Internal constructor.
     */
    private DbIpLookupService() {
        this.entries = new TreeMap<>();
        this.ipv6entries = new TreeMap<>();
        this.parser = new DbIpFileParser(new StringPool());
    }

    /**
     * Loads the DB-IP database entries from the given file.
     *
     * @param fileName
     *            the file name of the db-ip geolocation file.
     */
    public DbIpLookupService(String fileName) {
        this();
        checkNotNull(fileName, "Pre-condition violated: fileName must not be null.");

        try (InputStream fis = Files.newInputStream(Paths.get(fileName), StandardOpenOption.READ);
                InputStream gis = new GZIPInputStream(fis);
                Reader reader = new InputStreamReader(gis, StandardCharsets.UTF_8)) {

            logger.info("Loading IP geolocation data from {} ...", fileName);
            parser.parse(reader, this::addEntry);
            logger.info("IP geolocation data loaded, {} IPv4 and {} IPv6 entries", entries.size(), ipv6entries.size());
        } catch (Exception e) {
            logger.warn("Could not load file from path {}.", fileName, e);
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
        this();
        for (GeoIpEntry entry : entries) {
            addEntry(entry);
        }
    }
}
