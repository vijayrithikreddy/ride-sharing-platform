package com.rideshare.authservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rideshare.authservice.entity.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final String ROLE_TAG = "role";

    @Value("${jwt.validity.accessToken}")
    private Long ACCESS_TOKEN_VALIDITY_DURATION;

    @Value("${jwt.validity.refreshToken}")
    private Long REFRESH_TOKEN_VALIDITY_DURATION;

    @Value("${jwt.secret}")
    private String SECRET;

    private DecodedJWT getDecodedToken(String token) {

        Algorithm algorithm =
                Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));

        JWTVerifier verifier =
                JWT.require(algorithm)
                        .withIssuer("RideShare")
                        .build();

        return verifier.verify(token);
    }

    public String generateAccessToken(AuthUser user) {

        Algorithm algorithm =
                Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("user_id", user.getId().toString())
                .withClaim("role", user.getRole().toString())
                .withClaim("issued_date", new Date())
                .withIssuer("RideShare")
                .withExpiresAt(new Date(System.currentTimeMillis()
                        + ACCESS_TOKEN_VALIDITY_DURATION))
                .sign(algorithm);
    }

    public String generateRefreshToken(AuthUser user) {

        Algorithm algorithm =
                Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("user_id", user.getId().toString())
                .withClaim("role", user.getRole().toString())
                .withClaim("issued_date", new Date())
                .withIssuer("RideShare")
                .withExpiresAt(new Date(System.currentTimeMillis()
                        + REFRESH_TOKEN_VALIDITY_DURATION))
                .sign(algorithm);
    }

    public boolean validateToken(String token) {

        try {

            getDecodedToken(token);

            return true;

        } catch (JWTVerificationException ex) {

            return false;

        }

    }

    public String retrieveEmailFromToken(String token) {

        return getDecodedToken(token)
                .getSubject();

    }

    public UUID retrieveUserIdFromToken(String token) {

        String userId = getDecodedToken(token)
                .getClaim("user_id")
                .asString();

        return UUID.fromString(userId);
    }

    public String retrieveRoleFromToken(String token) {

        return getDecodedToken(token)
                .getClaim(ROLE_TAG)
                .asString();

    }

}