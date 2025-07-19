package com.example.task1.dto.stripe.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StripeSubscriptionDto {
  private String id;
  private String object;
  private String status;
  private String customer;
  private StripeSubscriptionItemsDto items;
}
