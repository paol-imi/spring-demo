package com.example.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.web.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${app.web.cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${app.web.cors.allowed-headers}")
    private List<String> allowedHeaders;

    @Value("${app.web.cors.allow-credentials}")
    private boolean allowCredentials;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(this.allowCredentials);
        config.setAllowedOrigins(this.allowedOrigins);
        config.setAllowedHeaders(this.allowedHeaders);
        config.setAllowedMethods(this.allowedMethods);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}