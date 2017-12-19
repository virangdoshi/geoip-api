package com.s24.geoip;

import static java.util.Objects.requireNonNull;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

import java.io.IOException;
import java.io.Reader;
import java.net.InetAddress;
import java.util.Map;
import java.util.function.Consumer;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.util.CsvContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.net.InetAddresses;

/**
 * Parser for the geolocation file provided by db-ip.
 */
final class DbIpFileParser {

    // The column names (null means ignore that field)
    private static final String IP_START = "ip_start";
    private static final String IP_END = "ip_end";
    private static final String COUNTRY = "country";
    private static final String STATEPROV = "stateprov";
    private static final String CITY = "city";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String TIMEZONE_OFFSET = "timezone_offset";
    private static final String TIMEZONE_NAME = "timezone_name";
    private static final String ISP_NAME = "isp_name";
    private static final String CONNECTION_TYPE = "connection_type";
    private static final String ORGANIZATION_NAME = "organization_name";

    private static final String[] CSV_COLUMNS = {
            IP_START,
            IP_END,
            COUNTRY,
            STATEPROV,
            null, // district (not used)
            CITY,
            null, // zipcode (not used)
            LATITUDE,
            LONGITUDE,
            null, // geoname_id (not used)
            TIMEZONE_OFFSET,
            TIMEZONE_NAME,
            ISP_NAME,
            CONNECTION_TYPE,
            ORGANIZATION_NAME,
    };

    private final StringPool stringPool;
    private final CellProcessor[] cellProcessors;

    /**
     * Create a parser.
     * 
     * @param stringPool
     *            the string pool to use for parsed string values.
     */
    DbIpFileParser(StringPool stringPool) {
        this.stringPool = requireNonNull(stringPool);
        CellProcessor inetAddress = new ParseInetAddress();
        CellProcessor string = new PoolString(stringPool);
        CellProcessor optionalString = new Optional(new PoolString(stringPool));
        this.cellProcessors = new CellProcessor[] {
                inetAddress, // ip_start
                inetAddress, // ip_end
                string, // country
                optionalString, // stateprov
                null, // district (not used)
                null, // city (parsed manually into two fields (city and cityDistrict), not processed by supercsv)
                null, // zipcode (not used)
                optionalString, // latitude
                optionalString, // longitude
                null, // geoname_id (not used)
                optionalString, // timezone_offset
                optionalString, // timezone_name
                optionalString, // isp_name
                optionalString, // connection_type
                optionalString, // organization_name
        };
        if (cellProcessors.length != CSV_COLUMNS.length) {
            throw new AssertionError("Programmer error: length of columns does not match length of cell processors");
        }
    }

    /**
     * Parse the given input file. All parsed entries are given to the provided consumer.
     * 
     * @param input
     *            the input to parse.
     * @param consumer
     *            the consumer for the parsed entries.
     */
    void parse(Reader input, Consumer<GeoIpEntry> consumer) throws IOException {
        try (CsvMapReader reader = new CsvMapReader(input, STANDARD_PREFERENCE)) {
            Map<String, Object> entry;
            while ((entry = reader.read(CSV_COLUMNS, cellProcessors)) != null) {
                consumer.accept(csvEntryToGeoIpEntry(entry));
            }
        }
    }

    @VisibleForTesting
    GeoIpEntry csvEntryToGeoIpEntry(Map<String, Object> record) {
        // Split city into city and district
        String unparsedCityValue = (String) record.get(CITY);
        String city = null;
        String cityDistrict = null;
        if (unparsedCityValue != null) {
            int idx = unparsedCityValue.indexOf('(');
            if (idx != -1 && (idx + 1) < unparsedCityValue.length()) {
                city = stringPool.pool(unparsedCityValue.substring(0, idx).trim());
                cityDistrict = stringPool
                        .pool(unparsedCityValue.substring(idx + 1, unparsedCityValue.indexOf(')', idx + 1)));
            } else {
                city = stringPool.pool(unparsedCityValue);
            }
        }

        return new GeoIpEntry(
                (InetAddress) record.get(IP_START),
                (InetAddress) record.get(IP_END),
                (String) record.get(COUNTRY),
                (String) record.get(STATEPROV),
                city,
                cityDistrict,
                (String) record.get(LATITUDE),
                (String) record.get(LONGITUDE),
                (String) record.get(TIMEZONE_OFFSET),
                (String) record.get(TIMEZONE_NAME),
                (String) record.get(ISP_NAME),
                (String) record.get(CONNECTION_TYPE),
                (String) record.get(ORGANIZATION_NAME));
    }

    /**
     * Cell processor which interns strings in the given string pool.
     */
    @VisibleForTesting
    static class PoolString extends CellProcessorAdaptor implements StringCellProcessor {
        private final StringPool pool;

        PoolString(StringPool pool) {
            this.pool = pool;
        }

        @Override
        public <T> T execute(Object value, CsvContext context) {
            if (!(value instanceof String)) {
                throw new SuperCsvCellProcessorException(String.class, value, context, this);
            }
            String result = pool.pool((String) value);
            return this.next.execute(result, context);
        }
    }

    /**
     * Cell processor which parses IP addresses.
     */
    @VisibleForTesting
    static class ParseInetAddress extends CellProcessorAdaptor implements StringCellProcessor {
        @Override
        public <T> T execute(Object value, CsvContext context) {
            if (!(value instanceof String)) {
                throw new SuperCsvCellProcessorException(String.class, value, context, this);
            }
            InetAddress result = InetAddresses.forString((String) value);
            return this.next.execute(result, context);
        }
    }
}
