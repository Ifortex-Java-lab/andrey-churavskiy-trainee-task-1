package com.example.task1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponseDto {
  private String priceId;
  private String productName;
  private Long amount;
  private String currency;
  private String interval;
}
