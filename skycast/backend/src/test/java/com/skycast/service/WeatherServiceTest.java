package com.skycast.service;

import com.skycast.client.OpenMeteoClient;
import com.skycast.exception.CityNotFoundException;
import com.skycast.model.Units;
import com.skycast.model.UpstreamModels.ForecastResponse;
import com.skycast.model.UpstreamModels.GeocodingResult;
import com.skycast.model.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private OpenMeteoClient client;

    @Test
    void combinesGeocodingAndForecast() {
        GeocodingResult place = new GeocodingResult("Lisbon", 38.72, -9.14, "Portugal", "Lisbon");
        ForecastResponse forecast = new ForecastResponse(
                new ForecastResponse.Current(21.5, 20.0, 60, 12.0, 2, 1),
                new ForecastResponse.Daily(
                        List.of("2026-11-04", "2026-11-05"),
                        List.of(2, 61),
                        List.of(23.0, 19.0),
                        List.of(14.0, 13.0),
                        List.of(5, 70)));

        when(client.geocode("Lisbon")).thenReturn(Optional.of(place));
        when(client.forecast(anyDouble(), anyDouble(), eq(Units.METRIC))).thenReturn(forecast);

        WeatherResponse result = new WeatherService(client).getWeather("  Lisbon ", Units.METRIC);

        assertThat(result.location().name()).isEqualTo("Lisbon");
        assertThat(result.current().daytime()).isTrue();
        assertThat(result.current().condition().label()).isEqualTo("Partly cloudy");
        assertThat(result.daily()).hasSize(2);
        assertThat(result.daily().get(1).date()).isEqualTo(LocalDate.of(2026, 11, 5));
        assertThat(result.daily().get(1).condition().category()).isEqualTo("rain");
        assertThat(result.daily().get(1).precipitationChance()).isEqualTo(70);
        assertThat(result.units().temperature()).isEqualTo("°C");
    }

    @Test
    void throwsWhenCityIsUnknown() {
        when(client.geocode("Nowhereville")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new WeatherService(client).getWeather("Nowhereville", Units.METRIC))
                .isInstanceOf(CityNotFoundException.class);
    }
}
