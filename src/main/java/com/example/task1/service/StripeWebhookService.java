package com.example.task1.service;

import com.example.task1.dto.stripe.webhook.StripeWebhookEventDto;

public interface StripeWebhookService {
  void handleEvent(StripeWebhookEventDto eventDto);
}
