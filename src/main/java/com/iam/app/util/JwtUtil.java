package com.iam.app.util;

import java.util.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iam.app.models.Role;

public class JwtUtil {
    private String accessSecret = "accessSecret";
    private long accessExpiry = 86400000L;
    private long refreshExpiry = 86400000L * 7;
    private String refreshSecret = "refreshSecret";
    ObjectMapper mapper = new ObjectMapper();

    public String generateAccessToken(UUID userId, String email, List<Role> roles) {
        try {
            String rolesJson = mapper.writeValueAsString(roles);
            return JWT.create()
                    .withSubject(userId.toString())
                    .withClaim("email", email)
                    .withClaim("roles", rolesJson)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + accessExpiry))
                    .sign(Algorithm.HMAC256(accessSecret));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate access token", e);
        }
    }

    public String generateRefreshToken(UUID userId) {
        return JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpiry))
                .sign(Algorithm.HMAC256(refreshSecret));
    }

    public DecodedJWT validateAccessToken(String token) {
        return JWT.require(Algorithm.HMAC256(accessSecret))
                .build()
                .verify(token);
    }

    public DecodedJWT validateRefreshToken(String token) {
        return JWT.require(Algorithm.HMAC256(refreshSecret))
                .build()
                .verify(token);
    }

    public UUID extractUserId(String token) {
        DecodedJWT jwt = validateAccessToken(token);
        return UUID.fromString(jwt.getSubject());
    }

    public String extractEmail(String token) {
        DecodedJWT jwt = validateAccessToken(token);
        return jwt.getClaim("email").asString();
    }

    public List<Role> extractRoles(String token) {
        try {
            DecodedJWT jwt = validateAccessToken(token);
            String rolesJson = jwt.getClaim("roles").asString();
            return mapper.convertValue(
                    mapper.readValue(rolesJson, List.class),
                    mapper.getTypeFactory().constructCollectionType(List.class, Role.class)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract roles from token", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            validateAccessToken(token);
            return false;
        } catch (TokenExpiredException e) {
            return true;
        }
    }

    public String refreshAccessToken(String refreshToken, String email, List<Role> roles) {
        DecodedJWT jwt = validateRefreshToken(refreshToken);
        UUID userId = UUID.fromString(jwt.getSubject());
        return generateAccessToken(userId, email, roles);
    }
}
