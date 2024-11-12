package com.mkhabibullin.audit.domain.exception;

/**
 * Exception thrown when audit validation fails.
 * Used for handling validation errors in the audit module.
 */
public class AuditValidationException extends RuntimeException {
  
  public AuditValidationException(String message) {
    super(message);
  }
  
  public AuditValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}