package com.example.task1.service;

import com.example.task1.dto.SubscriptionResponseDto;
import com.example.task1.entity.User;
import java.util.List;

public interface SubscriptionService {
  List<SubscriptionResponseDto> getUserSubscriptions();

  void syncSubscriptionsFromStripe(User user);
}
