package com.example.task1.service.impl;

import com.example.task1.dto.user.UserCreateDto;
import com.example.task1.dto.user.UserRequestDto;
import com.example.task1.dto.user.UserResponseDto;
import com.example.task1.entity.User;
import com.example.task1.exception.EmailAlreadyExistsException;
import com.example.task1.exception.StripeApiException;
import com.example.task1.mapper.UserMapper;
import com.example.task1.repository.UserRepository;
import com.example.task1.service.CurrentUserService;
import com.example.task1.service.SubscriptionService;
import com.example.task1.service.UserService;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerCreateParams;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SubscriptionService subscriptionService;
  private final CurrentUserService currentUserService;

  @Transactional
  public User createUser(UserRequestDto dto) {
    log.info("Attempting to create user with email: {}", dto.getEmail());

    if (userRepository.existsByEmail(dto.getEmail())) {
      log.warn("Email already exists: {}", dto.getEmail());
      throw new EmailAlreadyExistsException("Email already exists: " + dto.getEmail());
    }

    String customerId;
    try {
      customerId = getOrCreateStripeCustomerId(dto.getEmail());
      log.debug("Stripe customerId obtained/created for email {}: {}", dto.getEmail(), customerId);
    } catch (Exception e) {
      log.error("Stripe customer creation or fetching failed for email: {}", dto.getEmail(), e);
      throw new StripeApiException("Stripe customer creation or fetching failed", e);
    }

    UserCreateDto createDto = new UserCreateDto(dto.getEmail(), customerId, dto.getPassword());
    User savedUser = userRepository.save(userMapper.toEntity(createDto));
    log.info("User created with id: {}, email: {}", savedUser.getId(), savedUser.getEmail());

    subscriptionService.syncSubscriptionsFromStripe(savedUser);
    log.info("Stripe subscriptions synced for user id: {}", savedUser.getId());

    return savedUser;
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

  @Override
  public UserResponseDto getCurrentUser() {
    log.info("Getting current user DTO");
    User currentUser = currentUserService.getCurrentUserEntity();
    log.debug("Current user entity: {}", currentUser);
    UserResponseDto dto = userMapper.toDto(currentUser);
    log.debug("Mapped current user DTO: {}", dto);
    return dto;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    log.info("Loading user by username (email): {}", email);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> {
                  log.warn("User not found for email: {}", email);
                  return new UsernameNotFoundException("User not found");
                });
    log.debug("Loaded user entity: {}", user);
    UserDetails userDetails =
        new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    log.debug("Returning UserDetails: {}", userDetails);
    return userDetails;
  }
}
