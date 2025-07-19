package com.example.task1.controller;

import com.example.task1.dto.UserRequestDto;
import com.example.task1.dto.UserResponseDto;
import com.example.task1.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/create")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto createUser(@RequestBody UserRequestDto requestDto) {
    log.info("Received request to create user with email: {}", requestDto.getEmail());
    UserResponseDto response = userService.createUser(requestDto);
    log.debug("User created: {}", response);
    return response;
  }

  @GetMapping("/all")
  public List<UserResponseDto> getAllUsers() {
    log.info("Received request to get all users");
    List<UserResponseDto> users = userService.getAllUsers();
    log.debug("Returning {} users", users.size());
    return users;
  }
}
