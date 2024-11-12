package com.mkhabibullin.audit.persistence.repository;

import com.mkhabibullin.audit.domain.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AuditLog entities.
 */
public interface AuditLogRepository {
  void save(AuditLog auditLog);
  
  AuditLog findById(Long id);
  
  List<AuditLog> findByUsername(String username);
  
  List<AuditLog> findByTimestampRange(LocalDateTime startTimestamp, LocalDateTime endTimestamp);
  
  List<AuditLog> findByOperation(String operation);
  
  List<AuditLog> findRecentLogs(int limit);
}