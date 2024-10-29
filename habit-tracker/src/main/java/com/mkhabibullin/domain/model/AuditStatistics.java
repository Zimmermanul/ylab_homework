package com.mkhabibullin.domain.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents aggregated statistics for audit logs.
 * Contains various metrics and counts for analysis.
 */
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
  
  public AuditStatistics(long totalOperations,
                         double averageExecutionTime,
                         Map<String, Long> operationCounts,
                         Map<String, Long> userActivityCounts,
                         Map<String, Double> averageTimeByOperation,
                         String mostActiveUser,
                         String mostCommonOperation,
                         LocalDateTime periodStart,
                         LocalDateTime periodEnd) {
    this.totalOperations = totalOperations;
    this.averageExecutionTime = averageExecutionTime;
    this.operationCounts = operationCounts;
    this.userActivityCounts = userActivityCounts;
    this.averageTimeByOperation = averageTimeByOperation;
    this.mostActiveUser = mostActiveUser;
    this.mostCommonOperation = mostCommonOperation;
    this.periodStart = periodStart;
    this.periodEnd = periodEnd;
  }
  
  public long getTotalOperations() {
    return totalOperations;
  }
  
  public double getAverageExecutionTime() {
    return averageExecutionTime;
  }
  
  public Map<String, Long> getOperationCounts() {
    return operationCounts;
  }
  
  public Map<String, Long> getUserActivityCounts() {
    return userActivityCounts;
  }
  
  public Map<String, Double> getAverageTimeByOperation() {
    return averageTimeByOperation;
  }
  
  public String getMostActiveUser() {
    return mostActiveUser;
  }
  
  public String getMostCommonOperation() {
    return mostCommonOperation;
  }
  
  public LocalDateTime getPeriodStart() {
    return periodStart;
  }
  
  public LocalDateTime getPeriodEnd() {
    return periodEnd;
  }
}