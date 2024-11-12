package com.mkhabibullin.audit.application.validation;

import com.mkhabibullin.audit.domain.exception.AuditValidationException;
import com.mkhabibullin.audit.presentation.dto.AuditLogResponseDTO;
import com.mkhabibullin.audit.presentation.dto.AuditStatisticsDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
/**
 * Validator component for audit-related DTOs.
 * Provides validation methods for ensuring data integrity during audit operations.
 */
@Component
public class AuditValidator {
  
  /**
   * Validates audit log data.
   *
   * @param dto the audit log data to validate
   * @throws AuditValidationException if validation fails
   */
  public void validateAuditLogDTO(AuditLogResponseDTO dto) {
    if (dto == null) {
      throw new AuditValidationException("Audit log data cannot be null");
    }
    validateId(dto.id());
    validateUsername(dto.username());
    validateMethodName(dto.methodName());
    validateOperation(dto.operation());
    validateTimestamp(dto.timestamp());
    validateExecutionTime(dto.executionTimeMs());
    validateRequestUri(dto.requestUri());
    validateRequestMethod(dto.requestMethod());
  }
  
  /**
   * Validates audit statistics data.
   *
   * @param dto the audit statistics data to validate
   * @throws AuditValidationException if validation fails
   */
  public void validateAuditStatisticsDTO(AuditStatisticsDTO dto) {
    if (dto == null) {
      throw new AuditValidationException("Audit statistics data cannot be null");
    }
    validateTotalOperations(dto.totalOperations());
    validateAverageExecutionTime(dto.averageExecutionTime());
    validateOperationCounts(dto.operationCounts());
    validateUserActivityCounts(dto.userActivityCounts());
    validateAverageTimeByOperation(dto.averageTimeByOperation());
    validateMostActiveUser(dto.mostActiveUser());
    validateMostCommonOperation(dto.mostCommonOperation());
    validatePeriodStart(dto.periodStart());
    validatePeriodEnd(dto.periodEnd());
  }
  
  private void validateId(Long id) {
    if (id == null) {
      throw new AuditValidationException("Audit log ID is required");
    }
    if (id <= 0) {
      throw new AuditValidationException("Audit log ID must be positive");
    }
  }
  
  private void validateUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new AuditValidationException("Username is required");
    }
  }
  
  private void validateMethodName(String methodName) {
    if (methodName == null || methodName.trim().isEmpty()) {
      throw new AuditValidationException("Method name is required");
    }
  }
  
  private void validateOperation(String operation) {
    if (operation == null || operation.trim().isEmpty()) {
      throw new AuditValidationException("Operation is required");
    }
  }
  
  private void validateTimestamp(LocalDateTime timestamp) {
    if (timestamp == null) {
      throw new AuditValidationException("Timestamp is required");
    }
    if (timestamp.isAfter(LocalDateTime.now())) {
      throw new AuditValidationException("Timestamp cannot be in the future");
    }
  }
  
  private void validateExecutionTime(Long executionTimeMs) {
    if (executionTimeMs == null) {
      throw new AuditValidationException("Execution time is required");
    }
    if (executionTimeMs < 0) {
      throw new AuditValidationException("Execution time cannot be negative");
    }
  }
  
  private void validateRequestUri(String requestUri) {
    if (requestUri == null || requestUri.trim().isEmpty()) {
      throw new AuditValidationException("Request URI is required");
    }
    if (!requestUri.startsWith("/") && !"N/A".equals(requestUri)) {
      throw new AuditValidationException("Invalid request URI format");
    }
  }
  
  private void validateRequestMethod(String requestMethod) {
    if (requestMethod == null || requestMethod.trim().isEmpty()) {
      throw new AuditValidationException("Request method is required");
    }
    // Could add validation for specific HTTP methods if needed
    // e.g., GET, POST, PUT, DELETE, etc.
  }
  
  private void validateTotalOperations(long totalOperations) {
    if (totalOperations < 0) {
      throw new AuditValidationException("Total operations cannot be negative");
    }
  }
  
  private void validateAverageExecutionTime(double averageExecutionTime) {
    if (averageExecutionTime < 0) {
      throw new AuditValidationException("Average execution time cannot be negative");
    }
  }
  
  private void validateOperationCounts(Map<String, Long> operationCounts) {
    if (operationCounts == null) {
      throw new AuditValidationException("Operation counts cannot be null");
    }
    operationCounts.forEach((operation, count) -> {
      if (operation == null || operation.trim().isEmpty()) {
        throw new AuditValidationException("Operation name cannot be empty");
      }
      if (count == null || count < 0) {
        throw new AuditValidationException(
          String.format("Invalid count for operation '%s': count cannot be negative", operation)
        );
      }
    });
  }
  
  private void validateUserActivityCounts(Map<String, Long> userActivityCounts) {
    if (userActivityCounts == null) {
      throw new AuditValidationException("User activity counts cannot be null");
    }
    userActivityCounts.forEach((username, count) -> {
      if (username == null || username.trim().isEmpty()) {
        throw new AuditValidationException("Username cannot be empty in activity counts");
      }
      if (count == null || count < 0) {
        throw new AuditValidationException(
          String.format("Invalid count for user '%s': count cannot be negative", username)
        );
      }
    });
  }
  
  private void validateAverageTimeByOperation(Map<String, Double> averageTimeByOperation) {
    if (averageTimeByOperation == null) {
      throw new AuditValidationException("Average time by operation cannot be null");
    }
    averageTimeByOperation.forEach((operation, avgTime) -> {
      if (operation == null || operation.trim().isEmpty()) {
        throw new AuditValidationException("Operation name cannot be empty in average times");
      }
      if (avgTime == null || avgTime < 0) {
        throw new AuditValidationException(
          String.format("Invalid average time for operation '%s': time cannot be negative", operation)
        );
      }
    });
  }
  
  private void validateMostActiveUser(String mostActiveUser) {
    if (mostActiveUser == null || mostActiveUser.trim().isEmpty()) {
      throw new AuditValidationException("Most active user cannot be empty");
    }
  }
  
  private void validateMostCommonOperation(String mostCommonOperation) {
    if (mostCommonOperation == null || mostCommonOperation.trim().isEmpty()) {
      throw new AuditValidationException("Most common operation cannot be empty");
    }
  }
  
  private void validatePeriodStart(LocalDateTime periodStart) {
    if (periodStart == null) {
      throw new AuditValidationException("Period start time is required");
    }
    if (periodStart.isAfter(LocalDateTime.now())) {
      throw new AuditValidationException("Period start time cannot be in the future");
    }
  }
  
  private void validatePeriodEnd(LocalDateTime periodEnd) {
    if (periodEnd == null) {
      throw new AuditValidationException("Period end time is required");
    }
    if (periodEnd.isAfter(LocalDateTime.now())) {
      throw new AuditValidationException("Period end time cannot be in the future");
    }
  }
}