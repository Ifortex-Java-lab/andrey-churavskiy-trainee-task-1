package com.example.task1.exception;

public class ActiveSubscriptionExistsException extends RuntimeException {
  public ActiveSubscriptionExistsException(String message) {
    super(message);
  }
}
