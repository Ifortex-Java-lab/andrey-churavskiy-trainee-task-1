package com.example.task1.controller;

import com.example.task1.dto.user.UserResponseDto;
import com.example.task1.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/all")
  public List<UserResponseDto> getAllUsers() {
    log.info("Received request to get all users");
    List<UserResponseDto> users = userService.getAllUsers();
    log.debug("Returning {} users", users.size());
    return users;
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getCurrentUser() {
    log.info("Received request to get current user");
    UserResponseDto currentUser = userService.getCurrentUser();
    log.debug("Returning current user: {}", currentUser);
    return ResponseEntity.ok(currentUser);
  }
}
