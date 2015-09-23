package com.s24.geoip;

import java.net.InetAddress;

public interface GeoIpLookupService {

   GeoIpEntry lookup(InetAddress inet);

}
