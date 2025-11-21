package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // For dev only: keep secret in properties; change for prod
    private final Key key;
    private final long validityMs = 1000L * 60 * 60 * 24; // 24h

    public JwtUtil() {
        // short sample secret; override via application properties for real project
        byte[] secret = "replace-this-with-a-long-random-secret-for-prod-please".getBytes();
        this.key = Keys.hmacShaKeyFor(secret);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}