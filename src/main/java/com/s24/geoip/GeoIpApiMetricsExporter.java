package com.s24.geoip;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

import com.maxmind.geoip2.DatabaseReader;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Component
public class GeoIpApiMetricsExporter {

    public GeoIpApiMetricsExporter(MeterRegistry registry, Collection<DatabaseReader> databases) {
        for (DatabaseReader db : databases) {
            Tags tags = Tags.of(
                "type", db.getMetadata().getDatabaseType().toLowerCase());

            registry.gauge("geoip.database", tags, 1d);
            registry.gauge("geoip.database_age_days", tags, db.getMetadata(), m -> {
                long ageInMillis = Math.abs(new Date().getTime() - m.getBuildDate().getTime());
                return TimeUnit.DAYS.convert(ageInMillis, TimeUnit.MILLISECONDS);
            });
        }
    }

}
