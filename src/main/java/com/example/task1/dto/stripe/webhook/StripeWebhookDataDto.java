package com.example.task1.dto.stripe.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StripeWebhookDataDto {
  private StripeSubscriptionDto object;
}
