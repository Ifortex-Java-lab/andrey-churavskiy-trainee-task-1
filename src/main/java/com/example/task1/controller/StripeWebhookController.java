package com.example.task1.controller;

import com.example.task1.dto.webhook.StripeWebhookEventDto;
import com.example.task1.service.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(@RequestBody StripeWebhookEventDto eventDto) {
        stripeWebhookService.handleEvent(eventDto);
        return ResponseEntity.ok("Webhook received");
    }
}
