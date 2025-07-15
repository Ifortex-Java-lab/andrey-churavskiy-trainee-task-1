package com.example.task1.entity;

import com.example.task1.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String priceId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDateTime currentPeriodEnd;

    private LocalDateTime currentPeriodStart;

    private String stripeSubscriptionId;
}