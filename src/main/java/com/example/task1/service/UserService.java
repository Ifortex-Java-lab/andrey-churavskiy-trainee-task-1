package com.example.task1.service;

import com.example.task1.dto.UserRequestDto;
import com.example.task1.dto.UserResponseDto;
import com.example.task1.entity.User;
import com.example.task1.mapper.UserMapper;
import com.example.task1.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String customerId;
        try {
            customerId = getOrCreateStripeCustomerId(requestDto.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Stripe customer creation or fetching failed", e);
        }

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setCustomerId(customerId);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    private String getOrCreateStripeCustomerId(String email) throws Exception {
        Customer existingCustomer = findStripeCustomerByEmail(email);
        if (existingCustomer != null) {
            return existingCustomer.getId();
        } else {
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setEmail(email)
                    .build();
            Customer customer = Customer.create(customerParams);
            return customer.getId();
        }
    }

    private Customer findStripeCustomerByEmail(String email) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("limit", 1);

        CustomerCollection customers = Customer.list(params);
        if (customers.getData() != null && !customers.getData().isEmpty()) {
            return customers.getData().get(0);
        }
        return null;
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }
}
