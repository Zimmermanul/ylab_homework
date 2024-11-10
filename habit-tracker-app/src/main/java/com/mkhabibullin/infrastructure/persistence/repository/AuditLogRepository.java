package com.mkhabibullin.infrastructure.persistence.repository;

import com.mkhabibullin.domain.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AuditLog entities.
 */
public interface AuditLogRepository {
  
  /**
   * Saves a new audit log entry.
   *
   * @param auditLog the audit log to save
   */
  void save(AuditLog auditLog);
  
  /**
   * Finds an audit log by its ID.
   *
   * @param id the ID to search for
   * @return the found audit log or null
   */
  AuditLog findById(Long id);
  
  /**
   * Finds audit logs by username.
   *
   * @param username the username to search for
   * @return list of matching audit logs
   */
  List<AuditLog> findByUsername(String username);
  
  /**
   * Finds audit logs within a timestamp range.
   *
   * @param startTimestamp start of the range
   * @param endTimestamp   end of the range
   * @return list of matching audit logs
   */
  List<AuditLog> findByTimestampRange(LocalDateTime startTimestamp, LocalDateTime endTimestamp);
  
  /**
   * Finds audit logs by operation.
   *
   * @param operation the operation to search for
   * @return list of matching audit logs
   */
  List<AuditLog> findByOperation(String operation);
  
  /**
   * Finds the most recent audit logs.
   *
   * @param limit maximum number of logs to retrieve
   * @return list of recent audit logs
   */
  List<AuditLog> findRecentLogs(int limit);
}