package com.keerthivasan.crud.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Secret key used for signing and verifying JWT tokens
    // In production, store securely and do not hardcode
    private final String SECRET_KEY = "keerthivasandfghjfghjfghjfghjfghjghjghjghjghj";

    /**
     * Extracts the username (subject) from the JWT token.
     * Returns null if token parsing fails (invalid or expired token).
     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (JwtException e) {
            // Log or handle invalid token scenarios here if needed
            return null;
        }
    }

    /**
     * Extracts the expiration date from the JWT token.
     * Returns null if token parsing fails.
     */
    public Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Generic method to extract a specific claim from the token using the provided resolver function.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the token and extracts all claims.
     * Throws ExpiredJwtException if token expired,
     * Throws JwtException for any other parsing issues.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Token expired - propagate exception to handle differently if needed
            throw e;
        } catch (JwtException e) {
            // Other parsing exceptions (malformed, signature invalid, etc.)
            throw e;
        }
    }

    /**
     * Checks if the token has expired.
     * Returns true if token expired or expiration date cannot be retrieved.
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration == null || expiration.before(new Date());
    }

    /**
     * Generates a JWT token for the authenticated user.
     * Adds the user's first authority (role) as a custom claim.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Get the first role/authority or default to ROLE_USER
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");
        claims.put("role", role); // Add role as claim
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Builds the JWT token with claims, subject (username), issue date, expiration, and signs it.
     * Token valid for 10 hours.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Additional claims
                .setSubject(subject) // Username as subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Current time as issue date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiration
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign with HMAC SHA-256 and secret key
                .compact();
    }

    /**
     * Validates the JWT token:
     * - Username in token must match the UserDetails username.
     * - Token must not be expired.
     * Returns false if token is invalid or expired.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            // Token invalid or expired, so validation fails
            return false;
        }
    }
}
