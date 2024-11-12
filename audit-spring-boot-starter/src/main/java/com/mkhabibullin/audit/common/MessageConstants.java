package com.mkhabibullin.audit.common;

public final class MessageConstants {
  private MessageConstants() {
  }
  
  // Null checks
  public static final String AUDIT_LOG_NULL = "Audit log data cannot be null";
  public static final String AUDIT_STATS_NULL = "Audit statistics data cannot be null";
  
  // Field requirements
  public static final String ID_REQUIRED = "Audit log ID is required";
  public static final String ID_POSITIVE = "Audit log ID must be positive";
  public static final String USERNAME_REQUIRED = "Username is required";
  public static final String METHOD_NAME_REQUIRED = "Method name is required";
  public static final String OPERATION_REQUIRED = "Operation is required";
  public static final String TIMESTAMP_REQUIRED = "Timestamp is required";
  public static final String EXECUTION_TIME_REQUIRED = "Execution time is required";
  public static final String REQUEST_URI_REQUIRED = "Request URI is required";
  public static final String REQUEST_METHOD_REQUIRED = "Request method is required";
  
  // Validation messages
  public static final String TIMESTAMP_FUTURE = "Timestamp cannot be in the future";
  public static final String EXECUTION_TIME_NEGATIVE = "Execution time cannot be negative";
  public static final String INVALID_URI_FORMAT = "Invalid request URI format";
  public static final String TOTAL_OPERATIONS_NEGATIVE = "Total operations cannot be negative";
  public static final String AVG_EXECUTION_TIME_NEGATIVE = "Average execution time cannot be negative";
  
  // Map validation messages
  public static final String OPERATION_COUNTS_NULL = "Operation counts cannot be null";
  public static final String OPERATION_NAME_EMPTY = "Operation name cannot be empty";
  public static final String USER_ACTIVITY_COUNTS_NULL = "User activity counts cannot be null";
  public static final String USERNAME_EMPTY_COUNTS = "Username cannot be empty in activity counts";
  public static final String AVG_TIME_OPERATION_NULL = "Average time by operation cannot be null";
  public static final String OPERATION_NAME_EMPTY_AVG = "Operation name cannot be empty in average times";
  
  // Statistics validation
  public static final String MOST_ACTIVE_USER_EMPTY = "Most active user cannot be empty";
  public static final String MOST_COMMON_OPERATION_EMPTY = "Most common operation cannot be empty";
  public static final String PERIOD_START_REQUIRED = "Period start time is required";
  public static final String PERIOD_END_REQUIRED = "Period end time is required";
  public static final String PERIOD_START_FUTURE = "Period start time cannot be in the future";
  public static final String PERIOD_END_FUTURE = "Period end time cannot be in the future";
  
  // Format strings
  public static final String INVALID_COUNT_FORMAT = "Invalid count for operation '%s': count cannot be negative";
  public static final String INVALID_USER_COUNT_FORMAT = "Invalid count for user '%s': count cannot be negative";
  public static final String INVALID_AVG_TIME_FORMAT = "Invalid average time for operation '%s': time cannot be negative";
}