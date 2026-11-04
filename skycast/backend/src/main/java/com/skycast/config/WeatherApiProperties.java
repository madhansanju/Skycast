package com.skycast.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Upstream endpoints, bound from the {@code weather.api.*} properties. */
@ConfigurationProperties(prefix = "weather.api")
public record WeatherApiProperties(String geocodingUrl, String forecastUrl, int timeoutSeconds) {

    public WeatherApiProperties {
        if (timeoutSeconds <= 0) {
            timeoutSeconds = 5;
        }
    }
}
