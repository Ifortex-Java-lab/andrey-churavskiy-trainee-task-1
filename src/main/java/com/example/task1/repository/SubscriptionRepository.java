package com.example.task1.repository;

import com.example.task1.entity.Subscription;
import com.example.task1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
