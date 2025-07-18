package com.example.task1.service;

import com.example.task1.dto.SubscriptionResponseDto;
import com.example.task1.mapper.SubscriptionMapper;
import com.example.task1.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;

    public List<SubscriptionResponseDto> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .stream()
                .map(subscriptionMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}