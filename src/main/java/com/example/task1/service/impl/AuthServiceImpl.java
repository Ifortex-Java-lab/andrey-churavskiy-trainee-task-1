package com.example.task1.service.impl;

import com.example.task1.dto.auth.AuthRequestDto;
import com.example.task1.dto.auth.AuthResponseDto;
import com.example.task1.dto.auth.RegisterRequestDto;
import com.example.task1.dto.user.UserRequestDto;
import com.example.task1.entity.User;
import com.example.task1.exception.EmailAlreadyExistsException;
import com.example.task1.exception.InvalidCredentialsException;
import com.example.task1.exception.UserNotFoundException;
import com.example.task1.repository.UserRepository;
import com.example.task1.service.AuthService;
import com.example.task1.service.JwtService;
import com.example.task1.service.SubscriptionService;
import com.example.task1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserService userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final SubscriptionService subscriptionService;

  public AuthResponseDto register(RegisterRequestDto request) {
    log.info("Attempting to register user with email: {}", request.getEmail());
    if (userRepository.existsByEmail(request.getEmail())) {
      log.warn("Registration failed: email already exists: {}", request.getEmail());
      throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
    }
    User user =
        userService.createUser(
            new UserRequestDto(request.getEmail(), passwordEncoder.encode(request.getPassword())));
    log.info("User registered successfully with email: {}", user.getEmail());

    String token = jwtService.generateToken(user);
    log.debug("Generated JWT token for user {}: {}", user.getEmail(), token);
    return new AuthResponseDto(token);
  }

  public AuthResponseDto login(AuthRequestDto request) {
    log.info("Attempting to login user with email: {}", request.getEmail());
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(
                () -> {
                  log.warn("Login failed: user not found with email: {}", request.getEmail());
                  return new UserNotFoundException("User not found");
                });
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      log.warn("Login failed: invalid credentials for email: {}", request.getEmail());
      throw new InvalidCredentialsException("Invalid credentials");
    }
    log.info("User logged in successfully: {}", user.getEmail());

    subscriptionService.syncSubscriptionsFromStripe(user);

    String token = jwtService.generateToken(user);
    log.debug("Generated JWT token for user {}: {}", user.getEmail(), token);
    return new AuthResponseDto(token);
  }
}
