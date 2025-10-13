package com.example.departement.util;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SuppressWarnings("unused")
@Component
public class JwtUtils {

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Jwts.SIG.HS256.key().build();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.err.println("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted: " + e.getMessage());
            return false;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }
}
