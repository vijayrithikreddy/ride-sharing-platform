package com.rideshare.apigatewayservice.filter;

import com.rideshare.apigatewayservice.config.AuthorizationConfig;
import com.rideshare.apigatewayservice.exception.ForbiddenException;
import com.rideshare.apigatewayservice.exception.UnauthorizedException;
import com.rideshare.apigatewayservice.util.JwtUtil;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private final JwtUtil jwtUtil;
    private final AuthorizationConfig authorizationConfig;
    private static final List<String> PUBLIC_APIS = List.of(
            // Authentication
            "/api/auth/signup",
            "/api/auth/verifyOtp",
            "/api/auth/resend-otp",
            "/api/auth/login",
            "/api/auth/refresh-token",

            // API Documentation
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/webjars/**",

            // Actuator (optional for development)
            "/actuator/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path =
                exchange.getRequest()
                        .getURI()
                        .getPath();

        boolean isPublicApi =
                PUBLIC_APIS.stream()
                        .anyMatch(path::startsWith);
        if (isPublicApi){
            log.info("public api : {} ", path);
            return chain.filter(exchange);
        }

        String authorizationHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(
                                HttpHeaders.AUTHORIZATION
                        );
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

            throw new UnauthorizedException("Authorization Header Missing");
        }
        String token =
                authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {

            throw new UnauthorizedException(
                    "Invalid JWT Token"
            );
        }
        String email = jwtUtil.retrieveEmailFromToken(token);
        UUID id = jwtUtil.retrieveUserIdFromToken(token);

        String role = jwtUtil.retrieveRolesFromToken(token);

        log.info(
                "Authenticated User : {}",
                email
        );

        log.info(
                "Role : {}",
                role
        );


        String method =
                exchange.getRequest()
                        .getMethod()
                        .name();

        String route = determineRoute(path);

        String routeKey =
                method + ":" + route;

        List<String> allowedRoles =
                authorizationConfig
                        .routeRoles()
                        .get(routeKey);

        if (allowedRoles != null) {

            boolean authorized = allowedRoles.contains(role);

            if (!authorized) {

                throw new ForbiddenException(
                        "Access Denied"
                );
            }
        }
        return chain.filter(
                exchange.mutate()
                        .request(exchange.getRequest()
                                .mutate()
                                .header("X-User-Id", String.valueOf(id))
                                .build())
                        .build());
    }
    private String determineRoute(String path) {

        if (path.startsWith("/api/auth")) {
            return "/api/auth";
        }

        if (path.startsWith("/api/userprofiles")) {
            return "/api/userprofiles";
        }

        if (path.startsWith("/api/vehicles")) {
            return "/api/vehicles";
        }

        if (path.startsWith("/api/rides")) {
            return "/api/rides";
        }

        if (path.startsWith("/api/ride-requests")) {
            return "/api/ride-requests";
        }

        return "";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
