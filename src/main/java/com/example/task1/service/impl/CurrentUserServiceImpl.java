package com.example.task1.service.impl;

import com.example.task1.entity.User;
import com.example.task1.exception.UserNotFoundException;
import com.example.task1.repository.UserRepository;
import com.example.task1.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

  private final UserRepository userRepository;

  @Override
  public User getCurrentUserEntity() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    log.info("Attempting to get current user entity for email: {}", email);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> {
                  log.warn("User not found with email: {}", email);
                  return new UserNotFoundException("User not found with email:" + email);
                });
    log.info("Successfully retrieved user entity for email: {}", email);
    return user;
  }
}
