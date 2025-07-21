package com.example.task1.service.impl;

import com.example.task1.dto.SubscriptionDto;
import com.example.task1.dto.stripe.webhook.StripeSubscriptionDto;
import com.example.task1.dto.stripe.webhook.StripeSubscriptionItemDto;
import com.example.task1.dto.stripe.webhook.StripeWebhookEventDto;
import com.example.task1.entity.Subscription;
import com.example.task1.entity.User;
import com.example.task1.enums.SubscriptionStatus;
import com.example.task1.mapper.SubscriptionMapper;
import com.example.task1.repository.SubscriptionRepository;
import com.example.task1.repository.UserRepository;
import com.example.task1.service.StripeWebhookService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeWebhookServiceImpl implements StripeWebhookService {
  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;
  private final SubscriptionMapper subscriptionMapper;

  @Override
  public void handleEvent(StripeWebhookEventDto eventDto) {
    if (!isEventValid(eventDto)) {
      log.warn("Received incomplete Stripe webhook event: {}", eventDto);
      return;
    }

    StripeSubscriptionDto stripeSub = eventDto.getData().getObject();
    String stripeSubscriptionId = stripeSub.getId();
    SubscriptionStatus status = parseStatus(stripeSub.getStatus(), stripeSubscriptionId);

    SubscriptionDetails details = extractSubscriptionDetails(stripeSub);

    Optional<Subscription> optionalSubscription = subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId);

    log.info("Processing Stripe webhook event: {}, subscriptionId: {}", eventDto.getType(), stripeSubscriptionId);

    switch (eventDto.getType()) {
      case "customer.subscription.created":
        handleSubscriptionCreated(stripeSubscriptionId, status, details, stripeSub, optionalSubscription);
        break;
      case "customer.subscription.updated":
        handleSubscriptionUpdated(stripeSubscriptionId, status, details, optionalSubscription);
        break;
      case "customer.subscription.deleted":
        handleSubscriptionDeleted(stripeSubscriptionId, optionalSubscription);
        break;
      default:
        log.warn("Unhandled Stripe event type: {}", eventDto.getType());
        break;
    }
  }

  private boolean isEventValid(StripeWebhookEventDto eventDto) {
    return eventDto.getType() != null &&
            eventDto.getData() != null &&
            eventDto.getData().getObject() != null;
  }

  private SubscriptionStatus parseStatus(String statusString, String stripeSubscriptionId) {
    if (statusString == null) return null;
    try {
      return SubscriptionStatus.valueOf(statusString.toUpperCase());
    } catch (IllegalArgumentException ex) {
      log.warn("Unknown subscription status '{}' for subscriptionId: {}", statusString, stripeSubscriptionId);
      return null;
    }
  }

  private SubscriptionDetails extractSubscriptionDetails(StripeSubscriptionDto stripeSub) {
    SubscriptionDetails details = new SubscriptionDetails();
    if (stripeSub.getItems() != null &&
            stripeSub.getItems().getData() != null &&
            !stripeSub.getItems().getData().isEmpty()) {
      StripeSubscriptionItemDto item = stripeSub.getItems().getData().get(0);
      if (item.getPrice() != null) {
        details.priceId = item.getPrice().getId();
        details.productId = item.getPrice().getProduct();
      }
      details.currentPeriodStart = item.getCurrentPeriodStart();
      details.currentPeriodEnd = item.getCurrentPeriodEnd();
    }
    return details;
  }

  private void handleSubscriptionCreated(
          String stripeSubscriptionId,
          SubscriptionStatus status,
          SubscriptionDetails details,
          StripeSubscriptionDto stripeSub,
          Optional<Subscription> optionalSubscription) {

    if (optionalSubscription.isEmpty()) {
      log.info("Creating new subscription for Stripe subscriptionId: {}", stripeSubscriptionId);
      SubscriptionDto dto = new SubscriptionDto();
      dto.setStripeSubscriptionId(stripeSubscriptionId);
      dto.setStatus(status);
      dto.setPriceId(details.priceId);
      dto.setProductId(details.productId);

      if (details.currentPeriodStart != null) {
        dto.setCurrentPeriodStart(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(details.currentPeriodStart), ZoneOffset.UTC));
      }
      if (details.currentPeriodEnd != null) {
        dto.setCurrentPeriodEnd(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(details.currentPeriodEnd), ZoneOffset.UTC));
      }

      String customerId = stripeSub.getCustomer();
      User user = userRepository.findByCustomerId(customerId).orElse(null);
      if (user != null) {
        dto.setUserId(user.getId());
        Subscription subscription = subscriptionMapper.toEntity(dto);
        subscription.setUser(user);
        subscriptionRepository.save(subscription);
        log.info("Subscription saved for userId: {}, productId: {}", user.getId(), details.productId);
      } else {
        log.warn("User not found for customerId: {}", customerId);
      }
    } else {
      log.info("Subscription with Stripe subscriptionId: {} already exists", stripeSubscriptionId);
    }
  }

  private void handleSubscriptionUpdated(
          String stripeSubscriptionId,
          SubscriptionStatus status,
          SubscriptionDetails details,
          Optional<Subscription> optionalSubscription) {

    if (optionalSubscription.isPresent()) {
      log.info("Updating subscription for Stripe subscriptionId: {}", stripeSubscriptionId);
      Subscription subscription = optionalSubscription.get();
      subscription.setStatus(status);
      subscription.setPriceId(details.priceId);
      subscription.setProductId(details.productId);

      if (details.currentPeriodStart != null) {
        subscription.setCurrentPeriodStart(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(details.currentPeriodStart), ZoneOffset.UTC));
      }
      if (details.currentPeriodEnd != null) {
        subscription.setCurrentPeriodEnd(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(details.currentPeriodEnd), ZoneOffset.UTC));
      }
      subscriptionRepository.save(subscription);
      log.info("Subscription updated for subscriptionId: {}", stripeSubscriptionId);
    } else {
      log.warn("Subscription not found for Stripe subscriptionId: {}", stripeSubscriptionId);
    }
  }

  private void handleSubscriptionDeleted(
          String stripeSubscriptionId,
          Optional<Subscription> optionalSubscription) {

    if (optionalSubscription.isPresent()) {
      log.info("Canceling subscription for Stripe subscriptionId: {}", stripeSubscriptionId);
      Subscription subscription = optionalSubscription.get();
      subscription.setStatus(SubscriptionStatus.CANCELED);
      subscriptionRepository.save(subscription);
      log.info("Subscription canceled for subscriptionId: {}", stripeSubscriptionId);
    } else {
      log.warn("Subscription not found for Stripe subscriptionId: {} for deletion", stripeSubscriptionId);
    }
  }

  private static class SubscriptionDetails {
    String priceId;
    String productId;
    Long currentPeriodStart;
    Long currentPeriodEnd;
  }
}
