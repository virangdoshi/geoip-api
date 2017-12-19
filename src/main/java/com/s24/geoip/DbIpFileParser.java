package com.s24.geoip;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

/**
 * Parser for the geolocation file provided by db-ip.
 */
final class DbIpFileParser {

    private final StringPool stringPool;

    /**
     * Create a parser.
     * 
     * @param stringPool
     *            the string pool to use for parsed string values.
     */
    DbIpFileParser(StringPool stringPool) {
        this.stringPool = requireNonNull(stringPool);
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
        try (BufferedReader reader = new BufferedReader(input)) {
            String line;
            while ((line = reader.readLine()) != null) {
                consumer.accept(GeoIpEntry.fromLine(line, stringPool));
            }
        }
    }
}
