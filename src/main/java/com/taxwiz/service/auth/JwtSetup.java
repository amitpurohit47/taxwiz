package com.taxwiz.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


@Slf4j
@Service
public class JwtSetup {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(Map<String, Object> claims, String subject, Duration expiry) throws InvalidKeyException, IllegalArgumentException {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiry.toMillis()))
                .signWith(buildKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            if (isTokenExpired(token)) {
                log.error("Token Expired");
                return false;
            }
            return validateToken(token, username);
        } catch (ExpiredJwtException e) {
            log.error("Token Expired");
            return false;
        } catch (Exception e) {
            log.error("Error while validating token {}", e.getMessage());
            return false;
        }
    }

    private boolean validateToken(String token, String username) {
        if (!extractClaim(token, Claims::getSubject).equals(username)) return false;
        try {
            Jwts.parserBuilder()
                    .setSigningKey(buildKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (SignatureException e) {
            log.info("Signature verification failed");
            return false;
        }
        return true;
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractClaims(token);
        return resolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    private Date getExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(buildKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key buildKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
