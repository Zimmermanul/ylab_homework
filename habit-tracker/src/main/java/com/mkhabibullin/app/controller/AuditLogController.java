package com.mkhabibullin.app.controller;

import com.mkhabibullin.app.data.AuditLogDbRepository;
import com.mkhabibullin.app.dto.audit.AuditLogResponseDTO;
import com.mkhabibullin.app.dto.audit.AuditStatisticsDTO;
import com.mkhabibullin.app.dto.audit.UserActivityDTO;
import com.mkhabibullin.app.model.AuditLog;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for managing audit log operations and generating statistics.
 * Provides methods for retrieving and analyzing audit log data.
 */
public class AuditLogController {
  private final AuditLogDbRepository auditLogRepository;
  
  /**
   * Constructs a new AuditLogController with the specified repository.
   *
   * @param auditLogRepository the repository for audit log data
   */
  public AuditLogController(AuditLogDbRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }
  
  /**
   * Retrieves audit logs for a specific user.
   *
   * @param username the username whose logs to retrieve
   * @return list of audit log DTOs
   */
  public List<AuditLogResponseDTO> getUserLogs(String username) {
    List<AuditLog> logs = auditLogRepository.getByUsername(username);
    return logs.stream()
      .map(this::mapToResponseDTO)
      .collect(Collectors.toList());
  }
  
  /**
   * Retrieves audit logs for a specific operation type.
   *
   * @param operation the operation type to query
   * @return list of audit log DTOs
   */
  public List<AuditLogResponseDTO> getOperationLogs(String operation) {
    List<AuditLog> logs = auditLogRepository.getByOperation(operation);
    return logs.stream()
      .map(this::mapToResponseDTO)
      .collect(Collectors.toList());
  }
  
  /**
   * Retrieves the most recent audit logs.
   *
   * @param limit maximum number of logs to retrieve
   * @return list of audit log DTOs
   */
  public List<AuditLogResponseDTO> getRecentLogs(int limit) {
    List<AuditLog> logs = auditLogRepository.getRecentLogs(limit);
    return logs.stream()
      .map(this::mapToResponseDTO)
      .collect(Collectors.toList());
  }
  
  /**
   * Generates statistics for audit logs within a specified time range.
   *
   * @param startDateTime start of the time range
   * @param endDateTime   end of the time range
   * @return statistics DTO containing various metrics
   */
  public AuditStatisticsDTO getStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime) {
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
    
    return new AuditStatisticsDTO(
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
  
  /**
   * Generates activity summary for a specific user.
   *
   * @param username the username to analyze
   * @return user activity DTO containing usage statistics
   */
  public UserActivityDTO getUserActivity(String username) {
    List<AuditLog> userLogs = auditLogRepository.getByUsername(username);
    
    if (userLogs.isEmpty()) {
      return new UserActivityDTO(
        username, 0, Map.of(), 0.0,
        null, null
      );
    }
    Map<String, Long> operationCounts = userLogs.stream()
      .collect(Collectors.groupingBy(
        AuditLog::getOperation,
        Collectors.counting()
      ));
    double averageExecutionTime = userLogs.stream()
      .mapToLong(AuditLog::getExecutionTimeMs)
      .average()
      .orElse(0.0);
    LocalDateTime firstOperation = userLogs.stream()
      .min(Comparator.comparing(AuditLog::getTimestamp))
      .map(AuditLog::getTimestamp)
      .orElse(null);
    LocalDateTime lastOperation = userLogs.stream()
      .max(Comparator.comparing(AuditLog::getTimestamp))
      .map(AuditLog::getTimestamp)
      .orElse(null);
    return new UserActivityDTO(
      username,
      userLogs.size(),
      operationCounts,
      averageExecutionTime,
      firstOperation,
      lastOperation
    );
  }
  
  /**
   * Maps an AuditLog entity to its DTO representation.
   *
   * @param log the audit log entity to map
   * @return the corresponding DTO
   */
  private AuditLogResponseDTO mapToResponseDTO(AuditLog log) {
    return new AuditLogResponseDTO(
      log.getId(),
      log.getUsername(),
      log.getMethodName(),
      log.getOperation(),
      log.getTimestamp(),
      log.getExecutionTimeMs(),
      log.getRequestUri(),
      log.getRequestMethod()
    );
  }
}