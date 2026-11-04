package com.skycast.model;

import com.skycast.model.WeatherResponse.Condition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherCodesTest {

    @Test
    void mapsKnownCodes() {
        assertThat(WeatherCodes.describe(0).category()).isEqualTo("clear");
        assertThat(WeatherCodes.describe(63).category()).isEqualTo("rain");
        assertThat(WeatherCodes.describe(73).category()).isEqualTo("snow");
        assertThat(WeatherCodes.describe(95).category()).isEqualTo("storm");
    }

    @Test
    void fallsBackForUnknownCodes() {
        Condition condition = WeatherCodes.describe(1234);
        assertThat(condition.label()).isEqualTo("Unknown");
        assertThat(condition.code()).isEqualTo(1234);
    }
}
