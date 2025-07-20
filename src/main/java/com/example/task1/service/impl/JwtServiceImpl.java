package com.example.task1.service.impl;

import com.example.task1.entity.User;
import com.example.task1.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
  @Value("${jwt.secret}")
  private String jwtSecretString;

  private Key jwtSecretKey;

  @PostConstruct
  public void init() {
    this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
    log.info("JWT secret key initialized");
  }

  public String generateToken(User user) {
    log.info("Generating JWT token for user: {}", user.getEmail());
    Date now = new Date();
    Date expiry = new Date(now.getTime() + 1000 * 60 * 60 * 24);
    String token =
        Jwts.builder()
            .setSubject(user.getEmail())
            .claim("role", user.getRole().name())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
            .compact();
    log.debug("JWT token generated for {}: {}", user.getEmail(), token);
    return token;
  }

  public String extractEmail(String token) {
    log.info("Extracting email from JWT token");
    String email = extractAllClaims(token).getSubject();
    log.debug("Extracted email from token: {}", email);
    return email;
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    log.info("Validating JWT token for user: {}", userDetails.getUsername());
    String email = extractEmail(token);
    boolean isValid = (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    log.debug("Token validation result for {}: {}", userDetails.getUsername(), isValid);
    return isValid;
  }

  private boolean isTokenExpired(String token) {
    boolean expired = extractAllClaims(token).getExpiration().before(new Date());
    log.debug("Token expired: {}", expired);
    return expired;
  }

  private Claims extractAllClaims(String token) {
    log.debug("Extracting all claims from JWT token");
    Claims claims =
        Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token).getBody();
    log.trace("Extracted claims: {}", claims);
    return claims;
  }
}
