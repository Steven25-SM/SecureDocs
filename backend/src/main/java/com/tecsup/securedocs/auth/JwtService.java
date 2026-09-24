package com.tecsup.securedocs.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
        this.expiration = expiration;
    }

    public String generateToken(String correo, String role) {

        Date now = new Date();

        return Jwts.builder()
                .subject(correo)
                .claim("role", role)
                .issuedAt(now)
                .expiration(
                        new Date(now.getTime() + expiration)
                )
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {

        return parseClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(String token) {

        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}