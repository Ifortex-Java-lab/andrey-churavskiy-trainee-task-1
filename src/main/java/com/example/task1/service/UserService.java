package com.example.task1.service;

import com.example.task1.dto.user.UserRequestDto;
import com.example.task1.dto.user.UserResponseDto;
import com.example.task1.entity.User;
import java.util.List;

public interface UserService {
  User createUser(UserRequestDto dto);

  List<UserResponseDto> getAllUsers();

  UserResponseDto getCurrentUser();
}
