package com.mkhabibullin.infrastructure.persistence.repository;


import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.infrastructure.persistence.queries.AuditLogRepositoryQueries;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing AuditLog entities in the database.
 * Provides operations for saving and retrieving audit log entries.
 * All database operations are performed on the 'audit.audit_logs' table.
 */
public class AuditLogDbRepository {
  private final DataSource dataSource;
  
  /**
   * Constructs a new AuditLogDbRepository with the specified data source.
   *
   * @param dataSource The data source to be used for database connections
   */
  public AuditLogDbRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  
  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
  
  /**
   * Saves a new audit log entry in the database.
   * The audit log's ID will be set after successful creation.
   *
   * @param auditLog The audit log object to be persisted
   */
  public void save(AuditLog auditLog) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(AuditLogRepositoryQueries.SAVE_AUDIT_LOG)) {
      pstmt.setString(1, auditLog.getUsername());
      pstmt.setString(2, auditLog.getMethodName());
      pstmt.setString(3, auditLog.getOperation());
      pstmt.setTimestamp(4, Timestamp.valueOf(auditLog.getTimestamp()));
      pstmt.setLong(5, auditLog.getExecutionTimeMs());
      pstmt.setString(6, auditLog.getRequestUri());
      pstmt.setString(7, auditLog.getRequestMethod());
      
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          auditLog.setId(rs.getLong("id"));
        }
      }
    } catch (SQLException e) {
      System.out.println("Error saving audit log: \n " + e.getMessage());
    }
  }
  
  /**
   * Retrieves an audit log entry by its ID.
   *
   * @param id The ID of the audit log entry to retrieve
   * @return The audit log object if found, null otherwise
   */
  public AuditLog getById(Long id) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(AuditLogRepositoryQueries.GET_BY_ID)) {
      pstmt.setLong(1, id);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToAuditLog(rs);
        }
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving audit log by ID: \n " + e.getMessage());
    }
    return null;
  }
  
  /**
   * Retrieves all audit logs for a specific user.
   *
   * @param username The username whose audit logs to retrieve
   * @return List of audit logs for the specified user
   */
  public List<AuditLog> getByUsername(String username) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(AuditLogRepositoryQueries.GET_BY_USERNAME)) {
      pstmt.setString(1, username);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<AuditLog> logs = new ArrayList<>();
        while (rs.next()) {
          logs.add(mapResultSetToAuditLog(rs));
        }
        return logs;
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving audit logs by username: \n " + e.getMessage());
    }
    return new ArrayList<>();
  }
  
  /**
   * Retrieves audit logs within a specified timestamp range.
   *
   * @param startTimestamp The start of the range (inclusive)
   * @param endTimestamp   The end of the range (inclusive)
   * @return List of audit logs within the specified range
   */
  public List<AuditLog> getByTimestampRange(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(AuditLogRepositoryQueries.GET_BY_TIMESTAMP_RANGE)) {
      pstmt.setTimestamp(1, Timestamp.valueOf(startTimestamp));
      pstmt.setTimestamp(2, Timestamp.valueOf(endTimestamp));
      try (ResultSet rs = pstmt.executeQuery()) {
        List<AuditLog> logs = new ArrayList<>();
        while (rs.next()) {
          logs.add(mapResultSetToAuditLog(rs));
        }
        return logs;
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving audit logs by timestamp range: \n " + e.getMessage());
    }
    return new ArrayList<>();
  }
  
  /**
   * Retrieves audit logs by operation type.
   *
   * @param operation The operation type to search for
   * @return List of audit logs for the specified operation
   */
  public List<AuditLog> getByOperation(String operation) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(AuditLogRepositoryQueries.GET_BY_OPERATION)) {
      pstmt.setString(1, operation);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<AuditLog> logs = new ArrayList<>();
        while (rs.next()) {
          logs.add(mapResultSetToAuditLog(rs));
        }
        return logs;
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving audit logs by operation: \n " + e.getMessage());
    }
    return new ArrayList<>();
  }
  
  /**
   * Retrieves the most recent audit logs, limited by count.
   *
   * @param limit The maximum number of logs to retrieve
   * @return List of the most recent audit logs
   */
  public List<AuditLog> getRecentLogs(int limit) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(AuditLogRepositoryQueries.GET_RECENT_LOGS)) {
      pstmt.setInt(1, limit);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<AuditLog> logs = new ArrayList<>();
        while (rs.next()) {
          logs.add(mapResultSetToAuditLog(rs));
        }
        return logs;
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving recent audit logs: \n " + e.getMessage());
    }
    return new ArrayList<>();
  }
  
  private AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
    AuditLog log = new AuditLog(
      rs.getString("username"),
      rs.getString("method_name"),
      rs.getString("operation"),
      rs.getTimestamp("timestamp").toLocalDateTime(),
      rs.getLong("execution_time_ms"),
      rs.getString("request_uri"),
      rs.getString("request_method")
    );
    log.setId(rs.getLong("id"));
    return log;
  }
}