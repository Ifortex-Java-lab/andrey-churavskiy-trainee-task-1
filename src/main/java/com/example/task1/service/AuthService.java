package com.example.task1.service;

import com.example.task1.dto.auth.AuthRequestDto;
import com.example.task1.dto.auth.AuthResponseDto;
import com.example.task1.dto.auth.RegisterRequestDto;

public interface AuthService {
  AuthResponseDto register(RegisterRequestDto request);

  AuthResponseDto login(AuthRequestDto request);
}
