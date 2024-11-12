package com.mkhabibullin.audit.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents aggregated statistics for audit logs.
 * Contains various metrics and counts for analysis.
 */
@Getter
@AllArgsConstructor
public class AuditStatistics {
  private final long totalOperations;
  private final double averageExecutionTime;
  private final Map<String, Long> operationCounts;
  private final Map<String, Long> userActivityCounts;
  private final Map<String, Double> averageTimeByOperation;
  private final String mostActiveUser;
  private final String mostCommonOperation;
  private final LocalDateTime periodStart;
  private final LocalDateTime periodEnd;
}