package com.mkhabibullin.audit.domain.model;

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
  
  /**
   * Constructs a new AuditStatistics with the specified metrics.
   *
   * @param totalOperations        total number of operations performed
   * @param averageExecutionTime   average execution time across all operations
   * @param operationCounts        map of operation types to their occurrence counts
   * @param userActivityCounts     map of users to their activity counts
   * @param averageTimeByOperation map of operations to their average execution times
   * @param mostActiveUser         username of the most active user
   * @param mostCommonOperation    name of the most frequently performed operation
   * @param periodStart            start time of the statistics period
   * @param periodEnd              end time of the statistics period
   */
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
  
  /**
   * Gets the total number of operations performed during the period.
   *
   * @return total operation count
   */
  public long getTotalOperations() {
    return totalOperations;
  }
  
  /**
   * Gets the average execution time across all operations.
   *
   * @return average execution time in milliseconds
   */
  public double getAverageExecutionTime() {
    return averageExecutionTime;
  }
  
  /**
   * Gets the count of occurrences for each operation type.
   *
   * @return map of operation names to their counts
   */
  public Map<String, Long> getOperationCounts() {
    return operationCounts;
  }
  
  /**
   * Gets the activity count for each user.
   *
   * @return map of usernames to their activity counts
   */
  public Map<String, Long> getUserActivityCounts() {
    return userActivityCounts;
  }
  
  /**
   * Gets the average execution time for each operation type.
   *
   * @return map of operation names to their average execution times
   */
  public Map<String, Double> getAverageTimeByOperation() {
    return averageTimeByOperation;
  }
  
  /**
   * Gets the username of the most active user during the period.
   *
   * @return username of the most active user
   */
  public String getMostActiveUser() {
    return mostActiveUser;
  }
  
  /**
   * Gets the name of the most frequently performed operation.
   *
   * @return name of the most common operation
   */
  public String getMostCommonOperation() {
    return mostCommonOperation;
  }
  
  /**
   * Gets the start time of the statistics period.
   *
   * @return period start time
   */
  public LocalDateTime getPeriodStart() {
    return periodStart;
  }
  
  /**
   * Gets the end time of the statistics period.
   *
   * @return period end time
   */
  public LocalDateTime getPeriodEnd() {
    return periodEnd;
  }
}