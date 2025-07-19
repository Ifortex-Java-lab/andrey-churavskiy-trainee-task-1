package com.example.task1.exception.handler;

import com.example.task1.dto.ErrorResponseDto;
import com.example.task1.exception.ActiveSubscriptionExistsException;
import com.example.task1.exception.EmailAlreadyExistsException;
import com.example.task1.exception.StripeApiException;
import com.example.task1.exception.UserNotFoundException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ActiveSubscriptionExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleActiveSubscriptionExistsException(ActiveSubscriptionExistsException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(StripeApiException.class)
    public ResponseEntity<ErrorResponseDto> handleStripeApiException(StripeApiException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto("Stripe error: " + ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }


}