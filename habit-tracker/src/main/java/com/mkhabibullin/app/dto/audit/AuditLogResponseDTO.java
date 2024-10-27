package com.mkhabibullin.app.dto.audit;

import java.time.LocalDateTime;

/**
 * DTO for audit log response data.
 * Contains all relevant information about an audit log entry.
 */
public record AuditLogResponseDTO(
  Long id,
  String username,
  String methodName,
  String operation,
  LocalDateTime timestamp,
  Long executionTimeMs,
  String requestUri,
  String requestMethod
) {
}

