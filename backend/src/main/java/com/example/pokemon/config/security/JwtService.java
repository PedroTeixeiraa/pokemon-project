package com.example.pokemon.config.security;

import com.example.pokemon.service.auth.TokenIssuer;
import com.example.pokemon.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService implements TokenIssuer {
    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtService(String secret, long expirationMinutes) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT secret must contain at least 32 bytes");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    @Autowired
    public JwtService(JwtProperties properties) {
        this(properties.secret(), properties.expirationMinutes());
    }

    @Override
    public String issue(User user) {
        return createToken(String.valueOf(user.id()), user.email());
    }

    public String createToken(String userId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    public String userId(String token) {
        return claims(token).getSubject();
    }

    public String email(String token) {
        return claims(token).get("email", String.class);
    }

    public Claims claims(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }
}
