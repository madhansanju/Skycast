package com.skycast.exception;

public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String city) {
        super("No place found for \"" + city + "\". Check the spelling or try a larger nearby city.");
    }
}
