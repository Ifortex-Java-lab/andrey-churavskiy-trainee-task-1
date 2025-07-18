package com.example.task1.controller;

import com.example.task1.dto.*;
import com.example.task1.dto.webhook.StripeWebhookEventDto;
import com.example.task1.service.PlanService;
import com.example.task1.service.StripePaymentService;
import com.example.task1.service.StripePortalService;
import com.example.task1.service.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final StripePaymentService stripePaymentService;
    private final StripePortalService stripePortalService;
    private final StripeWebhookService stripeWebhookService;
    private final PlanService planService;

    @GetMapping("/plans")
    public List<PlanResponseDto> getAllPlans() {
        return planService.getAllPlans();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody StripeWebhookEventDto eventDto) {
        stripeWebhookService.handleEvent(eventDto);
        return ResponseEntity.ok("Webhook received");
    }
    @PostMapping("/portal")
    public StripePortalResponse createPortalSession(@RequestBody StripePortalRequest request) {
        return stripePortalService.createPortalSession(request);
    }

    @PostMapping("/checkout")
    public StripeCheckoutResponse createCheckoutSession(@RequestBody StripeCheckoutRequest request){
        return stripePaymentService.createCheckoutSession(request);
    }

}
