package com.mkhabibullin.application.validation;

import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.presentation.dto.audit.AuditLogResponseDTO;
import com.mkhabibullin.presentation.dto.audit.AuditStatisticsDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Validator component for audit-related DTOs.
 * Provides validation methods for ensuring data integrity during audit operations.
 */
@Component
public class AuditMapperValidator {
  
  /**
   * Validates audit log data.
   *
   * @param dto the audit log data to validate
   * @throws ValidationException if validation fails
   */
  public void validateAuditLogDTO(AuditLogResponseDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("Audit log data cannot be null");
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
   * @throws ValidationException if validation fails
   */
  public void validateAuditStatisticsDTO(AuditStatisticsDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("Audit statistics data cannot be null");
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
  
  private void validateId(Long id) throws ValidationException {
    if (id == null) {
      throw new ValidationException("ID is required");
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
  
  private void validateRequestUri(String requestUri) throws ValidationException {
    if (requestUri == null || requestUri.trim().isEmpty()) {
      throw new ValidationException("Request URI is required");
    }
  }
  
  private void validateRequestMethod(String requestMethod) throws ValidationException {
    if (requestMethod == null || requestMethod.trim().isEmpty()) {
      throw new ValidationException("Request method is required");
    }
  }
  
  private void validateTotalOperations(Long totalOperations) throws ValidationException {
    if (totalOperations == null || totalOperations < 0) {
      throw new ValidationException("Total operations must be non-negative");
    }
  }
  
  private void validateAverageExecutionTime(Double averageExecutionTime) throws ValidationException {
    if (averageExecutionTime == null || averageExecutionTime < 0) {
      throw new ValidationException("Average execution time must be non-negative");
    }
  }
  
  private void validateOperationCounts(Map<String, Long> operationCounts) throws ValidationException {
    if (operationCounts == null) {
      throw new ValidationException("Operation counts map is required");
    }
    for (Map.Entry<String, Long> entry : operationCounts.entrySet()) {
      String operation = entry.getKey();
      Long count = entry.getValue();
      if (operation == null || operation.trim().isEmpty()) {
        throw new ValidationException("Operation name cannot be empty");
      }
      if (count == null || count < 0) {
        throw new ValidationException("Invalid operation count for: " + operation);
      }
    }
  }
  
  private void validateUserActivityCounts(Map<String, Long> userActivityCounts) throws ValidationException {
    if (userActivityCounts == null) {
      throw new ValidationException("User activity counts map is required");
    }
    for (Map.Entry<String, Long> entry : userActivityCounts.entrySet()) {
      String username = entry.getKey();
      Long count = entry.getValue();
      if (username == null || username.trim().isEmpty()) {
        throw new ValidationException("Username cannot be empty");
      }
      if (count == null || count < 0) {
        throw new ValidationException("Invalid activity count for user: " + username);
      }
    }
  }
  
  private void validateAverageTimeByOperation(Map<String, Double> averageTimeByOperation) throws ValidationException {
    if (averageTimeByOperation == null) {
      throw new ValidationException("Average time by operation map is required");
    }
    for (Map.Entry<String, Double> entry : averageTimeByOperation.entrySet()) {
      String operation = entry.getKey();
      Double avgTime = entry.getValue();
      if (operation == null || operation.trim().isEmpty()) {
        throw new ValidationException("Operation name cannot be empty");
      }
      if (avgTime == null || avgTime < 0) {
        throw new ValidationException("Invalid average time for operation: " + operation);
      }
    }
  }
  
  private void validateMostActiveUser(String mostActiveUser) throws ValidationException {
    if (mostActiveUser == null || mostActiveUser.trim().isEmpty()) {
      throw new ValidationException("Most active user is required");
    }
  }
  
  private void validateMostCommonOperation(String mostCommonOperation) throws ValidationException {
    if (mostCommonOperation == null || mostCommonOperation.trim().isEmpty()) {
      throw new ValidationException("Most common operation is required");
    }
  }
  
  private void validatePeriodStart(LocalDateTime periodStart) throws ValidationException {
    if (periodStart == null) {
      throw new ValidationException("Period start is required");
    }
    if (periodStart.isAfter(LocalDateTime.now())) {
      throw new ValidationException("Period start cannot be in the future");
    }
  }
  
  private void validatePeriodEnd(LocalDateTime periodEnd) throws ValidationException {
    if (periodEnd == null) {
      throw new ValidationException("Period end is required");
    }
    if (periodEnd.isAfter(LocalDateTime.now())) {
      throw new ValidationException("Period end cannot be in the future");
    }
  }
}