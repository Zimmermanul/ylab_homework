package com.mkhabibullin.infrastructure.persistence.queries;

/**
 * Contains SQL query constants used by the AuditLogDbRepository.
 * This class provides centralized storage for all SQL queries related to audit log operations.
 * It cannot be instantiated as it only serves as a container for static constants.
 */
public final class AuditLogRepositoryQueries {
  
  private AuditLogRepositoryQueries() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  /**
   * SQL query for saving a new audit log entry.
   * This query inserts a new record into the audit_logs table and returns the generated ID.
   * Required parameters:
   * 1. username (String)
   * 2. method_name (String)
   * 3. operation (String)
   * 4. timestamp (Timestamp)
   * 5. execution_time_ms (Long)
   * 6. request_uri (String)
   * 7. request_method (String)
   */
  public static final String SAVE_AUDIT_LOG =
    "INSERT INTO audit.audit_logs (username, method_name, operation, timestamp, " +
    "execution_time_ms, request_uri, request_method) " +
    "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
  
  /**
   * SQL query for retrieving audit logs by username.
   * This query selects all audit records for a given username, ordered by timestamp.
   * Required parameters:
   * 1. username (String)
   */
  public static final String GET_BY_USERNAME =
    "SELECT * FROM audit.audit_logs WHERE username = ? ORDER BY timestamp DESC";
  
  /**
   * SQL query for retrieving a specific audit log by its ID.
   * This query selects a single audit record matching the provided ID.
   * Required parameters:
   * 1. id (Long)
   */
  public static final String GET_BY_ID =
    "SELECT * FROM audit.audit_logs WHERE id = ?";
  
  /**
   * SQL query for retrieving audit logs within a timestamp range.
   * This query selects all audit records between two timestamps.
   * Required parameters:
   * 1. start_timestamp (Timestamp)
   * 2. end_timestamp (Timestamp)
   */
  public static final String GET_BY_TIMESTAMP_RANGE =
    "SELECT * FROM audit.audit_logs " +
    "WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
  
  /**
   * SQL query for retrieving audit logs by operation type.
   * This query selects all audit records for a specific operation.
   * Required parameters:
   * 1. operation (String)
   */
  public static final String GET_BY_OPERATION =
    "SELECT * FROM audit.audit_logs WHERE operation = ? ORDER BY timestamp DESC";
  
  /**
   * SQL query for retrieving audit logs by method name.
   * This query selects all audit records for a specific method.
   * Required parameters:
   * 1. method_name (String)
   */
  public static final String GET_BY_METHOD_NAME =
    "SELECT * FROM audit.audit_logs WHERE method_name = ? ORDER BY timestamp DESC";
  
  /**
   * SQL query for retrieving recent audit logs.
   * This query selects the most recent audit records, limited by the specified count.
   * Required parameters:
   * 1. limit (Integer)
   */
  public static final String GET_RECENT_LOGS =
    "SELECT * FROM audit.audit_logs ORDER BY timestamp DESC LIMIT ?";
}