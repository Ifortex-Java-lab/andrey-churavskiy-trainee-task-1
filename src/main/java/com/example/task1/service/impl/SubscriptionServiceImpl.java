package com.example.task1.service.impl;

import com.example.task1.dto.SubscriptionDto;
import com.example.task1.dto.SubscriptionResponseDto;
import com.example.task1.entity.User;
import com.example.task1.enums.SubscriptionStatus;
import com.example.task1.exception.StripeApiException;
import com.example.task1.mapper.SubscriptionMapper;
import com.example.task1.repository.SubscriptionRepository;
import com.example.task1.service.CurrentUserService;
import com.example.task1.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionMapper subscriptionMapper;
  private final CurrentUserService currentUserService;

  public List<SubscriptionResponseDto> getUserSubscriptions() {
    log.info("Fetching user subscriptions");
    Long userId = currentUserService.getCurrentUserEntity().getId();
    List<SubscriptionResponseDto> result =
        subscriptionRepository.findByUserId(userId).stream()
            .map(subscriptionMapper::toResponseDto)
            .collect(Collectors.toList());
    log.debug("Fetched {} subscriptions for userId: {}", result.size(), userId);
    return result;
  }

  public void syncSubscriptionsFromStripe(User user) {
    String stripeCustomerId = user.getCustomerId();

    Map<String, Object> params = new HashMap<>();
    params.put("customer", stripeCustomerId);
    params.put("limit", 100);

    try {
      SubscriptionCollection subscriptions = Subscription.list(params);

      log.info(
          "Fetched Stripe subscriptions for userId={} (customerId={}): {}",
          user.getId(),
          stripeCustomerId,
          subscriptions.getData().size());

      for (Subscription stripeSub : subscriptions.getData()) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setUserId(user.getId());

        if (stripeSub.getItems() != null && !stripeSub.getItems().getData().isEmpty()) {
          com.stripe.model.SubscriptionItem item = stripeSub.getItems().getData().get(0);
          dto.setPriceId(item.getPrice().getId());
          dto.setProductId(item.getPrice().getProduct());
          dto.setCurrentPeriodEnd(
              Instant.ofEpochSecond(item.getCurrentPeriodEnd())
                  .atZone(ZoneId.systemDefault())
                  .toLocalDateTime());
          dto.setCurrentPeriodStart(
              Instant.ofEpochSecond(item.getCurrentPeriodStart())
                  .atZone(ZoneId.systemDefault())
                  .toLocalDateTime());
        }

        dto.setStatus(SubscriptionStatus.valueOf(stripeSub.getStatus().toUpperCase()));
        dto.setStripeSubscriptionId(stripeSub.getId());

        subscriptionRepository
            .findByStripeSubscriptionIdAndUserId(dto.getStripeSubscriptionId(), dto.getUserId())
            .ifPresent(
                sub -> {
                  dto.setId(sub.getId());
                });

        subscriptionRepository.save(subscriptionMapper.toEntity(dto));

        log.info(
            "Saved subscription: stripeSubscriptionId={}, userId={}",
            dto.getStripeSubscriptionId(),
            dto.getUserId());
      }
    } catch (StripeException e) {
      log.error(
          "Failed to synchronize subscriptions from Stripe for userId={} (customerId={})",
          user.getId(),
          stripeCustomerId,
          e);
      throw new StripeApiException("Failed to synchronize Stripe subscriptions", e);
    }
  }
}
