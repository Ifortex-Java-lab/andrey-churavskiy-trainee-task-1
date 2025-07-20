package com.example.task1.entity;

import com.example.task1.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "subscriptions")
public class Subscription {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String priceId;

  @Column(nullable = false)
  private String productId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SubscriptionStatus status;

  private LocalDateTime currentPeriodEnd;

  private LocalDateTime currentPeriodStart;

  @Column(nullable = false, unique = true)
  private String stripeSubscriptionId;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
