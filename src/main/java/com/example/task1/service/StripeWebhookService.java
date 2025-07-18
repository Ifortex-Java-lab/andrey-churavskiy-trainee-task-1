package com.example.task1.service;

import com.example.task1.dto.SubscriptionDto;
import com.example.task1.dto.webhook.StripeSubscriptionDto;
import com.example.task1.dto.webhook.StripeSubscriptionItemDto;
import com.example.task1.dto.webhook.StripeWebhookEventDto;
import com.example.task1.entity.Subscription;
import com.example.task1.entity.User;
import com.example.task1.enums.SubscriptionStatus;
import com.example.task1.mapper.SubscriptionMapper;
import com.example.task1.repository.SubscriptionRepository;
import com.example.task1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    public void handleEvent(StripeWebhookEventDto eventDto) {
        if (eventDto.getType() == null || eventDto.getData() == null || eventDto.getData().getObject() == null) {
            return;
        }

        StripeSubscriptionDto stripeSub = eventDto.getData().getObject();

        String stripeSubscriptionId = stripeSub.getId();
        String statusString = stripeSub.getStatus();
        SubscriptionStatus status = null;
        if (statusString != null) {
            status = SubscriptionStatus.valueOf(statusString.toUpperCase());
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

        switch (eventDto.getType()) {
            case "customer.subscription.created":
                if (optionalSubscription.isEmpty()) {
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
                    }
                }
                break;

            case "customer.subscription.updated":
                if (optionalSubscription.isPresent()) {
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
                }
                break;

            case "customer.subscription.deleted":
                if (optionalSubscription.isPresent()) {
                    Subscription subscription = optionalSubscription.get();
                    subscription.setStatus(SubscriptionStatus.CANCELED);
                    subscriptionRepository.save(subscription);
                }
                break;
        }
    }
}