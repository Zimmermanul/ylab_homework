package com.mkhabibullin.application.service;

import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.domain.model.AuditStatistics;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing audit log operations.
 * Defines contract for audit logging functionality including retrieval, recording,
 * and analysis of audit events.
 */
public interface AuditLogService {
  /**
   * Retrieves audit logs for a specific user.
   *
   * @param username the username whose logs to retrieve
   * @return list of audit logs
   */
  List<AuditLog> getUserLogs(String username) throws IOException;
  
  /**
   * Retrieves audit logs for a specific operation.
   *
   * @param operation the operation type to query
   * @return list of audit logs
   */
  List<AuditLog> getOperationLogs(String operation) throws IOException;
  
  /**
   * Retrieves the most recent audit logs.
   *
   * @param limit maximum number of logs to retrieve
   * @return list of recent audit logs
   */
  List<AuditLog> getRecentLogs(int limit) throws IOException;
  
  /**
   * Retrieves audit logs within a specified time range.
   *
   * @param startDateTime start of the time range
   * @param endDateTime   end of the time range
   * @return list of audit logs within the range
   */
  List<AuditLog> getLogsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException;
  
  /**
   * Records a new audit log entry.
   *
   * @param auditLog the audit log entry to save
   */
  void logAuditEvent(AuditLog auditLog) throws IOException;
  
  /**
   * Generates statistics for audit logs within a specified time range.
   * The statistics include:
   * - Total number of logs
   * - Average execution time
   * - Operation counts
   * - User activity counts
   * - Average time by operation
   * - Most active user
   * - Most common operation
   *
   * @param startDateTime start of the time range
   * @param endDateTime   end of the time range
   * @return statistics object containing various metrics
   */
  AuditStatistics getStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException;
}