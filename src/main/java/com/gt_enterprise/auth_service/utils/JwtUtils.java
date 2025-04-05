package com.gt_enterprise.auth_service.utils;

import com.gt_enterprise.auth_service.entity.Privilege;
import com.gt_enterprise.auth_service.entity.Role;
import com.gt_enterprise.auth_service.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${security.jwt.secret-key}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpirationMs;

    // Generate JWT token
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        //Get user roles
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        claims.put("roles", roles);

        //Optional. Get privileges
        List<String> privileges = user.getRoles().stream()
                .flatMap(r -> r.getPrivileges().stream())
                .map(Privilege::getName)
                .distinct()
                .toList();

        claims.put("privileges", privileges);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    // Extract username from JWT token
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())) // Use the same secret key used for signing the
                                                                         // token
                .build()
                .parseClaimsJws(token); // Parse the token with the key

        return !isTokenExpired(token);
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        final Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // Extract a single claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
            return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
