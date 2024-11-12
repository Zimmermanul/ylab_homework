package com.mkhabibullin.audit.persistence.queries;

public final class AuditLogQueries {
  private AuditLogQueries() {}
  
  public static final String GET_BY_USERNAME =
    "SELECT a FROM AuditLog a WHERE a.username = :username";
  
  public static final String GET_BY_TIMESTAMP_RANGE =
    "SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startTimestamp AND :endTimestamp";
  
  public static final String GET_BY_OPERATION =
    "SELECT a FROM AuditLog a WHERE a.operation = :operation";
  
  public static final String GET_RECENT_LOGS =
    "SELECT a FROM AuditLog a ORDER BY a.timestamp DESC";
}