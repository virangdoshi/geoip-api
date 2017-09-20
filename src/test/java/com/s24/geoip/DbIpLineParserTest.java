package com.s24.geoip;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DbIpLineParserTest {

    @Test
    public void parseSingleQuotedToken() throws Exception {
        DbIpLineParser parser = new DbIpLineParser("\"foo\"");
        assertEquals("foo", parser.next());
        assertFalse(parser.hasNext());
    }

    @Test
    public void parseMultipleQuotedTokens() throws Exception {
        DbIpLineParser parser = new DbIpLineParser("\"foo\",\"bar\",\"123\"");
        assertEquals("foo", parser.next());
        assertEquals("bar", parser.next());
        assertEquals("123", parser.next());
        assertFalse(parser.hasNext());
    }

    @Test
    public void testExampleLines() throws Exception {
        DbIpLineParser parser = new DbIpLineParser("\"0.0.0.0\",\"0.255.255.255\",\"ZZ\",\"\",\"\",\"0\",\"0\",\"\",\"\",\"Current network\",\"\",\"RFC 6890\"");
        assertEquals("0.0.0.0", parser.next());
        assertEquals("0.255.255.255", parser.next());
        assertEquals("ZZ", parser.next());
        assertEquals("", parser.next());
        assertEquals("", parser.next());
        assertEquals("0", parser.next());
        assertEquals("0", parser.next());
        assertEquals("", parser.next());
        assertEquals("", parser.next());
        assertEquals("Current network", parser.next());
        assertEquals("", parser.next());
        assertEquals("RFC 6890", parser.next());
        assertFalse(parser.hasNext());

        parser = new DbIpLineParser("\"2.60.0.0\",\"2.60.255.255\",\"RU\",\"Omsk\",\"Omsk\",\"54.9667\",\"73.3083\",\"6\",\"Asia/Omsk\",\"OJSC \\\"Sibirtelecom\\\"\",\"dsl\",\"OJSC Sibirtelecom\"");
        assertEquals("2.60.0.0", parser.next());
        assertEquals("2.60.255.255", parser.next());
        assertEquals("RU", parser.next());
        assertEquals("Omsk", parser.next());
        assertEquals("Omsk", parser.next());
        assertEquals("54.9667", parser.next());
        assertEquals("73.3083", parser.next());
        assertEquals("6", parser.next());
        assertEquals("Asia/Omsk", parser.next());
        assertEquals("OJSC \"Sibirtelecom\"", parser.next());
        assertEquals("dsl", parser.next());
        assertEquals("OJSC Sibirtelecom", parser.next());
    }
}
