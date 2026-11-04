package com.skycast.client;

import com.skycast.config.WeatherApiProperties;
import com.skycast.exception.UpstreamException;
import com.skycast.model.Units;
import com.skycast.model.UpstreamModels.ForecastResponse;
import com.skycast.model.UpstreamModels.GeocodingResponse;
import com.skycast.model.UpstreamModels.GeocodingResult;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Optional;

/** Thin wrapper around the two Open-Meteo endpoints we use. */
@Component
public class OpenMeteoClient {

    private static final String CURRENT_FIELDS =
            "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code,is_day";
    private static final String DAILY_FIELDS =
            "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max";

    private final RestClient geocodingClient;
    private final RestClient forecastClient;

    public OpenMeteoClient(WeatherApiProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        Duration timeout = Duration.ofSeconds(props.timeoutSeconds());
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);

        this.geocodingClient = RestClient.builder()
                .baseUrl(props.geocodingUrl()).requestFactory(factory).build();
        this.forecastClient = RestClient.builder()
                .baseUrl(props.forecastUrl()).requestFactory(factory).build();
    }

    public Optional<GeocodingResult> geocode(String city) {
        try {
            GeocodingResponse response = geocodingClient.get()
                    .uri(uri -> uri.path("/v1/search")
                            .queryParam("name", "{name}")
                            .queryParam("count", 1)
                            .queryParam("language", "en")
                            .queryParam("format", "json")
                            .build(city))
                    .retrieve()
                    .body(GeocodingResponse.class);

            if (response == null || response.results() == null || response.results().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(response.results().get(0));
        } catch (RestClientException e) {
            throw new UpstreamException("Geocoding request failed", e);
        }
    }

    public ForecastResponse forecast(double latitude, double longitude, Units units) {
        ForecastResponse response;
        try {
            response = forecastClient.get()
                    .uri(uri -> uri.path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", CURRENT_FIELDS)
                            .queryParam("daily", DAILY_FIELDS)
                            .queryParam("temperature_unit", units.temperatureParam())
                            .queryParam("wind_speed_unit", units.windParam())
                            .queryParam("timezone", "auto")
                            .queryParam("forecast_days", 7)
                            .build())
                    .retrieve()
                    .body(ForecastResponse.class);
        } catch (RestClientException e) {
            throw new UpstreamException("Forecast request failed", e);
        }

        if (response == null || response.current() == null || response.daily() == null) {
            throw new UpstreamException("Forecast response was empty", null);
        }
        return response;
    }
}
