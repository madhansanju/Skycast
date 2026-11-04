package com.skycast.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/** Turns on {@code @Cacheable}. Cache sizing and expiry live in application.properties. */
@Configuration
@EnableCaching
public class CacheConfig {
}
