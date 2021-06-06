package com.s24.geoip;

import com.maxmind.db.Metadata;
import com.maxmind.geoip2.NetworkDeserializer;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.RepresentedCountry;
import com.maxmind.geoip2.record.Subdivision;
import com.maxmind.geoip2.record.Traits;

import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.TypeHint;

@TypeHint(types = { GeoIpEntry.class, Metadata.class, City.class, CityResponse.class, Continent.class, Location.class,
        Postal.class, Country.class, RepresentedCountry.class, Subdivision.class, Traits.class,
        NetworkDeserializer.class })
@Configuration
public class MaxmindNativeConfiguration {

}
