package com.paysecure.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    // FIX: Changed from ${jwt.jwt.secret} to ${jwt.secret}
    @Value("${jwt.secret}")
    private String secret;

    // Validates the token's signature
    public void validateToken(final String token) {
        // Validation will now work because the secret string matches the key bytes
        Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
    }

    // Gets the signing key from the Base64 secret
    private Key getSigningKey() {
        // CRITICAL: This correctly decodes the Base64 string into bytes for HMAC signing.
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // Optional: Add a method to extract username if needed downstream
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}