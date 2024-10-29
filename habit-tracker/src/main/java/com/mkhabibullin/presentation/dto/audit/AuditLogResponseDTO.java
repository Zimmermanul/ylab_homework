package com.mkhabibullin.presentation.dto.audit;

import java.time.LocalDateTime;

/**
 * DTO for audit log response data.
 * Contains all relevant information about an audit log entry, including request details,
 * timing information, and operation metadata.
 *
 * @param id Unique identifier for the audit log entry
 * @param username Name of the user who performed the operation
 * @param methodName Name of the method that was executed
 * @param operation Type of operation performed (e.g., CREATE, UPDATE, DELETE)
 * @param timestamp Date and time when the operation was executed
 * @param executionTimeMs Time taken to execute the operation in milliseconds
 * @param requestUri The URI of the HTTP request
 * @param requestMethod The HTTP method used (GET, POST, PUT, DELETE, etc.)
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

