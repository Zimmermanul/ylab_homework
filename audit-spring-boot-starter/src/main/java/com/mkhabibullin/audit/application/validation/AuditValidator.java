package com.mkhabibullin.audit.application.validation;

import com.mkhabibullin.audit.common.MessageConstants;
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
      throw new AuditValidationException(MessageConstants.AUDIT_LOG_NULL);
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
      throw new AuditValidationException(MessageConstants.AUDIT_STATS_NULL);
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
      throw new AuditValidationException(MessageConstants.ID_REQUIRED);
    }
    if (id <= 0) {
      throw new AuditValidationException(MessageConstants.ID_POSITIVE);
    }
  }
  
  private void validateUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new AuditValidationException(MessageConstants.USERNAME_REQUIRED);
    }
  }
  
  private void validateMethodName(String methodName) {
    if (methodName == null || methodName.trim().isEmpty()) {
      throw new AuditValidationException(MessageConstants.METHOD_NAME_REQUIRED);
    }
  }
  
  private void validateOperation(String operation) {
    if (operation == null || operation.trim().isEmpty()) {
      throw new AuditValidationException(MessageConstants.OPERATION_REQUIRED);
    }
  }
  
  private void validateTimestamp(LocalDateTime timestamp) {
    if (timestamp == null) {
      throw new AuditValidationException(MessageConstants.TIMESTAMP_REQUIRED);
    }
    if (timestamp.isAfter(LocalDateTime.now())) {
      throw new AuditValidationException(MessageConstants.TIMESTAMP_FUTURE);
    }
  }
  
  private void validateExecutionTime(Long executionTimeMs) {
    if (executionTimeMs == null) {
      throw new AuditValidationException(MessageConstants.EXECUTION_TIME_REQUIRED);
    }
    if (executionTimeMs < 0) {
      throw new AuditValidationException(MessageConstants.EXECUTION_TIME_NEGATIVE);
    }
  }
  
  private void validateRequestUri(String requestUri) {
    if (requestUri == null || requestUri.trim().isEmpty()) {
      throw new AuditValidationException(MessageConstants.REQUEST_URI_REQUIRED);
    }
    if (!requestUri.startsWith("/") && !"N/A".equals(requestUri)) {
      throw new AuditValidationException(MessageConstants.INVALID_URI_FORMAT);
    }
  }
  
  private void validateRequestMethod(String requestMethod) {
    if (requestMethod == null || requestMethod.trim().isEmpty()) {
      throw new AuditValidationException(MessageConstants.REQUEST_METHOD_REQUIRED);
    }
  }
  
  private void validateTotalOperations(long totalOperations) {
    if (totalOperations < 0) {
      throw new AuditValidationException(MessageConstants.TOTAL_OPERATIONS_NEGATIVE);
    }
  }
  
  private void validateAverageExecutionTime(double averageExecutionTime) {
    if (averageExecutionTime < 0) {
      throw new AuditValidationException(MessageConstants.AVG_EXECUTION_TIME_NEGATIVE);
    }
  }
  
  private void validateOperationCounts(Map<String, Long> operationCounts) {
    if (operationCounts == null) {
      throw new AuditValidationException(MessageConstants.OPERATION_COUNTS_NULL);
    }
    operationCounts.forEach((operation, count) -> {
      if (operation == null || operation.trim().isEmpty()) {
        throw new AuditValidationException(MessageConstants.OPERATION_NAME_EMPTY);
      }
      if (count == null || count < 0) {
        throw new AuditValidationException(
          String.format(MessageConstants.INVALID_COUNT_FORMAT, operation)
        );
      }
    });
  }
  
  private void validateUserActivityCounts(Map<String, Long> userActivityCounts) {
    if (userActivityCounts == null) {
      throw new AuditValidationException(MessageConstants.USER_ACTIVITY_COUNTS_NULL);
    }
    userActivityCounts.forEach((username, count) -> {
      if (username == null || username.trim().isEmpty()) {
        throw new AuditValidationException(MessageConstants.USERNAME_EMPTY_COUNTS);
      }
      if (count == null || count < 0) {
        throw new AuditValidationException(
          String.format(MessageConstants.INVALID_USER_COUNT_FORMAT, username)
        );
      }
    });
  }
  
  private void validateAverageTimeByOperation(Map<String, Double> averageTimeByOperation) {
    if (averageTimeByOperation == null) {
      throw new AuditValidationException(MessageConstants.AVG_TIME_OPERATION_NULL);
    }
    averageTimeByOperation.forEach((operation, avgTime) -> {
      if (operation == null || operation.trim().isEmpty()) {
        throw new AuditValidationException(MessageConstants.OPERATION_NAME_EMPTY_AVG);
      }
      if (avgTime == null || avgTime < 0) {
        throw new AuditValidationException(
          String.format(MessageConstants.INVALID_AVG_TIME_FORMAT, operation)
        );
      }
    });
  }
  
  private void validateMostActiveUser(String mostActiveUser) {
    if (mostActiveUser == null || mostActiveUser.trim().isEmpty()) {
      throw new AuditValidationException(MessageConstants.MOST_ACTIVE_USER_EMPTY);
    }
  }
  
  private void validateMostCommonOperation(String mostCommonOperation) {
    if (mostCommonOperation == null || mostCommonOperation.trim().isEmpty()) {
      throw new AuditValidationException(MessageConstants.MOST_COMMON_OPERATION_EMPTY);
    }
  }
  
  private void validatePeriodStart(LocalDateTime periodStart) {
    if (periodStart == null) {
      throw new AuditValidationException(MessageConstants.PERIOD_START_REQUIRED);
    }
    if (periodStart.isAfter(LocalDateTime.now())) {
      throw new AuditValidationException(MessageConstants.PERIOD_START_FUTURE);
    }
  }
  
  private void validatePeriodEnd(LocalDateTime periodEnd) {
    if (periodEnd == null) {
      throw new AuditValidationException(MessageConstants.PERIOD_END_REQUIRED);
    }
    if (periodEnd.isAfter(LocalDateTime.now())) {
      throw new AuditValidationException(MessageConstants.PERIOD_END_FUTURE);
    }
  }
}