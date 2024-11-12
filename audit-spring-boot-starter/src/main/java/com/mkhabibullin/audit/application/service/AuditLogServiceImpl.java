package com.mkhabibullin.audit.application.service;

import com.mkhabibullin.audit.domain.exception.AuditValidationException;
import com.mkhabibullin.audit.domain.model.AuditLog;
import com.mkhabibullin.audit.domain.model.AuditStatistics;
import com.mkhabibullin.audit.persistence.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogServiceImpl implements AuditLogService {
  private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);
  private final AuditLogRepository auditLogRepository;
  
  @Override
  public List<AuditLog> getUserLogs(String username) {
    log.debug("Retrieving audit logs for user: {}", username);
    Objects.requireNonNull(username, "Username must not be null");
    return auditLogRepository.findByUsername(username);
  }
  
  @Override
  public List<AuditLog> getOperationLogs(String operation) {
    log.debug("Retrieving audit logs for operation: {}", operation);
    Objects.requireNonNull(operation, "Operation must not be null");
    return auditLogRepository.findByOperation(operation);
  }
  
  @Override
  public List<AuditLog> getRecentLogs(int limit) {
    log.debug("Retrieving {} recent audit logs", limit);
    if (limit <= 0) {
      throw new AuditValidationException("Limit must be greater than zero");
    }
    return auditLogRepository.findRecentLogs(limit);
  }
  
  @Override
  public List<AuditLog> getLogsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    log.debug("Retrieving audit logs between {} and {}", startDateTime, endDateTime);
    validateDateRange(startDateTime, endDateTime);
    return auditLogRepository.findByTimestampRange(startDateTime, endDateTime);
  }
  
  @Override
  @Transactional
  public void logAuditEvent(AuditLog auditLog) {
    log.debug("Saving audit log: {}", auditLog);
    Objects.requireNonNull(auditLog, "Audit log must not be null");
    auditLogRepository.save(auditLog);
  }
  
  @Override
  public AuditStatistics getStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    log.debug("Generating audit statistics for period {} to {}", startDateTime, endDateTime);
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
    Objects.requireNonNull(startDateTime, "Start date-time must not be null");
    Objects.requireNonNull(endDateTime, "End date-time must not be null");
    if (startDateTime.isAfter(endDateTime)) {
      throw new AuditValidationException("Start date-time cannot be after end date-time");
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