package com.example.task1.controller;

import com.example.task1.dto.SubscriptionResponseDto;
import com.example.task1.service.SubscriptionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @GetMapping
  public ResponseEntity<List<SubscriptionResponseDto>> getUserSubscriptions() {
    log.info("Received request to get subscriptions");
    List<SubscriptionResponseDto> subscriptions = subscriptionService.getUserSubscriptions();
    log.debug("Returning {} subscriptions", subscriptions.size());
    return ResponseEntity.ok(subscriptions);
  }
}
