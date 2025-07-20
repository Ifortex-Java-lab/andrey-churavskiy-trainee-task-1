package com.example.task1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/stripe")
public class StripeResultController {
  @GetMapping("/success")
  public String paymentSuccess() {
    return "success";
  }

  @GetMapping("/cancel")
  public String paymentCancel() {
    return "cancel";
  }
}
