package com.example.task1.controller;

import com.example.task1.dto.auth.AuthRequestDto;
import com.example.task1.dto.auth.AuthResponseDto;
import com.example.task1.dto.auth.RegisterRequestDto;
import com.example.task1.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto request) {
    return ResponseEntity.ok(authService.login(request));
  }
}
