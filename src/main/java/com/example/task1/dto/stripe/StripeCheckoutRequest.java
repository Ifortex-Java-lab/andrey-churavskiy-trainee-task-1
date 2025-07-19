package com.example.task1.dto.stripe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StripeCheckoutRequest {
  private Long userId;
  private String priceId;
}
