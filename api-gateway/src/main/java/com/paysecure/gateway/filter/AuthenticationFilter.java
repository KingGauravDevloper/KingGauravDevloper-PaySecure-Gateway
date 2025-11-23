package com.paysecure.gateway.filter;

import com.paysecure.gateway.security.JwtUtil;
import com.paysecure.gateway.security.RouterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;
import java.util.List; // Ensure this is imported if used in getHeaders().get(0)

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouterValidator routerValidator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    // This method returns the actual request filter logic
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            
            if (routerValidator.isSecured.test(exchange.getRequest())) {
                
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return this.onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
                }

                // Use getFirst() instead of get(0) for safety
                final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION); 
                
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
                }
                
                final String token = authHeader.substring(7);

                try {
                    jwtUtil.validateToken(token);
                    // Optionally: Add validated user details to the header for downstream services
                    // String username = jwtUtil.extractUsername(token);
                    // exchange.getRequest().mutate().header("X-Auth-User", username).build();
                } catch (JwtException e) {
                    return this.onError(exchange, "Unauthorized: Token Invalid", HttpStatus.UNAUTHORIZED);
                }
            }
            
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        return response.writeWith(Mono.just(response.bufferFactory().wrap(err.getBytes())));
    }

    public static class Config {
        // Configuration placeholder
    }
}