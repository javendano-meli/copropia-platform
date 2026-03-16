package com.copropia.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("lb://auth-service"))
                .route("copropiedad-service", r -> r
                        .path("/api/copropiedades/**", "/api/propiedades/**", "/api/planes/**")
                        .uri("lb://copropiedad-service"))
                .route("asamblea-service", r -> r
                        .path("/api/asambleas/**", "/api/votaciones/**", "/api/votos/**")
                        .uri("lb://asamblea-service"))
                .route("zona-comun-service", r -> r
                        .path("/api/zonas-comunes/**", "/api/reservas/**")
                        .uri("lb://zona-comun-service"))
                .build();
    }
}
