package com.skycast.controller;

import com.skycast.model.Units;
import com.skycast.model.WeatherResponse;
import com.skycast.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WeatherController {

    private static final int MAX_CITY_LENGTH = 100;

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /** Example: {@code GET /api/weather?city=Lisbon&units=metric} */
    @GetMapping("/weather")
    public WeatherResponse weather(@RequestParam String city,
                                   @RequestParam(defaultValue = "metric") String units) {
        if (city.isBlank()) {
            throw new IllegalArgumentException("Enter a city name.");
        }
        if (city.length() > MAX_CITY_LENGTH) {
            throw new IllegalArgumentException("City name must be " + MAX_CITY_LENGTH + " characters or fewer.");
        }
        return weatherService.getWeather(city, Units.from(units));
    }
}
