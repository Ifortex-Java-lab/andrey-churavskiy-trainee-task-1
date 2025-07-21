package com.example.task1.service;

import com.example.task1.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
  String generateToken(User user);

  String extractEmail(String token);

  boolean validateToken(String token, UserDetails userDetails);
}
