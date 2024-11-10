package com.mkhabibullin.application.validation;

import com.mkhabibullin.common.MessageConstants;
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
public class AuditValidator {
  
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
      throw new ValidationException(MessageConstants.ID_REQUIRED);
    }
  }
  
  private void validateUsername(String username) throws ValidationException {
    if (username == null || username.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.USERNAME_REQUIRED);
    }
  }
  
  private void validateMethodName(String methodName) throws ValidationException {
    if (methodName == null || methodName.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.METHOD_NAME_REQUIRED);
    }
  }
  
  private void validateOperation(String operation) throws ValidationException {
    if (operation == null || operation.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.OPERATION_REQUIRED);
    }
  }
  
  private void validateTimestamp(LocalDateTime timestamp) throws ValidationException {
    if (timestamp == null) {
      throw new ValidationException(MessageConstants.TIMESTAMP_REQUIRED);
    }
    if (timestamp.isAfter(LocalDateTime.now())) {
      throw new ValidationException(MessageConstants.TIMESTAMP_FUTURE);
    }
  }
  
  private void validateExecutionTime(Long executionTimeMs) throws ValidationException {
    if (executionTimeMs == null) {
      throw new ValidationException(MessageConstants.EXECUTION_TIME_REQUIRED);
    }
    if (executionTimeMs < 0) {
      throw new ValidationException(MessageConstants.EXECUTION_TIME_NEGATIVE);
    }
  }
  
  private void validateRequestUri(String requestUri) throws ValidationException {
    if (requestUri == null || requestUri.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.REQUEST_URI_REQUIRED);
    }
  }
  
  private void validateRequestMethod(String requestMethod) throws ValidationException {
    if (requestMethod == null || requestMethod.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.REQUEST_METHOD_REQUIRED);
    }
  }
  
  private void validateTotalOperations(Long totalOperations) throws ValidationException {
    if (totalOperations == null || totalOperations < 0) {
      throw new ValidationException(MessageConstants.TOTAL_OPERATIONS_INVALID);
    }
  }
  
  private void validateAverageExecutionTime(Double averageExecutionTime) throws ValidationException {
    if (averageExecutionTime == null || averageExecutionTime < 0) {
      throw new ValidationException(MessageConstants.AVG_EXECUTION_TIME_INVALID);
    }
  }
  
  private void validateOperationCounts(Map<String, Long> operationCounts) throws ValidationException {
    if (operationCounts == null) {
      throw new ValidationException(MessageConstants.OPERATION_COUNTS_REQUIRED);
    }
    for (Map.Entry<String, Long> entry : operationCounts.entrySet()) {
      String operation = entry.getKey();
      Long count = entry.getValue();
      if (operation == null || operation.trim().isEmpty()) {
        throw new ValidationException(MessageConstants.OPERATION_NAME_EMPTY);
      }
      if (count == null || count < 0) {
        throw new ValidationException(String.format(MessageConstants.INVALID_OPERATION_COUNT, operation));
      }
    }
  }
  
  private void validateUserActivityCounts(Map<String, Long> userActivityCounts) throws ValidationException {
    if (userActivityCounts == null) {
      throw new ValidationException(MessageConstants.USER_ACTIVITY_COUNTS_REQUIRED);
    }
    for (Map.Entry<String, Long> entry : userActivityCounts.entrySet()) {
      String username = entry.getKey();
      Long count = entry.getValue();
      if (username == null || username.trim().isEmpty()) {
        throw new ValidationException(MessageConstants.USERNAME_EMPTY);
      }
      if (count == null || count < 0) {
        throw new ValidationException(String.format(MessageConstants.INVALID_USER_COUNT, username));
      }
    }
  }
  
  private void validateAverageTimeByOperation(Map<String, Double> averageTimeByOperation) throws ValidationException {
    if (averageTimeByOperation == null) {
      throw new ValidationException(MessageConstants.AVG_TIME_BY_OPERATION_REQUIRED);
    }
    for (Map.Entry<String, Double> entry : averageTimeByOperation.entrySet()) {
      String operation = entry.getKey();
      Double avgTime = entry.getValue();
      if (operation == null || operation.trim().isEmpty()) {
        throw new ValidationException(MessageConstants.OPERATION_NAME_EMPTY);
      }
      if (avgTime == null || avgTime < 0) {
        throw new ValidationException(String.format(MessageConstants.INVALID_AVG_TIME, operation));
      }
    }
  }
  
  private void validateMostActiveUser(String mostActiveUser) throws ValidationException {
    if (mostActiveUser == null || mostActiveUser.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.MOST_ACTIVE_USER_REQUIRED);
    }
  }
  
  private void validateMostCommonOperation(String mostCommonOperation) throws ValidationException {
    if (mostCommonOperation == null || mostCommonOperation.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.MOST_COMMON_OPERATION_REQUIRED);
    }
  }
  
  private void validatePeriodStart(LocalDateTime periodStart) throws ValidationException {
    if (periodStart == null) {
      throw new ValidationException(MessageConstants.PERIOD_START_REQUIRED);
    }
    if (periodStart.isAfter(LocalDateTime.now())) {
      throw new ValidationException(MessageConstants.PERIOD_START_FUTURE);
    }
  }
  
  private void validatePeriodEnd(LocalDateTime periodEnd) throws ValidationException {
    if (periodEnd == null) {
      throw new ValidationException(MessageConstants.PERIOD_END_REQUIRED);
    }
    if (periodEnd.isAfter(LocalDateTime.now())) {
      throw new ValidationException(MessageConstants.PERIOD_END_FUTURE);
    }
  }
}