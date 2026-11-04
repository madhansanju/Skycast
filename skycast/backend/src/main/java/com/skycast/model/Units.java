package com.skycast.model;

import java.util.Locale;

public enum Units {
    METRIC("celsius", "kmh", "°C", "km/h"),
    IMPERIAL("fahrenheit", "mph", "°F", "mph");

    private final String temperatureParam;
    private final String windParam;
    private final String temperatureSymbol;
    private final String windSymbol;

    Units(String temperatureParam, String windParam, String temperatureSymbol, String windSymbol) {
        this.temperatureParam = temperatureParam;
        this.windParam = windParam;
        this.temperatureSymbol = temperatureSymbol;
        this.windSymbol = windSymbol;
    }

    public String temperatureParam() { return temperatureParam; }
    public String windParam() { return windParam; }
    public String temperatureSymbol() { return temperatureSymbol; }
    public String windSymbol() { return windSymbol; }

    /** Parses user input such as "metric" or "Imperial". */
    public static Units from(String value) {
        if (value == null || value.isBlank()) {
            return METRIC;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Units must be 'metric' or 'imperial'.");
        }
    }
}
