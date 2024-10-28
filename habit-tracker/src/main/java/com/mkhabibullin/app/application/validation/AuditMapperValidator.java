package com.mkhabibullin.app.application.validation;

import com.mkhabibullin.app.application.mapper.AuditMapper;
import com.mkhabibullin.app.domain.exception.ValidationException;
import com.mkhabibullin.app.domain.model.AuditLog;
import com.mkhabibullin.app.domain.model.AuditStatistics;
import org.mapstruct.BeforeMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator class for audit-related entities and DTOs.
 * Provides validation methods used by {@link AuditMapper} to ensure data integrity
 * during audit operations and statistics generation.
 */
public class AuditMapperValidator {
  
  /**
   * Validates audit log entity before mapping to DTO.
   *
   * @param auditLog The audit log entity to validate
   * @throws ValidationException if validation fails
   */
  @BeforeMapping
  public void validateAuditLog(AuditLog auditLog) throws ValidationException {
    List<String> errors = new ArrayList<>();
    
    try {
      validateUsername(auditLog.getUsername());
    } catch (ValidationException e) {
      errors.addAll(e.getValidationErrors());
    }
    
    try {
      validateMethodName(auditLog.getMethodName());
    } catch (ValidationException e) {
      errors.addAll(e.getValidationErrors());
    }
    
    try {
      validateOperation(auditLog.getOperation());
    } catch (ValidationException e) {
      errors.addAll(e.getValidationErrors());
    }
    
    try {
      validateTimestamp(auditLog.getTimestamp());
    } catch (ValidationException e) {
      errors.addAll(e.getValidationErrors());
    }
    
    try {
      validateExecutionTime(auditLog.getExecutionTimeMs());
    } catch (ValidationException e) {
      errors.addAll(e.getValidationErrors());
    }
    
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }
  
  /**
   * Validates audit statistics entity before mapping to DTO.
   *
   * @param statistics The audit statistics entity to validate
   * @throws ValidationException if validation fails
   */
  @BeforeMapping
  public void validateAuditStatistics(AuditStatistics statistics) throws ValidationException {
    List<String> errors = new ArrayList<>();
    try {
      validateTimestampRange(statistics.getPeriodStart(), statistics.getPeriodEnd());
    } catch (ValidationException e) {
      errors.addAll(e.getValidationErrors());
    }
    try {
      validateOperationCounts(statistics.getOperationCounts());
    } catch (ValidationException e) {
      errors.addAll(e.getValidationErrors());
    }
    try {
      validateUserActivityCounts(statistics.getUserActivityCounts());
    } catch (ValidationException e) {
      errors.addAll(e.getValidationErrors());
    }
    try {
      validateAverageTimeByOperation(statistics.getAverageTimeByOperation());
    } catch (ValidationException e) {
      errors.addAll(e.getValidationErrors());
    }
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }
  
  private void validateUsername(String username) throws ValidationException {
    if (username == null || username.trim().isEmpty()) {
      throw new ValidationException("Username is required");
    }
  }
  
  private void validateMethodName(String methodName) throws ValidationException {
    if (methodName == null || methodName.trim().isEmpty()) {
      throw new ValidationException("Method name is required");
    }
  }
  
  private void validateOperation(String operation) throws ValidationException {
    if (operation == null || operation.trim().isEmpty()) {
      throw new ValidationException("Operation is required");
    }
  }
  
  private void validateTimestamp(LocalDateTime timestamp) throws ValidationException {
    if (timestamp == null) {
      throw new ValidationException("Timestamp is required");
    }
    if (timestamp.isAfter(LocalDateTime.now())) {
      throw new ValidationException("Timestamp cannot be in the future");
    }
  }
  
  private void validateExecutionTime(Long executionTimeMs) throws ValidationException {
    if (executionTimeMs == null) {
      throw new ValidationException("Execution time is required");
    }
    if (executionTimeMs < 0) {
      throw new ValidationException("Execution time cannot be negative");
    }
  }
  
  private void validateTimestampRange(LocalDateTime start, LocalDateTime end) throws ValidationException {
    List<String> errors = new ArrayList<>();
    if (start == null || end == null) {
      errors.add("Both start and end timestamps are required");
    } else {
      if (end.isBefore(start)) {
        errors.add("End timestamp cannot be before start timestamp");
      }
      if (start.isAfter(LocalDateTime.now()) || end.isAfter(LocalDateTime.now())) {
        errors.add("Timestamp range cannot include future dates");
      }
    }
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }
  
  private void validateOperationCounts(Map<String, Long> operationCounts) throws ValidationException {
    List<String> errors = new ArrayList<>();
    if (operationCounts == null) {
      throw new ValidationException("Operation counts map is required");
    }
    operationCounts.forEach((operation, count) -> {
      if (operation == null || operation.trim().isEmpty()) {
        errors.add("Operation name cannot be empty");
      }
      if (count == null || count < 0) {
        errors.add("Invalid operation count for: " + operation);
      }
    });
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }
  
  private void validateUserActivityCounts(Map<String, Long> userActivityCounts) throws ValidationException {
    List<String> errors = new ArrayList<>();
    if (userActivityCounts == null) {
      throw new ValidationException("User activity counts map is required");
    }
    userActivityCounts.forEach((username, count) -> {
      if (username == null || username.trim().isEmpty()) {
        errors.add("Username cannot be empty");
      }
      if (count == null || count < 0) {
        errors.add("Invalid activity count for user: " + username);
      }
    });
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }
  
  private void validateAverageTimeByOperation(Map<String, Double> averageTimeByOperation) throws ValidationException {
    List<String> errors = new ArrayList<>();
    if (averageTimeByOperation == null) {
      throw new ValidationException("Average time by operation map is required");
    }
    averageTimeByOperation.forEach((operation, avgTime) -> {
      if (operation == null || operation.trim().isEmpty()) {
        errors.add("Operation name cannot be empty");
      }
      if (avgTime == null || avgTime < 0) {
        errors.add("Invalid average time for operation: " + operation);
      }
    });
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }
}