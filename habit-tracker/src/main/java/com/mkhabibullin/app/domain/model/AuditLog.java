package com.mkhabibullin.app.domain.model;

import java.time.LocalDateTime;

/**
 * Represents an audit log entry for tracking system operations.
 * This class encapsulates information about method executions, including user details,
 * timing information, and operation metadata.
 */
public class AuditLog {
  private Long id;
  private String username;
  private String methodName;
  private String operation;
  private LocalDateTime timestamp;
  private Long executionTimeMs;
  private String requestUri;
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
  
  /**
   * Gets the unique identifier of this audit log entry.
   *
   * @return the audit log's ID
   */
  public Long getId() {
    return id;
  }
  
  /**
   * Sets the unique identifier for this audit log entry.
   *
   * @param id the ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * Gets the username of the user who performed the operation.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }
  
  /**
   * Sets the username of the user who performed the operation.
   *
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }
  
  /**
   * Gets the name of the method that was executed.
   *
   * @return the method name
   */
  public String getMethodName() {
    return methodName;
  }
  
  /**
   * Sets the name of the method that was executed.
   *
   * @param methodName the method name to set
   */
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }
  
  /**
   * Gets the description of the operation that was performed.
   *
   * @return the operation description
   */
  public String getOperation() {
    return operation;
  }
  
  /**
   * Sets the description of the operation that was performed.
   *
   * @param operation the operation description to set
   */
  public void setOperation(String operation) {
    this.operation = operation;
  }
  
  /**
   * Gets the timestamp when the operation was performed.
   *
   * @return the timestamp
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }
  
  /**
   * Sets the timestamp when the operation was performed.
   *
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
  
  /**
   * Gets the execution time of the operation in milliseconds.
   *
   * @return the execution time in milliseconds
   */
  public Long getExecutionTimeMs() {
    return executionTimeMs;
  }
  
  /**
   * Sets the execution time of the operation in milliseconds.
   *
   * @param executionTimeMs the execution time to set
   */
  public void setExecutionTimeMs(Long executionTimeMs) {
    this.executionTimeMs = executionTimeMs;
  }
  
  /**
   * Gets the URI of the request that triggered the operation.
   *
   * @return the request URI
   */
  public String getRequestUri() {
    return requestUri;
  }
  
  /**
   * Sets the URI of the request that triggered the operation.
   *
   * @param requestUri the request URI to set
   */
  public void setRequestUri(String requestUri) {
    this.requestUri = requestUri;
  }
  
  /**
   * Gets the HTTP method of the request.
   *
   * @return the HTTP method
   */
  public String getRequestMethod() {
    return requestMethod;
  }
  
  /**
   * Sets the HTTP method of the request.
   *
   * @param requestMethod the HTTP method to set
   */
  public void setRequestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
  }
}