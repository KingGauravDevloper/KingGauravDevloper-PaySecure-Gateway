package com.paysecure.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // Explicitly allow your Frontend URL
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        corsConfig.setMaxAge(3600L); // Cache the check for 1 hour
        corsConfig.addAllowedMethod("*"); // Allow GET, POST, PUT, DELETE, OPTIONS
        corsConfig.addAllowedHeader("*"); // Allow all headers (Authorization, Content-Type)
        corsConfig.setAllowCredentials(true); // Allow cookies/tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply to ALL routes

        return new CorsWebFilter(source);
    }
}