package com.example.task1.service.impl;

import com.example.task1.dto.stripe.StripeCheckoutRequest;
import com.example.task1.dto.stripe.StripeCheckoutResponse;
import com.example.task1.entity.User;
import com.example.task1.enums.SubscriptionStatus;
import com.example.task1.exception.ActiveSubscriptionExistsException;
import com.example.task1.exception.StripeApiException;
import com.example.task1.exception.UserNotFoundException;
import com.example.task1.repository.SubscriptionRepository;
import com.example.task1.repository.UserRepository;
import com.example.task1.service.CurrentUserService;
import com.example.task1.service.StripePaymentService;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements StripePaymentService {

  private final SubscriptionRepository subscriptionRepository;
  private final CurrentUserService currentUserService;

  @Value("${stripe.success-url}")
  private String successUrl;

  @Value("${stripe.cancel-url}")
  private String cancelUrl;

  public StripeCheckoutResponse createCheckoutSession(StripeCheckoutRequest request) {
    log.info("Attempting to create Stripe payment session for priceId: {}", request.getPriceId());
    User user = currentUserService.getCurrentUserEntity();

    try {
      Price price = Price.retrieve(request.getPriceId());
      String productId = price.getProduct();
      log.debug(
          "Retrieved priceId: {}, productId: {} for userId: {}",
          request.getPriceId(),
          productId,
          user.getId());

      boolean hasActiveSubscription =
          subscriptionRepository.existsByUserIdAndProductIdAndStatusNot(
              user.getId(), productId, SubscriptionStatus.CANCELED);

      if (hasActiveSubscription) {
        log.warn(
            "User {} already has an active subscription for productId: {}",
            user.getId(),
            productId);
        throw new ActiveSubscriptionExistsException(
            "User already has an active subscription for this product.");
      }

      SessionCreateParams params =
          SessionCreateParams.builder()
              .setSuccessUrl(successUrl)
              .setCancelUrl(cancelUrl)
              .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
              .setCustomer(user.getCustomerId())
              .addLineItem(
                  SessionCreateParams.LineItem.builder()
                      .setPrice(request.getPriceId())
                      .setQuantity(1L)
                      .build())
              .build();
      log.debug(
          "Stripe SessionCreateParams built for userId: {}, productId: {}",
          user.getId(),
          productId);

      Session session = Session.create(params);
      log.info(
          "Stripe checkout session created for userId: {}. URL: {}",
          user.getId(),
          session.getUrl());

      return new StripeCheckoutResponse(session.getUrl());
    } catch (ActiveSubscriptionExistsException | UserNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Stripe payment session creation failed for userId: {}", user.getId(), e);
      throw new StripeApiException("Stripe payment session creation failed", e);
    }
  }
}
