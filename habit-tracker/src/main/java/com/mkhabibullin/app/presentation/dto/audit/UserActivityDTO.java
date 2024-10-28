package com.mkhabibullin.app.presentation.dto.audit;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for user activity summary.
 * Contains statistics about a specific user's activities within the system,
 * including operation history and performance metrics.
 *
 * @param username Name of the user whose activity is being summarized
 * @param totalOperations Total number of operations performed by the user
 * @param operationCounts Map of operation types to their occurrence count for this user
 * @param averageExecutionTime Average execution time of the user's operations in milliseconds
 * @param firstOperation Timestamp of the user's first recorded operation
 * @param lastOperation Timestamp of the user's most recent operation
 */
public record UserActivityDTO(
  String username,
  long totalOperations,
  Map<String, Long> operationCounts,
  double averageExecutionTime,
  LocalDateTime firstOperation,
  LocalDateTime lastOperation
) {
}
