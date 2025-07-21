package com.example.task1.exception;

public class StripeApiException extends RuntimeException {
  public StripeApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
