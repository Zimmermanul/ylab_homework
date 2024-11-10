package com.mkhabibullin.presentation.dto.audit;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Audit log entry details")
public record AuditLogResponseDTO(
  @Schema(description = "Unique identifier of the audit log entry")
  Long id,
  
  @Schema(description = "Username who performed the action", example = "john.doe")
  String username,
  
  @Schema(description = "Name of the method that was executed", example = "createHabit")
  String methodName,
  
  @Schema(description = "Type of operation performed", example = "Create Habit")
  String operation,
  
  @Schema(description = "Timestamp when the operation was performed", example = "2024-03-15T14:30:00")
  LocalDateTime timestamp,
  
  @Schema(description = "Execution time in milliseconds", example = "150")
  Long executionTimeMs,
  
  @Schema(description = "URI of the request", example = "/api/habits")
  String requestUri,
  
  @Schema(description = "HTTP method of the request", example = "POST")
  String requestMethod
) {
}
