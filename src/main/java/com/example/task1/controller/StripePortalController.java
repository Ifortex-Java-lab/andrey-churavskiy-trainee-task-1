package com.example.task1.controller;

import com.example.task1.dto.StripePortalRequest;
import com.example.task1.dto.StripePortalResponse;
import com.example.task1.service.StripePortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stripe/portal")
@RequiredArgsConstructor
public class StripePortalController {

    private final StripePortalService stripePortalService;
    @Value("${stripe.return-url}")
    private String returnUrl;

    @PostMapping
    public StripePortalResponse createPortalSession(@RequestBody StripePortalRequest request) throws Exception {
        return stripePortalService.createPortalSession(request, returnUrl);
    }
}