package com.skycast.model;

import com.skycast.model.WeatherResponse.Condition;

/**
 * Translates WMO weather codes (as used by Open-Meteo) into a label and a coarse category.
 * The category drives the sky theme in the frontend: clear, cloudy, fog, rain, snow or storm.
 */
public final class WeatherCodes {

    private WeatherCodes() {
    }

    public static Condition describe(int code) {
        return switch (code) {
            case 0 -> new Condition(code, "Clear sky", "clear");
            case 1 -> new Condition(code, "Mostly clear", "clear");
            case 2 -> new Condition(code, "Partly cloudy", "cloudy");
            case 3 -> new Condition(code, "Overcast", "cloudy");
            case 45, 48 -> new Condition(code, "Fog", "fog");
            case 51, 53, 55 -> new Condition(code, "Drizzle", "rain");
            case 56, 57 -> new Condition(code, "Freezing drizzle", "rain");
            case 61 -> new Condition(code, "Light rain", "rain");
            case 63 -> new Condition(code, "Rain", "rain");
            case 65 -> new Condition(code, "Heavy rain", "rain");
            case 66, 67 -> new Condition(code, "Freezing rain", "rain");
            case 71 -> new Condition(code, "Light snow", "snow");
            case 73 -> new Condition(code, "Snow", "snow");
            case 75 -> new Condition(code, "Heavy snow", "snow");
            case 77 -> new Condition(code, "Snow grains", "snow");
            case 80, 81 -> new Condition(code, "Rain showers", "rain");
            case 82 -> new Condition(code, "Violent rain showers", "rain");
            case 85, 86 -> new Condition(code, "Snow showers", "snow");
            case 95 -> new Condition(code, "Thunderstorm", "storm");
            case 96, 99 -> new Condition(code, "Thunderstorm with hail", "storm");
            default -> new Condition(code, "Unknown", "cloudy");
        };
    }
}
