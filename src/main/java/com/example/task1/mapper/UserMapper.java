package com.example.task1.mapper;

import com.example.task1.dto.auth.RegisterRequestDto;
import com.example.task1.dto.user.UserCreateDto;
import com.example.task1.dto.user.UserResponseDto;
import com.example.task1.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  UserResponseDto toDto(User user);

  User toEntity(UserResponseDto dto);

  @Mapping(target = "role", expression = "java(com.example.task1.enums.Role.USER)")
  User toEntity(UserCreateDto dto);

  @Mapping(target = "role", expression = "java(com.example.task1.enums.Role.USER)")
  User fromRegisterDto(RegisterRequestDto dto);
}
