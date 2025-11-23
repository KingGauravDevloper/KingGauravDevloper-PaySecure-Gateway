package com.paysecure.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paysecure.gateway.filter.AuthenticationFilter; // Import the filter factory

@Configuration
public class GatewayConfig {

    // Inject the AuthenticationFilter Factory directly
    private final AuthenticationFilter authFilter;

    public GatewayConfig(AuthenticationFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        // Create an empty config object required by the filter factory
        AuthenticationFilter.Config filterConfig = new AuthenticationFilter.Config();

        return builder.routes()
                
                // Route 1: Auth Service (Public Endpoints)
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("http://localhost:8081"))
                
                // Route 2: Transaction Service (Secured)
                .route("transaction-service", r -> r.path("/api/transactions/**")
                        // FIX: Use the apply() method of the AuthenticationFilter factory
                        .filters(f -> f.filter(authFilter.apply(filterConfig))) 
                        .uri("http://localhost:8082"))
                
                // Route 3: Orchestrator Service (Secured)
                .route("orchestration-service", r -> r.path("/api/payments/**")
                        // FIX: Use the apply() method of the AuthenticationFilter factory
                        .filters(f -> f.filter(authFilter.apply(filterConfig))) 
                        .uri("http://localhost:8084"))

                // Route 4: Notification Service (Secured)
                .route("notification-route", r -> r.path("/notifications/**")
                        // FIX: Use the apply() method of the AuthenticationFilter factory
                        .filters(f -> f.filter(authFilter.apply(filterConfig))) 
                        .uri("http://localhost:8083"))
                
                .build();
    }
}