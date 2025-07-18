package com.example.task1.service;

import com.example.task1.dto.StripeCheckoutRequest;
import com.example.task1.dto.StripeCheckoutResponse;
import com.example.task1.entity.User;
import com.example.task1.enums.SubscriptionStatus;
import com.example.task1.repository.SubscriptionRepository;
import com.example.task1.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class StripePaymentService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;
    @Value("${stripe.success-url}")
    private String successUrl;
    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public StripeCheckoutResponse createCheckoutSession(StripeCheckoutRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        try {
            Price price = Price.retrieve(request.getPriceId());
            String productId = price.getProduct();

            boolean hasActiveSubscription = subscriptionRepository.existsByUserIdAndProductIdAndStatusNot(
                    user.getId(),
                    productId,
                    SubscriptionStatus.CANCELED
            );

            if (hasActiveSubscription) {
                throw new IllegalStateException("User already has an active subscription for this product.");
            }

            SessionCreateParams params = SessionCreateParams.builder()
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setCustomer(user.getCustomerId())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(request.getPriceId())
                                    .setQuantity(1L)
                                    .build()
                    )
                    .build();
            Session session = Session.create(params);

            return new StripeCheckoutResponse(session.getUrl());
        } catch (Exception e) {
            throw new RuntimeException("Stripe checkout session creation failed", e);
        }
    }
}