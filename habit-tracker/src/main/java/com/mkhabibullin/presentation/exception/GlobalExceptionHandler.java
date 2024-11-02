package com.mkhabibullin.presentation.exception;

import com.mkhabibullin.domain.exception.AuthenticationException;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all @RequestMapping methods.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
  
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public ResponseEntity<ErrorDTO> handleAuthenticationException(
    AuthenticationException ex,
    WebRequest request) {
    log.error("Authentication error: {}", ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO(
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(errorDTO);
  }
  
  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ErrorDTO> handleValidationException(
    ValidationException ex,
    WebRequest request) {
    log.error("Validation error: {}", ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO(
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(errorDTO);
  }
  
  /**
   * Handles EntityNotFoundException. Created to encapsulate errors with more detail than ValidationException.
   */
  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ResponseEntity<ErrorDTO> handleEntityNotFoundException(
    EntityNotFoundException ex,
    WebRequest request) {
    log.error("Entity not found: {}", ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO(
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(errorDTO);
  }
  
  /**
   * Handle all other exceptions that don't have specific handlers
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ResponseEntity<ErrorDTO> handleGlobalException(
    Exception ex,
    WebRequest request) {
    log.error("Unexpected error:", ex);
    ErrorDTO errorDTO = new ErrorDTO(
      "Internal server error",
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(errorDTO);
  }
}