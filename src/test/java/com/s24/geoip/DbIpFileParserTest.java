package com.s24.geoip;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assume;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.InetAddresses;


public class DbIpFileParserTest {

    private CsvContext dummyContext = new CsvContext(0, 0, 0);
    private DbIpFileParser parser = new DbIpFileParser(new StringPool());

    @SuppressWarnings({ "RedundantStringConstructorCall", "StringEquality" })
    @Test
    public void poolStringCellProcessorShouldPoolStringValues() throws Exception {
        CellProcessor proc = new DbIpFileParser.PoolString(new StringPool());
        // Construct strings explicitly to have separate instances
        String input1 = new String("foo");
        String input2 = new String("foo");
        Assume.assumeTrue("Inputs must be different instances of equal strings, otherwise this test is useless",
                input1 != input2 && input1.equals(input2));

        String output1 = proc.execute(input1, dummyContext);
        String output2 = proc.execute(input2, dummyContext);

        assertTrue(output1 == output2);
    }

    @Test
    public void parseInetAddressCellProcessorShouldParseIpv4Addresses() throws Exception {
        CellProcessor proc = new DbIpFileParser.ParseInetAddress();
        InetAddress address = proc.execute("192.168.0.0", dummyContext);
        assertTrue(address instanceof Inet4Address);
        assertArrayEquals(new byte[] { (byte) 192, (byte) 168, 0, 0 }, address.getAddress());
    }

    @Test
    public void parseInetAddressCellProcessorShouldParseIpv6Addresses() throws Exception {
        CellProcessor proc = new DbIpFileParser.ParseInetAddress();
        InetAddress address = proc.execute("2001:db8:1::", dummyContext);
        assertTrue(address instanceof Inet6Address);
        assertArrayEquals(new byte[] { 0x20, 0x01, 0x0d, (byte) 0xb8, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00 }, address.getAddress());
    }

    @Test
    public void entryWithSimpleCityNameShouldHaveNoCityDistrict() throws Exception {
        Map<String, Object> entry = ImmutableMap.of(
                "ip_start", InetAddresses.forString("0.0.0.0"),
                "ip_end", InetAddresses.forString("0.0.0.255"),
                "country", "DE",
                "city", "Hamburg");

        GeoIpEntry result = parser.csvEntryToGeoIpEntry(entry);

        assertEquals("Hamburg", result.getCity());
        assertNull(result.getCityDistrict());
    }

    @Test
    public void entryWithCityNameWithDistrictShouldBeSplitIntoCityAndCityDistrict() throws Exception {
        Map<String, Object> entry = ImmutableMap.of(
                "ip_start", InetAddresses.forString("0.0.0.0"),
                "ip_end", InetAddresses.forString("0.0.0.255"),
                "country", "DE",
                "city", "Hamburg (St. Pauli)");

        GeoIpEntry result = parser.csvEntryToGeoIpEntry(entry);

        assertEquals("Hamburg", result.getCity());
        assertEquals("St. Pauli", result.getCityDistrict());
    }

    @Test
    public void shouldParseCsvFile() throws Exception {
        String file = "2.60.0.0,2.60.255.255,RU,Omsk,Omsk,Omsk,,54.9667,73.3083,1496153,6,Asia/Omsk,\"OJSC \"\"Sibirtelecom\"\"\",dsl,\"OJSC Sibirtelecom\"\n"
                + "2.71.243.0,2.71.243.255,SE,Stockholm,\"Stockholm Municipality\",\"Stockholm (Stadshagen)\",\"104 25\",59.3347,18.0147,2673730,1,Europe/Stockholm,HI3G,wireless,\"Hi3G Access AB\"\n"
                + "2c0f:ffc8::,2c0f:ffc8:ffff:ffff:ffff:ffff:ffff:ffff,ZA,\"Western Cape\",\"City of Cape Town\",\"Cape Town (Observatory)\",7735,-33.9439,18.4712,3363444,2,Africa/Johannesburg,\"Frogfoot Networks\",,\"Frogfoot Networks\"";
        List<GeoIpEntry> results = new ArrayList<>();

        parser.parse(new StringReader(file), results::add);

        assertEquals(3, results.size());

        GeoIpEntry entry1 = results.get(0);
        assertEquals(InetAddresses.forString("2.60.0.0"), entry1.getStart());
        assertEquals(InetAddresses.forString("2.60.255.255"), entry1.getEnd());
        assertEquals("RU", entry1.getCountry());
        assertEquals("Omsk", entry1.getStateprov());
        assertEquals("Omsk", entry1.getCity());
        assertNull(entry1.getCityDistrict());
        assertEquals("54.9667", entry1.getLatitude());
        assertEquals("73.3083", entry1.getLongitude());
        assertEquals("6", entry1.getTimezoneOffset());
        assertEquals("Asia/Omsk", entry1.getTimezone());
        assertEquals("OJSC \"Sibirtelecom\"", entry1.getIsp());
        assertEquals("dsl", entry1.getConnection());
        assertEquals("OJSC Sibirtelecom", entry1.getOrganization());

        GeoIpEntry entry2 = results.get(1);
        assertEquals(InetAddresses.forString("2.71.243.0"), entry2.getStart());
        assertEquals(InetAddresses.forString("2.71.243.255"), entry2.getEnd());
        assertEquals("SE", entry2.getCountry());
        assertEquals("Stockholm", entry2.getStateprov());
        assertEquals("Stockholm", entry2.getCity());
        assertEquals("Stadshagen", entry2.getCityDistrict());
        assertEquals("59.3347", entry2.getLatitude());
        assertEquals("18.0147", entry2.getLongitude());
        assertEquals("1", entry2.getTimezoneOffset());
        assertEquals("Europe/Stockholm", entry2.getTimezone());
        assertEquals("HI3G", entry2.getIsp());
        assertEquals("wireless", entry2.getConnection());
        assertEquals("Hi3G Access AB", entry2.getOrganization());

        GeoIpEntry entry3 = results.get(2);
        assertEquals(InetAddresses.forString("2c0f:ffc8:0:0:0:0:0:0"), entry3.getStart());
        assertEquals(InetAddresses.forString("2c0f:ffc8:ffff:ffff:ffff:ffff:ffff:ffff"), entry3.getEnd());
        assertEquals("ZA", entry3.getCountry());
        assertEquals("Western Cape", entry3.getStateprov());
        assertEquals("Cape Town", entry3.getCity());
        assertEquals("Observatory", entry3.getCityDistrict());
        assertEquals("-33.9439", entry3.getLatitude());
        assertEquals("18.4712", entry3.getLongitude());
        assertEquals("2", entry3.getTimezoneOffset());
        assertEquals("Africa/Johannesburg", entry3.getTimezone());
        assertEquals("Frogfoot Networks", entry3.getIsp());
        assertNull(entry3.getConnection());
        assertEquals("Frogfoot Networks", entry3.getOrganization());
    }
}
