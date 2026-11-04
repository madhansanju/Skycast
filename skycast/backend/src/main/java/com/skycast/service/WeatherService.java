package com.skycast.service;

import com.skycast.client.OpenMeteoClient;
import com.skycast.exception.CityNotFoundException;
import com.skycast.model.Units;
import com.skycast.model.UpstreamModels.ForecastResponse;
import com.skycast.model.UpstreamModels.GeocodingResult;
import com.skycast.model.WeatherCodes;
import com.skycast.model.WeatherResponse;
import com.skycast.model.WeatherResponse.Current;
import com.skycast.model.WeatherResponse.DayForecast;
import com.skycast.model.WeatherResponse.Location;
import com.skycast.model.WeatherResponse.UnitLabels;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private final OpenMeteoClient client;

    public WeatherService(OpenMeteoClient client) {
        this.client = client;
    }

    /** Results are cached per city and unit system (see spring.cache.caffeine.spec). */
    @Cacheable(cacheNames = "weather", key = "#city.trim().toLowerCase() + ':' + #units.name()")
    public WeatherResponse getWeather(String city, Units units) {
        String query = city.trim();
        GeocodingResult place = client.geocode(query)
                .orElseThrow(() -> new CityNotFoundException(query));
        ForecastResponse forecast = client.forecast(place.latitude(), place.longitude(), units);

        return new WeatherResponse(
                new Location(place.name(), place.admin1(), place.country(),
                        place.latitude(), place.longitude()),
                toCurrent(forecast.current()),
                toDaily(forecast.daily()),
                new UnitLabels(units.temperatureSymbol(), units.windSymbol()));
    }

    private Current toCurrent(ForecastResponse.Current c) {
        return new Current(
                c.temperature(),
                c.apparentTemperature(),
                c.humidity(),
                c.windSpeed(),
                c.isDay() == 1,
                WeatherCodes.describe(c.weatherCode()));
    }

    private List<DayForecast> toDaily(ForecastResponse.Daily d) {
        List<DayForecast> days = new ArrayList<>();
        for (int i = 0; i < d.time().size(); i++) {
            Integer rainChance = d.precipitationProbability() == null
                    ? null : d.precipitationProbability().get(i);
            days.add(new DayForecast(
                    LocalDate.parse(d.time().get(i)),
                    d.temperatureMin().get(i),
                    d.temperatureMax().get(i),
                    rainChance,
                    WeatherCodes.describe(d.weatherCode().get(i))));
        }
        return days;
    }
}
