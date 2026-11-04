package com.skycast.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** Shapes of the Open-Meteo responses. Only the fields we use are mapped. */
public final class UpstreamModels {

    private UpstreamModels() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeocodingResponse(List<GeocodingResult> results) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeocodingResult(String name, double latitude, double longitude,
                                  String country, String admin1) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ForecastResponse(Current current, Daily daily) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Current(
                @JsonProperty("temperature_2m") double temperature,
                @JsonProperty("apparent_temperature") double apparentTemperature,
                @JsonProperty("relative_humidity_2m") int humidity,
                @JsonProperty("wind_speed_10m") double windSpeed,
                @JsonProperty("weather_code") int weatherCode,
                @JsonProperty("is_day") int isDay) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Daily(
                List<String> time,
                @JsonProperty("weather_code") List<Integer> weatherCode,
                @JsonProperty("temperature_2m_max") List<Double> temperatureMax,
                @JsonProperty("temperature_2m_min") List<Double> temperatureMin,
                @JsonProperty("precipitation_probability_max") List<Integer> precipitationProbability) {
        }
    }
}
