package com.mkhabibullin.presentation.dto.audit;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for audit statistics.
 * Contains aggregated statistics about audit logs within a specified time period,
 * including operation counts, user activity, and performance metrics.
 *
 * @param totalOperations Total number of operations performed in the period
 * @param averageExecutionTime Average execution time across all operations in milliseconds
 * @param operationCounts Map of operation types to their occurrence count
 * @param userActivityCounts Map of usernames to their operation count
 * @param averageTimeByOperation Map of operation types to their average execution time
 * @param mostActiveUser Username of the user with the most operations
 * @param mostCommonOperation The most frequently performed operation type
 * @param periodStart Start of the time period for these statistics
 * @param periodEnd End of the time period for these statistics
 */
@Schema(description = "Statistical analysis of audit logs")
public record AuditStatisticsDTO(
  @Schema(description = "Total number of operations performed")
  long totalOperations,
  
  @Schema(description = "Average execution time across all operations in milliseconds", example = "145.5")
  double averageExecutionTime,
  
  @Schema(description = "Count of each operation type",
    example = "{\"Create Habit\": 50, \"Update Habit\": 30}")
  Map<String, Long> operationCounts,
  
  @Schema(description = "Count of activities by user",
    example = "{\"john.doe\": 100, \"jane.smith\": 75}")
  Map<String, Long> userActivityCounts,
  
  @Schema(description = "Average execution time by operation type",
    example = "{\"Create Habit\": 150.5, \"Update Habit\": 120.3}")
  Map<String, Double> averageTimeByOperation,
  
  @Schema(description = "Username of the most active user", example = "john.doe")
  String mostActiveUser,
  
  @Schema(description = "Most frequently performed operation", example = "Create Habit")
  String mostCommonOperation,
  
  @Schema(description = "Start of the analysis period", example = "2024-03-01T00:00:00")
  LocalDateTime periodStart,
  
  @Schema(description = "End of the analysis period", example = "2024-03-31T23:59:59")
  LocalDateTime periodEnd
) {
}