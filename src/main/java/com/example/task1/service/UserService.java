package com.example.task1.service;

import com.example.task1.dto.UserRequestDto;
import com.example.task1.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto requestDto);
    List<UserResponseDto> getAllUsers();
}
