package com.rideshare.apigatewayservice.config;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AuthorizationConfig {
    private final Map<String, List<String>> ROUTE_ROLES = Map.ofEntries(

            // ==========================
            // USER PROFILE
            // ==========================

            Map.entry(
                    "GET:/api/userprofiles",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "PUT:/api/userprofiles",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "GET:/api/userprofiles/summary",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "POST:/api/userprofiles/summaries",
                    List.of("ROLE_USER")
            ),

            // ==========================
            // VEHICLE
            // ==========================

            Map.entry(
                    "POST:/api/vehicles",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "PUT:/api/vehicles",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "GET:/api/vehicles",
                    List.of("ROLE_USER")
            ),

            // ==========================
            // RIDES
            // ==========================

            Map.entry(
                    "POST:/api/rides/publish",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "PUT:/api/rides/update",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "PUT:/api/rides/cancel",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "PUT:/api/rides/start",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "PUT:/api/rides/complete",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "GET:/api/rides/active",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "GET:/api/rides/history",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "POST:/api/rides/search",
                    List.of("ROLE_USER")
            ),

            // ==========================
            // RIDE REQUESTS
            // ==========================

            Map.entry(
                    "POST:/api/ride-requests/request",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "PUT:/api/ride-requests/cancel",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "PUT:/api/ride-requests/accept",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "PUT:/api/ride-requests/reject",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "GET:/api/ride-requests/sent",
                    List.of("ROLE_USER")
            ),

            Map.entry(
                    "GET:/api/ride-requests/received",
                    List.of("ROLE_USER")
            )
    );

    public Map<String, List<String>> routeRoles() {

        return ROUTE_ROLES;
    }
}