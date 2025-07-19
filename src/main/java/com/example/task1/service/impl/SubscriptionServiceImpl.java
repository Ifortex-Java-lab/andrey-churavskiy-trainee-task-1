package com.example.task1.service.impl;

import com.example.task1.dto.SubscriptionResponseDto;
import com.example.task1.mapper.SubscriptionMapper;
import com.example.task1.repository.SubscriptionRepository;
import com.example.task1.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;

    public List<SubscriptionResponseDto> getUserSubscriptions(Long userId) {
        log.info("Fetching subscriptions for userId: {}", userId);
        List<SubscriptionResponseDto> result = subscriptionRepository.findByUserId(userId)
                .stream()
                .map(subscriptionMapper::toResponseDto)
                .collect(Collectors.toList());
        log.debug("Fetched {} subscriptions for userId: {}", result.size(), userId);
        return result;
    }
}