package com.rideshare.authservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.rideshare.authservice.entity.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.validity.accessToken}")
    private Long ACCESS_TOKEN_VALIDITY_DURATION;
    @Value("${jwt.validity.refreshToken}")
    private Long REFRESH_TOKEN_VALIDITY_DURATION;
    @Value("${jwt.secret}")
    private String SECRET;

    public String generateAccessToken(AuthUser user){
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("role",user.getRole().toString())
                .withClaim("issued_date",new Date())
                .withIssuer("RideShare")
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_DURATION))
                .sign(algorithm);
    }
    public String generateRefreshToken(AuthUser user){
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("role",user.getRole().toString())
                .withClaim("issued_date",new Date())
                .withIssuer("RideShare")
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_DURATION))
                .sign(algorithm);
    }
}
