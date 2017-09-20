package com.s24.geoip;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tuned parser for parsing lines from the DB-IP database file. Assumes that all values are quoted.
 */
class DbIpLineParser implements Iterator<String> {
    private static final char QUOTE = '"';
    private static final char SEPARATOR = ',';
    private static final char ESCAPE = '\\';

    private int position = 0;
    private final String input;

    DbIpLineParser(String input) {
        this.input = input;
    }

    public String next() {
        if (position < input.length()) {
            // skip over the initial quote
            if (input.charAt(position++) != QUOTE) {
                throw new RuntimeException("Encountered an unquoted value in input: " + input);
            }

            int tokenStart = position;

            // go to the closing quote character
            while (position < input.length() && input.charAt(position) != QUOTE) {
                if (input.charAt(position++) == ESCAPE && position < input.length()) {
                    position++;
                }
            }

            String result = input.substring(tokenStart, position++);

            // unescape escaped quotes in the string, if any
            if (result.indexOf(ESCAPE) != -1) {
                result = result.replaceAll("\\\\\"", "\"");
            }

            // skip over the following separator char
            if (position < input.length() && input.charAt(position++) != SEPARATOR) {
                throw new RuntimeException("Expected a comma at position " + (position - 1) + " in input: " + input);
            }

            return result;
        }
        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        return position < input.length();
    }
}
