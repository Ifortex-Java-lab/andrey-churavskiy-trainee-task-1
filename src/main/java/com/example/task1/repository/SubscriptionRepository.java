package com.example.task1.repository;

import com.example.task1.entity.Subscription;
import com.example.task1.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
    boolean existsByUserIdAndProductIdAndStatusNot(Long userId, String productId, SubscriptionStatus status);
    List<Subscription> findByUserId(Long userId);
}
