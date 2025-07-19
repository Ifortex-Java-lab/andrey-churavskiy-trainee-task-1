package com.example.task1.service.impl;

import com.example.task1.dto.UserRequestDto;
import com.example.task1.dto.UserResponseDto;
import com.example.task1.entity.User;
import com.example.task1.exception.EmailAlreadyExistsException;
import com.example.task1.exception.StripeApiException;
import com.example.task1.mapper.UserMapper;
import com.example.task1.repository.UserRepository;
import com.example.task1.service.UserService;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Value("${stripe.api.key}")
  private String stripeApiKey;

  @PostConstruct
  public void init() {
    Stripe.apiKey = stripeApiKey;
    log.info("Stripe API key initialized in UserService");
  }

  @Transactional
  public UserResponseDto createUser(UserRequestDto requestDto) {
    log.info("Attempting to create user with email: {}", requestDto.getEmail());

    if (userRepository.existsByEmail(requestDto.getEmail())) {
      log.warn("Email already exists: {}", requestDto.getEmail());
      throw new EmailAlreadyExistsException("Email already exists: " + requestDto.getEmail());
    }

    String customerId;
    try {
      customerId = getOrCreateStripeCustomerId(requestDto.getEmail());
      log.debug(
          "Stripe customerId obtained/created for email {}: {}", requestDto.getEmail(), customerId);
    } catch (Exception e) {
      log.error(
          "Stripe customer creation or fetching failed for email: {}", requestDto.getEmail(), e);
      throw new StripeApiException("Stripe customer creation or fetching failed", e);
    }

    User user = new User();
    user.setEmail(requestDto.getEmail());
    user.setCustomerId(customerId);

    User savedUser = userRepository.save(user);
    log.info("User created with id: {}, email: {}", savedUser.getId(), savedUser.getEmail());

    return userMapper.toDto(savedUser);
  }

  private String getOrCreateStripeCustomerId(String email) throws Exception {
    Customer existingCustomer = findStripeCustomerByEmail(email);
    if (existingCustomer != null) {
      log.debug("Stripe customer already exists for email: {}", email);
      return existingCustomer.getId();
    } else {
      log.debug("Creating Stripe customer for email: {}", email);
      CustomerCreateParams customerParams = CustomerCreateParams.builder().setEmail(email).build();
      Customer customer = Customer.create(customerParams);
      log.info("Created Stripe customer with id: {} for email: {}", customer.getId(), email);
      return customer.getId();
    }
  }

  private Customer findStripeCustomerByEmail(String email) throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("email", email);
    params.put("limit", 1);

    CustomerCollection customers = Customer.list(params);
    if (customers.getData() != null && !customers.getData().isEmpty()) {
      log.debug("Found Stripe customer for email: {}", email);
      return customers.getData().get(0);
    }
    log.debug("No Stripe customer found for email: {}", email);
    return null;
  }

  public List<UserResponseDto> getAllUsers() {
    log.info("Fetching all users from database");
    List<User> users = userRepository.findAll();
    log.debug("Fetched {} users from database", users.size());
    return users.stream().map(userMapper::toDto).collect(java.util.stream.Collectors.toList());
  }
}
