package com.example.task1.dto.stripe.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StripeSubscriptionItemDto {
  private String id;
  private String object;
  private StripePriceDto price;
  private Long current_period_start;
  private Long current_period_end;
}
