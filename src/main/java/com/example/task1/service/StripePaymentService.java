package com.example.task1.service;

import com.example.task1.dto.StripeCheckoutRequest;
import com.example.task1.dto.StripeCheckoutResponse;
import com.example.task1.entity.User;
import com.example.task1.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
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

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public StripeCheckoutResponse createCheckoutSession(StripeCheckoutRequest request, String successUrl, String cancelUrl) throws Exception {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        if (user.getCustomerId() == null || user.getCustomerId().isEmpty()) {
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .build();
            Customer customer = Customer.create(customerParams);
            user.setCustomerId(customer.getId());
            userRepository.save(user);
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
    }
}