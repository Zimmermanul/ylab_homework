package com.mkhabibullin.app.application.service;

import com.mkhabibullin.app.domain.model.AuditLog;
import com.mkhabibullin.app.domain.model.AuditStatistics;
import com.mkhabibullin.app.infrastructure.persistence.repository.AuditLogDbRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing audit log operations.
 * Contains business logic for audit operations.
 */
public class AuditLogService {
  private final AuditLogDbRepository auditLogRepository;
  
  /**
   * Constructs a new AuditLogService with the specified repository.
   *
   * @param auditLogRepository the repository for audit log data
   */
  public AuditLogService(AuditLogDbRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }
  
  /**
   * Retrieves audit logs for a specific user.
   *
   * @param username the username whose logs to retrieve
   * @return list of audit logs
   * @throws IOException if there's an error during retrieval
   */
  public List<AuditLog> getUserLogs(String username) throws IOException {
    return auditLogRepository.getByUsername(username);
  }
  
  /**
   * Retrieves audit logs for a specific operation.
   *
   * @param operation the operation type to query
   * @return list of audit logs
   * @throws IOException if there's an error during retrieval
   */
  public List<AuditLog> getOperationLogs(String operation) throws IOException {
    return auditLogRepository.getByOperation(operation);
  }
  
  /**
   * Retrieves the most recent audit logs.
   *
   * @param limit maximum number of logs to retrieve
   * @return list of recent audit logs
   * @throws IOException if there's an error during retrieval
   */
  public List<AuditLog> getRecentLogs(int limit) throws IOException {
    return auditLogRepository.getRecentLogs(limit);
  }
  
  /**
   * Retrieves audit logs within a specified time range.
   *
   * @param startDateTime start of the time range
   * @param endDateTime   end of the time range
   * @return list of audit logs within the range
   * @throws IOException if there's an error during retrieval
   */
  public List<AuditLog> getLogsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
    return auditLogRepository.getByTimestampRange(startDateTime, endDateTime);
  }
  
  /**
   * Records a new audit log entry.
   *
   * @param auditLog the audit log entry to save
   * @throws IOException if there's an error during saving
   */
  public void logAuditEvent(AuditLog auditLog) throws IOException {
    auditLogRepository.save(auditLog);
  }
  
  /**
   * Generates statistics for audit logs within a specified time range.
   *
   * @param startDateTime start of the time range
   * @param endDateTime   end of the time range
   * @return statistics object containing various metrics
   * @throws IOException if there's an error during retrieval
   */
  public AuditStatistics getStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
    List<AuditLog> logs = auditLogRepository.getByTimestampRange(startDateTime, endDateTime);
    Map<String, Long> operationCounts = logs.stream()
      .collect(Collectors.groupingBy(
        AuditLog::getOperation,
        Collectors.counting()
      ));
    Map<String, Long> userActivityCounts = logs.stream()
      .collect(Collectors.groupingBy(
        AuditLog::getUsername,
        Collectors.counting()
      ));
    Map<String, Double> averageTimeByOperation = logs.stream()
      .collect(Collectors.groupingBy(
        AuditLog::getOperation,
        Collectors.averagingLong(AuditLog::getExecutionTimeMs)
      ));
    String mostActiveUser = userActivityCounts.entrySet().stream()
      .max(Map.Entry.comparingByValue())
      .map(Map.Entry::getKey)
      .orElse("N/A");
    String mostCommonOperation = operationCounts.entrySet().stream()
      .max(Map.Entry.comparingByValue())
      .map(Map.Entry::getKey)
      .orElse("N/A");
    double averageExecutionTime = logs.stream()
      .mapToLong(AuditLog::getExecutionTimeMs)
      .average()
      .orElse(0.0);
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
}