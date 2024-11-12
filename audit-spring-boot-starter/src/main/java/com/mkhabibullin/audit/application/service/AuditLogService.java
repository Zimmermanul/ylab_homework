package com.mkhabibullin.audit.application.service;


import com.mkhabibullin.audit.domain.model.AuditLog;
import com.mkhabibullin.audit.domain.model.AuditStatistics;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing audit log operations.
 * Defines contract for audit logging functionality including retrieval, recording,
 * and analysis of audit events.
 */
@Validated
public interface AuditLogService {
  /**
   * Retrieves audit logs for a specific user.
   *
   * @param username the username whose logs to retrieve
   * @return list of audit logs
   */
  List<AuditLog> getUserLogs(@NotBlank String username);
  
  /**
   * Retrieves audit logs for a specific operation.
   *
   * @param operation the operation type to query
   * @return list of audit logs
   */
  List<AuditLog> getOperationLogs(@NotBlank String operation);
  
  /**
   * Retrieves the most recent audit logs.
   *
   * @param limit maximum number of logs to retrieve
   * @return list of recent audit logs
   */
  List<AuditLog> getRecentLogs(@Min(1) int limit);
  
  /**
   * Retrieves audit logs within a specified time range.
   *
   * @param startDateTime start of the time range
   * @param endDateTime   end of the time range
   * @return list of audit logs within the range
   */
  List<AuditLog> getLogsByDateRange(
    @NotNull LocalDateTime startDateTime,
    @NotNull LocalDateTime endDateTime
  );
  
  /**
   * Records a new audit log entry.
   *
   * @param auditLog the audit log entry to save
   */
  void logAuditEvent(@NotNull @Valid AuditLog auditLog);
  
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
  AuditStatistics getStatistics(
    @NotNull LocalDateTime startDateTime,
    @NotNull LocalDateTime endDateTime
  );
}