package com.mkhabibullin.app.application.validation;

import com.mkhabibullin.app.application.mapper.AuditMapper;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Validator class for audit mapping operations.
 * Provides validation methods used by {@link AuditMapper} to ensure
 * data integrity during the mapping process.
 */
public class AuditMapperValidator {
  
  /**
   * Validates the ID field.
   *
   * @param id The ID to validate
   * @return The validated ID
   */
  @Named("validateId")
  public Long validateId(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("ID is required");
    }
    return id;
  }
  
  /**
   * Validates the username field.
   *
   * @param username The username to validate
   * @return The validated username
   */
  @Named("validateUsername")
  public String validateUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username is required");
    }
    return username;
  }
  
  /**
   * Validates the method name field.
   *
   * @param methodName The method name to validate
   * @return The validated method name
   */
  @Named("validateMethodName")
  public String validateMethodName(String methodName) {
    if (methodName == null || methodName.trim().isEmpty()) {
      throw new IllegalArgumentException("Method name is required");
    }
    return methodName;
  }
  
  /**
   * Validates the operation field.
   *
   * @param operation The operation to validate
   * @return The validated operation
   */
  @Named("validateOperation")
  public String validateOperation(String operation) {
    if (operation == null || operation.trim().isEmpty()) {
      throw new IllegalArgumentException("Operation is required");
    }
    return operation;
  }
  
  /**
   * Validates the timestamp field.
   *
   * @param timestamp The timestamp to validate
   * @return The validated timestamp
   */
  @Named("validateTimestamp")
  public LocalDateTime validateTimestamp(LocalDateTime timestamp) {
    if (timestamp == null) {
      throw new IllegalArgumentException("Timestamp is required");
    }
    if (timestamp.isAfter(LocalDateTime.now())) {
      throw new IllegalArgumentException("Timestamp cannot be in the future");
    }
    return timestamp;
  }
  
  /**
   * Validates the execution time field.
   *
   * @param executionTimeMs The execution time to validate
   * @return The validated execution time
   */
  @Named("validateExecutionTime")
  public Long validateExecutionTime(Long executionTimeMs) {
    if (executionTimeMs == null) {
      throw new IllegalArgumentException("Execution time is required");
    }
    if (executionTimeMs < 0) {
      throw new IllegalArgumentException("Execution time cannot be negative");
    }
    return executionTimeMs;
  }
  
  /**
   * Validates the request URI field.
   *
   * @param requestUri The request URI to validate
   * @return The validated request URI
   */
  @Named("validateRequestUri")
  public String validateRequestUri(String requestUri) {
    if (requestUri == null || requestUri.trim().isEmpty()) {
      throw new IllegalArgumentException("Request URI is required");
    }
    return requestUri;
  }
  
  /**
   * Validates the request method field.
   *
   * @param requestMethod The request method to validate
   * @return The validated request method
   */
  @Named("validateRequestMethod")
  public String validateRequestMethod(String requestMethod) {
    if (requestMethod == null || requestMethod.trim().isEmpty()) {
      throw new IllegalArgumentException("Request method is required");
    }
    return requestMethod;
  }
  
  /**
   * Validates the total operations count.
   *
   * @param totalOperations The total operations count to validate
   * @return The validated total operations count
   */
  @Named("validateTotalOperations")
  public Long validateTotalOperations(Long totalOperations) {
    if (totalOperations == null || totalOperations < 0) {
      throw new IllegalArgumentException("Total operations must be non-negative");
    }
    return totalOperations;
  }
  
  /**
   * Validates the average execution time.
   *
   * @param averageExecutionTime The average execution time to validate
   * @return The validated average execution time
   */
  @Named("validateAverageExecutionTime")
  public Double validateAverageExecutionTime(Double averageExecutionTime) {
    if (averageExecutionTime == null || averageExecutionTime < 0) {
      throw new IllegalArgumentException("Average execution time must be non-negative");
    }
    return averageExecutionTime;
  }
  
  /**
   * Validates the operation counts map.
   *
   * @param operationCounts The operation counts map to validate
   * @return The validated operation counts map
   */
  @Named("validateOperationCounts")
  public Map<String, Long> validateOperationCounts(Map<String, Long> operationCounts) {
    if (operationCounts == null) {
      throw new IllegalArgumentException("Operation counts map is required");
    }
    operationCounts.forEach((operation, count) -> {
      if (operation == null || operation.trim().isEmpty()) {
        throw new IllegalArgumentException("Operation name cannot be empty");
      }
      if (count == null || count < 0) {
        throw new IllegalArgumentException("Invalid operation count for: " + operation);
      }
    });
    return operationCounts;
  }
  
  /**
   * Validates the user activity counts map.
   *
   * @param userActivityCounts The user activity counts map to validate
   * @return The validated user activity counts map
   */
  @Named("validateUserActivityCounts")
  public Map<String, Long> validateUserActivityCounts(Map<String, Long> userActivityCounts) {
    if (userActivityCounts == null) {
      throw new IllegalArgumentException("User activity counts map is required");
    }
    userActivityCounts.forEach((username, count) -> {
      if (username == null || username.trim().isEmpty()) {
        throw new IllegalArgumentException("Username cannot be empty");
      }
      if (count == null || count < 0) {
        throw new IllegalArgumentException("Invalid activity count for user: " + username);
      }
    });
    return userActivityCounts;
  }
  
  /**
   * Validates the average time by operation map.
   *
   * @param averageTimeByOperation The average time by operation map to validate
   * @return The validated average time by operation map
   */
  @Named("validateAverageTimeByOperation")
  public Map<String, Double> validateAverageTimeByOperation(Map<String, Double> averageTimeByOperation) {
    if (averageTimeByOperation == null) {
      throw new IllegalArgumentException("Average time by operation map is required");
    }
    averageTimeByOperation.forEach((operation, avgTime) -> {
      if (operation == null || operation.trim().isEmpty()) {
        throw new IllegalArgumentException("Operation name cannot be empty");
      }
      if (avgTime == null || avgTime < 0) {
        throw new IllegalArgumentException("Invalid average time for operation: " + operation);
      }
    });
    return averageTimeByOperation;
  }
  
  /**
   * Validates the most active user field.
   *
   * @param mostActiveUser The most active user to validate
   * @return The validated most active user
   */
  @Named("validateMostActiveUser")
  public String validateMostActiveUser(String mostActiveUser) {
    if (mostActiveUser == null || mostActiveUser.trim().isEmpty()) {
      throw new IllegalArgumentException("Most active user is required");
    }
    return mostActiveUser;
  }
  
  /**
   * Validates the most common operation field.
   *
   * @param mostCommonOperation The most common operation to validate
   * @return The validated most common operation
   */
  @Named("validateMostCommonOperation")
  public String validateMostCommonOperation(String mostCommonOperation) {
    if (mostCommonOperation == null || mostCommonOperation.trim().isEmpty()) {
      throw new IllegalArgumentException("Most common operation is required");
    }
    return mostCommonOperation;
  }
  
  /**
   * Validates the period start timestamp.
   *
   * @param periodStart The period start timestamp to validate
   * @return The validated period start timestamp
   */
  @Named("validatePeriodStart")
  public LocalDateTime validatePeriodStart(LocalDateTime periodStart) {
    if (periodStart == null) {
      throw new IllegalArgumentException("Period start is required");
    }
    if (periodStart.isAfter(LocalDateTime.now())) {
      throw new IllegalArgumentException("Period start cannot be in the future");
    }
    return periodStart;
  }
  
  /**
   * Validates the period end timestamp.
   *
   * @param periodEnd The period end timestamp to validate
   * @return The validated period end timestamp
   */
  @Named("validatePeriodEnd")
  public LocalDateTime validatePeriodEnd(LocalDateTime periodEnd) {
    if (periodEnd == null) {
      throw new IllegalArgumentException("Period end is required");
    }
    if (periodEnd.isAfter(LocalDateTime.now())) {
      throw new IllegalArgumentException("Period end cannot be in the future");
    }
    return periodEnd;
  }
}