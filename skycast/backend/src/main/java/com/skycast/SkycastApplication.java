package com.skycast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SkycastApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkycastApplication.class, args);
    }
}
