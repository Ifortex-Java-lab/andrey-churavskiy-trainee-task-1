package com.example.task1.service;

import com.example.task1.dto.SubscriptionResponseDto;

import java.util.List;

public interface SubscriptionService {
    List<SubscriptionResponseDto> getUserSubscriptions(Long userId);
}
