package com.example.task1.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
  @NotBlank(message = "Email must not be blank")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Password must not be blank")
  @Size(min = 4, message = "Password must be at least 6 characters")
  private String password;
}
