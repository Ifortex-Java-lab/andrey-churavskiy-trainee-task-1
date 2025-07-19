package com.example.task1.controller;

import com.example.task1.dto.*;
import com.example.task1.dto.stripe.StripeCheckoutRequest;
import com.example.task1.dto.stripe.StripeCheckoutResponse;
import com.example.task1.dto.stripe.StripePortalRequest;
import com.example.task1.dto.stripe.StripePortalResponse;
import com.example.task1.dto.stripe.webhook.StripeWebhookEventDto;
import com.example.task1.service.PlanService;
import com.example.task1.service.StripePaymentService;
import com.example.task1.service.StripePortalService;
import com.example.task1.service.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final StripePaymentService stripePaymentService;
    private final StripePortalService stripePortalService;
    private final StripeWebhookService stripeWebhookService;
    private final PlanService planService;

    @GetMapping("/plans/all")
    public List<PlanResponseDto> getAllPlans() {
        log.info("Received GET request for all Stripe plans");
        List<PlanResponseDto> plans = planService.getAllPlans();
        log.debug("Returning {} plans", plans.size());
        return plans;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody StripeWebhookEventDto eventDto) {
        log.info("Received Stripe webhook event: {}", eventDto.getType());
        stripeWebhookService.handleEvent(eventDto);
        log.debug("Processed webhook event: {}", eventDto.getType());
        return ResponseEntity.ok("Webhook received");
    }

    @PostMapping("/portal")
    public StripePortalResponse createPortalSession(@RequestBody StripePortalRequest request) {
        log.info("Received request to create Stripe portal session for userId: {}", request.getUserId());
        StripePortalResponse response = stripePortalService.createPortalSession(request);
        log.debug("Created Stripe portal session for userId: {}. URL: {}", request.getUserId(), response.getUrl());
        return response;
    }

    @PostMapping("/payment")
    public StripeCheckoutResponse createPaymentSession(@RequestBody StripeCheckoutRequest request){
        log.info("Received request to create Stripe payment session for userId: {}, priceId: {}",
                request.getUserId(), request.getPriceId());
        StripeCheckoutResponse response = stripePaymentService.createCheckoutSession(request);
        log.debug("Created Stripe payment session for userId: {}. URL: {}",
                request.getUserId(), response.getCheckoutUrl());
        return response;
    }

}
