package com.example.task1.service;

import com.example.task1.dto.StripePortalRequest;
import com.example.task1.dto.StripePortalResponse;
import com.example.task1.entity.User;
import com.example.task1.repository.UserRepository;
import com.stripe.model.billingportal.Session;
import com.stripe.param.billingportal.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePortalService {

    private final UserRepository userRepository;

    public StripePortalResponse createPortalSession(StripePortalRequest request, String returnUrl) throws Exception {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        if (user.getCustomerId() == null || user.getCustomerId().isEmpty()) {
            throw new IllegalStateException("User does not have associated Stripe customerId");
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setCustomer(user.getCustomerId())
                .setReturnUrl(returnUrl)
                .build();

        Session session = Session.create(params);

        return new StripePortalResponse(session.getUrl());
    }
}
