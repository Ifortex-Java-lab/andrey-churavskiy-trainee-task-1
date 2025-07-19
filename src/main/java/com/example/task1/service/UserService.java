package com.example.task1.service;

import com.example.task1.dto.user.UserRequestDto;
import com.example.task1.dto.user.UserResponseDto;
import java.util.List;

public interface UserService {
  UserResponseDto createUser(UserRequestDto requestDto);

  List<UserResponseDto> getAllUsers();
}
