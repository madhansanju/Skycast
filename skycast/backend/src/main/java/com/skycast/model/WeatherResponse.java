package com.skycast.model;

import java.time.LocalDate;
import java.util.List;

/** What our own API returns to the frontend. */
public record WeatherResponse(Location location, Current current, List<DayForecast> daily, UnitLabels units) {

    public record Location(String name, String region, String country, double latitude, double longitude) {
    }

    public record Condition(int code, String label, String category) {
    }

    public record Current(double temperature, double feelsLike, int humidity, double windSpeed,
                          boolean daytime, Condition condition) {
    }

    public record DayForecast(LocalDate date, double min, double max,
                              Integer precipitationChance, Condition condition) {
    }

    public record UnitLabels(String temperature, String wind) {
    }
}
