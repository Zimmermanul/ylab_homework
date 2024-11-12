package com.mkhabibullin.audit.persistence.queries;

/**
 * Contains JPQL query constants used by the AuditLogRepository.
 * This class provides centralized storage for all JPQL queries related to audit log operations.
 */
public final class AuditLogQueries {
  private AuditLogQueries() {}
  
  /**
   * JPQL query for retrieving audit logs by username.
   */
  public static final String GET_BY_USERNAME =
    "SELECT a FROM AuditLog a WHERE a.username = :username";
  
  /**
   * JPQL query for retrieving audit logs within a timestamp range.
   */
  public static final String GET_BY_TIMESTAMP_RANGE =
    "SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startTimestamp AND :endTimestamp";
  
  /**
   * JPQL query for retrieving audit logs by operation.
   */
  public static final String GET_BY_OPERATION =
    "SELECT a FROM AuditLog a WHERE a.operation = :operation";
  
  /**
   * JPQL query for retrieving recent audit logs.
   */
  public static final String GET_RECENT_LOGS =
    "SELECT a FROM AuditLog a ORDER BY a.timestamp DESC";
}