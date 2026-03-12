package com.akillibutce.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey anahtarOlustur() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String tokenUret(UserDetails kullaniciDetaylari) {
        return Jwts.builder()
                .subject(kullaniciDetaylari.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(anahtarOlustur())
                .compact();
    }

    public String epostaGetir(String token) {
        return Jwts.parser()
                .verifyWith(anahtarOlustur())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean tokenGecerliMi(String token) {
        try {
            Jwts.parser()
                    .verifyWith(anahtarOlustur())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Gecersiz JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token suresi dolmus: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Desteklenmeyen JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT token bos: {}", e.getMessage());
        }
        return false;
    }
}
