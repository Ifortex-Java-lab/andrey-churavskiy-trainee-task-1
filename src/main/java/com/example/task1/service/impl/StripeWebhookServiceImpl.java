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
import jakarta.transaction.Transactional;
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

    @Transactional
    public void handleEvent(StripeWebhookEventDto eventDto) {
        if (eventDto.getType() == null || eventDto.getData() == null || eventDto.getData().getObject() == null) {
            log.warn("Received incomplete Stripe webhook event: {}", eventDto);
            return;
        }

        StripeSubscriptionDto stripeSub = eventDto.getData().getObject();

        String stripeSubscriptionId = stripeSub.getId();
        String statusString = stripeSub.getStatus();
        SubscriptionStatus status = null;
        if (statusString != null) {
            try {
                status = SubscriptionStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown subscription status '{}' for subscriptionId: {}", statusString, stripeSubscriptionId);
            }
        }

        String priceId = null;
        String productId = null;
        Long currentPeriodStart = null;
        Long currentPeriodEnd = null;

        if (stripeSub.getItems() != null &&
                stripeSub.getItems().getData() != null &&
                !stripeSub.getItems().getData().isEmpty()) {

            StripeSubscriptionItemDto item = stripeSub.getItems().getData().get(0);
            if (item.getPrice() != null) {
                priceId = item.getPrice().getId();
                productId = item.getPrice().getProduct();
            }
            currentPeriodStart = item.getCurrent_period_start();
            currentPeriodEnd = item.getCurrent_period_end();
        }

        Optional<Subscription> optionalSubscription = subscriptionRepository
                .findByStripeSubscriptionId(stripeSubscriptionId);

        log.info("Processing Stripe webhook event: {}, subscriptionId: {}", eventDto.getType(), stripeSubscriptionId);

        switch (eventDto.getType()) {
            case "customer.subscription.created":
                if (optionalSubscription.isEmpty()) {
                    log.info("Creating new subscription for Stripe subscriptionId: {}", stripeSubscriptionId);
                    SubscriptionDto dto = new SubscriptionDto();
                    dto.setStripeSubscriptionId(stripeSubscriptionId);
                    dto.setStatus(status);
                    dto.setPriceId(priceId);
                    dto.setProductId(productId);
                    if (currentPeriodStart != null) {
                        dto.setCurrentPeriodStart(
                                LocalDateTime.ofInstant(Instant.ofEpochSecond(currentPeriodStart), ZoneOffset.UTC));
                    }
                    if (currentPeriodEnd != null) {
                        dto.setCurrentPeriodEnd(
                                LocalDateTime.ofInstant(Instant.ofEpochSecond(currentPeriodEnd), ZoneOffset.UTC));
                    }

                    String customerId = stripeSub.getCustomer();
                    User user = userRepository.findByCustomerId(customerId).orElse(null);
                    if (user != null) {
                        dto.setUserId(user.getId());
                        Subscription subscription = subscriptionMapper.toEntity(dto);
                        subscription.setUser(user);
                        subscriptionRepository.save(subscription);
                        log.info("Subscription saved for userId: {}, productId: {}", user.getId(), productId);
                    } else {
                        log.warn("User not found for customerId: {}", customerId);
                    }
                } else {
                    log.info("Subscription with Stripe subscriptionId: {} already exists", stripeSubscriptionId);
                }
                break;

            case "customer.subscription.updated":
                if (optionalSubscription.isPresent()) {
                    log.info("Updating subscription for Stripe subscriptionId: {}", stripeSubscriptionId);
                    Subscription subscription = optionalSubscription.get();
                    subscription.setStatus(status);
                    subscription.setPriceId(priceId);
                    subscription.setProductId(productId);
                    if (currentPeriodStart != null) {
                        subscription.setCurrentPeriodStart(
                                LocalDateTime.ofInstant(Instant.ofEpochSecond(currentPeriodStart), ZoneOffset.UTC));
                    }
                    if (currentPeriodEnd != null) {
                        subscription.setCurrentPeriodEnd(
                                LocalDateTime.ofInstant(Instant.ofEpochSecond(currentPeriodEnd), ZoneOffset.UTC));
                    }
                    subscriptionRepository.save(subscription);
                    log.info("Subscription updated for subscriptionId: {}", stripeSubscriptionId);
                } else {
                    log.warn("Subscription not found for Stripe subscriptionId: {}", stripeSubscriptionId);
                }
                break;

            case "customer.subscription.deleted":
                if (optionalSubscription.isPresent()) {
                    log.info("Canceling subscription for Stripe subscriptionId: {}", stripeSubscriptionId);
                    Subscription subscription = optionalSubscription.get();
                    subscription.setStatus(SubscriptionStatus.CANCELED);
                    subscriptionRepository.save(subscription);
                    log.info("Subscription canceled for subscriptionId: {}", stripeSubscriptionId);
                } else {
                    log.warn("Subscription not found for Stripe subscriptionId: {} for deletion", stripeSubscriptionId);
                }
                break;

            default:
                log.warn("Unhandled Stripe event type: {}", eventDto.getType());
                break;
        }
    }
}