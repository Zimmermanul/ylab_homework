package com.mkhabibullin.app.dto.audit;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for audit statistics.
 * Contains aggregated statistics about audit logs.
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
