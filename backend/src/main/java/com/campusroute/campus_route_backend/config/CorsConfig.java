package com.campusroute.campus_route_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Lets the React front end (Vite or Create React App, dev only) read the API with GET requests. */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final String VITE_ORIGIN = "http://localhost:5173";
    private static final String CRA_ORIGIN = "http://localhost:3000";

    // Only /api/** is opened: Swagger and everything else keep the default (same-origin) policy
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(VITE_ORIGIN, CRA_ORIGIN)
                .allowedMethods("GET");
    }
}
