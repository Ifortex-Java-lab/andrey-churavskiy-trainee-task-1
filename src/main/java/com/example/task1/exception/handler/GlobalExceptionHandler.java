package com.example.task1.exception.handler;

import com.example.task1.dto.ErrorResponseDto;
import com.example.task1.exception.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex) {
    ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(ActiveSubscriptionExistsException.class)
  public ResponseEntity<ErrorResponseDto> handleActiveSubscriptionExistsException(
      ActiveSubscriptionExistsException ex) {
    ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(StripeApiException.class)
  public ResponseEntity<ErrorResponseDto> handleStripeApiException(StripeApiException ex) {
    ErrorResponseDto errorResponse =
        new ErrorResponseDto("Stripe error: " + ex.getMessage(), LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDto> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex) {
    ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidCredentials(
      InvalidCredentialsException ex, HttpServletRequest request) {
    ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleNoHandlerFound(
      NoHandlerFoundException ex, HttpServletRequest request) {
    ErrorResponseDto errorResponse =
        new ErrorResponseDto(
            "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL(),
            LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponseDto> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
    String message = "HTTP method " + ex.getMethod() + " is not supported for this endpoint";
    ErrorResponseDto errorResponse = new ErrorResponseDto(message, LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    String message = "Malformed JSON request or invalid data format";
    ErrorResponseDto errorResponse = new ErrorResponseDto(message, LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDto> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    String message = "Access denied: you do not have permission to access this resource";
    ErrorResponseDto errorResponse = new ErrorResponseDto(message, LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<String> errorDetails =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
            .collect(Collectors.toList());

    String message = "Validation failed for one or more fields: " + String.join("; ", errorDetails);
    ErrorResponseDto errorResponse = new ErrorResponseDto(message, LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    String message = "Validation error: " + ex.getMessage();
    ErrorResponseDto errorResponse = new ErrorResponseDto(message, LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
