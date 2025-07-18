package com.example.task1.dto;

import com.example.task1.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDto {
    private Long id;
    private Long userId;
    private String priceId;
    private String productId;
    private SubscriptionStatus status;
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;
    private String stripeSubscriptionId;
}