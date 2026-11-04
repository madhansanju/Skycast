package com.skycast.controller;

import com.skycast.exception.CityNotFoundException;
import com.skycast.model.Units;
import com.skycast.model.WeatherCodes;
import com.skycast.model.WeatherResponse;
import com.skycast.model.WeatherResponse.Current;
import com.skycast.model.WeatherResponse.DayForecast;
import com.skycast.model.WeatherResponse.Location;
import com.skycast.model.WeatherResponse.UnitLabels;
import com.skycast.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void returnsWeatherForCity() throws Exception {
        WeatherResponse response = new WeatherResponse(
                new Location("Lisbon", "Lisbon", "Portugal", 38.72, -9.14),
                new Current(21.5, 20.0, 60, 12.0, true, WeatherCodes.describe(0)),
                List.of(new DayForecast(LocalDate.of(2026, 11, 4), 14.0, 23.0, 5, WeatherCodes.describe(0))),
                new UnitLabels("°C", "km/h"));
        when(weatherService.getWeather("Lisbon", Units.METRIC)).thenReturn(response);

        mockMvc.perform(get("/api/weather").param("city", "Lisbon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location.name").value("Lisbon"))
                .andExpect(jsonPath("$.current.daytime").value(true))
                .andExpect(jsonPath("$.current.condition.category").value("clear"))
                .andExpect(jsonPath("$.daily[0].date").value("2026-11-04"));
    }

    @Test
    void returns404WhenCityIsUnknown() throws Exception {
        when(weatherService.getWeather(eq("Nowhereville"), any(Units.class)))
                .thenThrow(new CityNotFoundException("Nowhereville"));

        mockMvc.perform(get("/api/weather").param("city", "Nowhereville"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("City not found"));
    }

    @Test
    void returns400ForBlankCity() throws Exception {
        mockMvc.perform(get("/api/weather").param("city", "   "))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(weatherService);
    }

    @Test
    void returns400ForUnknownUnits() throws Exception {
        mockMvc.perform(get("/api/weather").param("city", "Lisbon").param("units", "kelvin"))
                .andExpect(status().isBadRequest());
    }
}
