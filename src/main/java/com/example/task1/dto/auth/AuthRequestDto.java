package com.example.task1.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDto {
  @NotBlank(message = "Email must not be blank")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Password must not be blank")
  private String password;
}
