package com.example.task1.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
  private Long id;
  private String email;
  // TODO: remove in production. Only for development/testing!
  private String customerId;
}
