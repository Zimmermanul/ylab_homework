package com.mkhabibullin.app.dto.audit;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for user activity summary.
 * Contains statistics about a specific user's activities.
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
