package com.vaultchecker.platform.shared.infrastructure.time.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimeZoneConfiguration {

    public TimeZoneConfiguration(@Value("${app.time-zone}") String timeZone) {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }
}
