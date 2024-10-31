package com.mkhabibullin.presentation.controller;

import com.mkhabibullin.application.service.AuditLogService;
import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.domain.model.AuditStatistics;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing audit log operations and generating statistics.
 * Provides methods for retrieving and analyzing audit log data.
 */
public class AuditLogController {
  private final AuditLogService auditLogService;
  
  /**
   * Constructs a new AuditLogController with the specified service.
   *
   * @param auditLogService the service to handle audit-related operations
   */
  public AuditLogController(AuditLogService auditLogService) {
    this.auditLogService = auditLogService;
  }
  
  /**
   * Retrieves audit logs for a specific user.
   *
   * @param username the username whose logs to retrieve
   * @return list of audit logs
   * @throws IOException if there's an error during retrieval
   */
  public List<AuditLog> getUserLogs(String username) throws IOException {
    return auditLogService.getUserLogs(username);
  }
  
  /**
   * Retrieves audit logs for a specific operation.
   *
   * @param operation the operation type to query
   * @return list of audit logs
   * @throws IOException if there's an error during retrieval
   */
  public List<AuditLog> getOperationLogs(String operation) throws IOException {
    return auditLogService.getOperationLogs(operation);
  }
  
  /**
   * Retrieves the most recent audit logs.
   *
   * @param limit maximum number of logs to retrieve
   * @return list of recent audit logs
   * @throws IOException if there's an error during retrieval
   */
  public List<AuditLog> getRecentLogs(int limit) throws IOException {
    return auditLogService.getRecentLogs(limit);
  }
  
  /**
   * Retrieves audit logs within a specified time range.
   *
   * @param startDateTime start of the time range
   * @param endDateTime end of the time range
   * @return list of audit logs within the range
   * @throws IOException if there's an error during retrieval
   */
  public List<AuditLog> getLogsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
    return auditLogService.getLogsByDateRange(startDateTime, endDateTime);
  }
  
  /**
   * Retrieves statistics for audit logs within a specified time range.
   *
   * @param startDateTime start of the time range
   * @param endDateTime end of the time range
   * @return statistics object containing various metrics
   * @throws IOException if there's an error during retrieval
   */
  public AuditStatistics getStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
    return auditLogService.getStatistics(startDateTime, endDateTime);
  }
}