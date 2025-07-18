package com.example.task1.controller;

import com.example.task1.dto.StripeCheckoutRequest;
import com.example.task1.dto.StripeCheckoutResponse;
import com.example.task1.service.StripePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
public class StripePaymentController {

    private final StripePaymentService stripePaymentService;

    @PostMapping("/checkout")
    public StripeCheckoutResponse createCheckoutSession(@RequestBody StripeCheckoutRequest request) throws Exception {
        return stripePaymentService.createCheckoutSession(request);
    }
}
