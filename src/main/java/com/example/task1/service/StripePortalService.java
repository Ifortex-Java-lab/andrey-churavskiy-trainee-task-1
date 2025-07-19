package com.example.task1.service;

import com.example.task1.dto.stripe.StripePortalRequest;
import com.example.task1.dto.stripe.StripePortalResponse;

public interface StripePortalService {
  StripePortalResponse createPortalSession(StripePortalRequest request);
}
