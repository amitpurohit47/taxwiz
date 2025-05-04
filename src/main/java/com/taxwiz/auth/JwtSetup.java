package com.taxwiz.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtSetup {
    private final String SECRET_KEY = "a9D9Q9wvm2hLkS9UT+p8fyLZQJ0V4CfV6sbfwl3jwM0=";
    private final long expirationTime = 1000 * 60 * 30; // 1 day

    public String generateToken(String firmId, String purpose) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("firmId", firmId);
        claims.put("purpose",purpose);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(firmId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims extractClaim(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            extractClaim(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
