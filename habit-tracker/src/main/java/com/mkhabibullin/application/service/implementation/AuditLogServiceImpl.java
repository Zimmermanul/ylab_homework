package com.mkhabibullin.application.service.implementation;

import com.mkhabibullin.application.service.AuditLogService;
import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.domain.model.AuditStatistics;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of AuditLogService that provides audit logging functionality.
 * This class handles the business logic for recording, retrieving, and analyzing
 * audit log entries using the provided repository.
 */
@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {
  
  
  private final AuditLogRepository auditLogRepository;
  
  /**
   * Constructs a new AuditLogServiceImpl with the specified repository.
   *
   * @param auditLogRepository the repository for audit log data
   */
  public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = Objects.requireNonNull(auditLogRepository,
      "auditLogRepository must not be null");
  }
  
  /**
   * Retrieves audit logs for a specific user.
   *
   * @param username the username whose logs to retrieve
   * @return list of audit logs
   */
  @Override
  public List<AuditLog> getUserLogs(String username) {
    return auditLogRepository.findByUsername(username);
  }
  
  /**
   * Retrieves audit logs for a specific operation.
   *
   * @param operation the operation type to query
   * @return list of audit logs
   */
  @Override
  public List<AuditLog> getOperationLogs(String operation) {
    return auditLogRepository.findByOperation(operation);
  }
  
  /**
   * Retrieves the most recent audit logs.
   *
   * @param limit maximum number of logs to retrieve
   * @return list of recent audit logs
   */
  @Override
  public List<AuditLog> getRecentLogs(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit must be greater than 0");
    }
    return auditLogRepository.findRecentLogs(limit);
  }
  
  /**
   * Retrieves audit logs within a specified time range.
   *
   * @param startDateTime start of the time range
   * @param endDateTime   end of the time range
   * @return list of audit logs within the range
   */
  @Override
  public List<AuditLog> getLogsByDateRange(LocalDateTime startDateTime,
                                           LocalDateTime endDateTime) {
    validateDateRange(startDateTime, endDateTime);
    return auditLogRepository.findByTimestampRange(startDateTime, endDateTime);
  }
  
  /**
   * Records a new audit log entry.
   *
   * @param auditLog the audit log entry to save
   */
  @Override
  public void logAuditEvent(AuditLog auditLog) {
    Objects.requireNonNull(auditLog, "auditLog must not be null");
    auditLogRepository.save(auditLog);
  }
  
  /**
   * Generates statistics for audit logs within a specified time range.
   *
   * @param startDateTime start of the time range
   * @param endDateTime   end of the time range
   * @return statistics object containing various metrics
   */
  @Override
  public AuditStatistics getStatistics(LocalDateTime startDateTime,
                                       LocalDateTime endDateTime) {
    validateDateRange(startDateTime, endDateTime);
    
    List<AuditLog> logs = auditLogRepository.findByTimestampRange(startDateTime, endDateTime);
    
    Map<String, Long> operationCounts = calculateOperationCounts(logs);
    Map<String, Long> userActivityCounts = calculateUserActivityCounts(logs);
    Map<String, Double> averageTimeByOperation = calculateAverageTimeByOperation(logs);
    
    String mostActiveUser = findMostActiveUser(userActivityCounts);
    String mostCommonOperation = findMostCommonOperation(operationCounts);
    double averageExecutionTime = calculateAverageExecutionTime(logs);
    
    return new AuditStatistics(
      logs.size(),
      averageExecutionTime,
      operationCounts,
      userActivityCounts,
      averageTimeByOperation,
      mostActiveUser,
      mostCommonOperation,
      startDateTime,
      endDateTime
    );
  }
  
  private void validateDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    Objects.requireNonNull(startDateTime, "startDateTime must not be null");
    Objects.requireNonNull(endDateTime, "endDateTime must not be null");
    if (startDateTime.isAfter(endDateTime)) {
      throw new IllegalArgumentException(
        "startDateTime must not be after endDateTime");
    }
  }
  
  private Map<String, Long> calculateOperationCounts(List<AuditLog> logs) {
    return logs.stream()
      .collect(Collectors.groupingBy(
        AuditLog::getOperation,
        Collectors.counting()
      ));
  }
  
  private Map<String, Long> calculateUserActivityCounts(List<AuditLog> logs) {
    return logs.stream()
      .collect(Collectors.groupingBy(
        AuditLog::getUsername,
        Collectors.counting()
      ));
  }
  
  private Map<String, Double> calculateAverageTimeByOperation(List<AuditLog> logs) {
    return logs.stream()
      .collect(Collectors.groupingBy(
        AuditLog::getOperation,
        Collectors.averagingLong(AuditLog::getExecutionTimeMs)
      ));
  }
  
  private String findMostActiveUser(Map<String, Long> userActivityCounts) {
    return userActivityCounts.entrySet().stream()
      .max(Map.Entry.comparingByValue())
      .map(Map.Entry::getKey)
      .orElse("N/A");
  }
  
  private String findMostCommonOperation(Map<String, Long> operationCounts) {
    return operationCounts.entrySet().stream()
      .max(Map.Entry.comparingByValue())
      .map(Map.Entry::getKey)
      .orElse("N/A");
  }
  
  private double calculateAverageExecutionTime(List<AuditLog> logs) {
    return logs.stream()
      .mapToLong(AuditLog::getExecutionTimeMs)
      .average()
      .orElse(0.0);
  }
}