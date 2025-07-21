package com.example.task1.service.impl;

import com.example.task1.dto.stripe.StripePortalResponse;
import com.example.task1.entity.User;
import com.example.task1.exception.StripeApiException;
import com.example.task1.service.CurrentUserService;
import com.example.task1.service.StripePortalService;
import com.stripe.model.billingportal.Session;
import com.stripe.param.billingportal.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePortalServiceImpl implements StripePortalService {

  private final CurrentUserService currentUserService;

  @Value("${stripe.return-url}")
  private String returnUrl;

  public StripePortalResponse createPortalSession() {
    log.info("Received request to create Stripe portal session");
    User user = currentUserService.getCurrentUserEntity();

    try {
      SessionCreateParams params =
          SessionCreateParams.builder()
              .setCustomer(user.getCustomerId())
              .setReturnUrl(returnUrl)
              .build();

      log.debug(
          "Creating Stripe portal session for userId: {}, customerId: {}",
          user.getId(),
          user.getCustomerId());
      Session session = Session.create(params);

      log.info(
          "Stripe portal session created for userId: {}. URL: {}", user.getId(), session.getUrl());
      return new StripePortalResponse(session.getUrl());
    } catch (Exception e) {
      log.error("Stripe portal session creation failed for userId: {}", user.getId(), e);
      throw new StripeApiException("Stripe portal session creation failed", e);
    }
  }
}
