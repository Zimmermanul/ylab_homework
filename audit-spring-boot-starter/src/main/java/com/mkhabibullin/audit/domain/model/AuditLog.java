package com.mkhabibullin.audit.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents an audit log entry for tracking system operations.
 * This class encapsulates information about method executions, including user details,
 * timing information, and operation metadata.
 */
@Entity
@Table(name = "audit_logs", schema = "audit")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_seq")
  @SequenceGenerator(name = "audit_seq", sequenceName = "entity.global_seq", allocationSize = 1)
  private Long id;
  @Column(nullable = false)
  private String username;
  @Column(name = "method_name", nullable = false)
  private String methodName;
  @Column(nullable = false)
  private String operation;
  @Column(nullable = false)
  private LocalDateTime timestamp;
  @Column(name = "execution_time_ms", nullable = false)
  private Long executionTimeMs;
  @Column(name = "request_uri")
  private String requestUri;
  @Column(name = "request_method")
  private String requestMethod;
  
  /**
   * Constructs a new AuditLog with the specified details.
   *
   * @param username        The user who performed the operation
   * @param methodName      The name of the method that was executed
   * @param operation       The description of the operation performed
   * @param timestamp       When the operation occurred
   * @param executionTimeMs How long the operation took in milliseconds
   * @param requestUri      The URI of the request
   * @param requestMethod   The HTTP method used
   */
  public AuditLog(String username, String methodName, String operation,
                  LocalDateTime timestamp, Long executionTimeMs,
                  String requestUri, String requestMethod) {
    this.username = username;
    this.methodName = methodName;
    this.operation = operation;
    this.timestamp = timestamp;
    this.executionTimeMs = executionTimeMs;
    this.requestUri = requestUri;
    this.requestMethod = requestMethod;
  }
}