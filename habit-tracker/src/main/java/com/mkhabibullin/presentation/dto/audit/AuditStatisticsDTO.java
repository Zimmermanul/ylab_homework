package com.mkhabibullin.presentation.dto.audit;

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
public record AuditStatisticsDTO(
  long totalOperations,
  double averageExecutionTime,
  Map<String, Long> operationCounts,
  Map<String, Long> userActivityCounts,
  Map<String, Double> averageTimeByOperation,
  String mostActiveUser,
  String mostCommonOperation,
  LocalDateTime periodStart,
  LocalDateTime periodEnd
) {
}
