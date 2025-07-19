package com.example.task1.service;

import com.example.task1.dto.stripe.StripeCheckoutRequest;
import com.example.task1.dto.stripe.StripeCheckoutResponse;

public interface StripePaymentService {
    StripeCheckoutResponse createCheckoutSession(StripeCheckoutRequest request);
}
