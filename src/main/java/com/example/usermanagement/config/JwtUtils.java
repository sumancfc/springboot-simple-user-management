package com.example.usermanagement.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private Key getSigninKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Create a Token
    public String generateToken(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate the Token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigninKey())
                    .build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract username from the token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigninKey())
                .build().parseClaimsJws(token)
                .getBody().getSubject();
    }

    public long getRemainingTime(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigninKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            long now = System.currentTimeMillis();

            // Difference in milliseconds
            long diff = expiration.getTime() - now;

            // Convert to seconds (Redis uses seconds for TTL)
            return diff > 0 ? diff / 1000 : 0;
        } catch (Exception e) {
            // If parsing fails or token is already expired
            return 0;
        }
    }
}
