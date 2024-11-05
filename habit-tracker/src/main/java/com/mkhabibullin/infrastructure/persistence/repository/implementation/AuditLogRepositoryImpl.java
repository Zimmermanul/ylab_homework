package com.mkhabibullin.infrastructure.persistence.repository.implementation;

import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.infrastructure.persistence.queries.AuditLogQueries;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of AuditLogRepository interface.
 * Provides JPA-based implementation for managing audit log entries using EntityManager.
 * This implementation includes error handling and logging for all database operations.
 *
 * @see AuditLogRepository
 */
@Repository
@Transactional
public class AuditLogRepositoryImpl implements AuditLogRepository {
  private static final Logger log = LoggerFactory.getLogger(AuditLogRepositoryImpl.class);
  
  @PersistenceContext
  private EntityManager entityManager;
  
  /**
   * Persists a new audit log entry to the database.
   *
   * @param auditLog the audit log entry to save
   * @throws NullPointerException if auditLog is null
   * @throws RuntimeException     if there is an error during persistence
   */
  @Override
  public void save(AuditLog auditLog) {
    try {
      Objects.requireNonNull(auditLog, "auditLog must not be null");
      entityManager.persist(auditLog);
    } catch (Exception e) {
      log.error("Error saving audit log: ", e);
      throw new RuntimeException("Error saving audit log", e);
    }
  }
  
  /**
   * Retrieves an audit log entry by its ID.
   *
   * @param id the unique identifier of the audit log entry
   * @return the found AuditLog entity, or null if not found or if an error occurs
   */
  @Override
  public AuditLog findById(Long id) {
    try {
      return entityManager.find(AuditLog.class, id);
    } catch (Exception e) {
      log.error("Error retrieving audit log by ID: ", e);
      return null;
    }
  }
  
  /**
   * Retrieves all audit log entries for a specific username.
   *
   * @param username the username to search for
   * @return a list of matching AuditLog entries, or an empty list if none found or if an error occurs
   */
  @Override
  public List<AuditLog> findByUsername(String username) {
    try {
      TypedQuery<AuditLog> query = entityManager.createQuery(
        AuditLogQueries.GET_BY_USERNAME,
        AuditLog.class
      );
      query.setParameter("username", username);
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error retrieving audit logs by username: ", e);
      return new ArrayList<>();
    }
  }
  
  /**
   * Retrieves audit log entries within a specified time range.
   *
   * @param startTimestamp the start of the time range (inclusive)
   * @param endTimestamp the end of the time range (inclusive)
   * @return a list of AuditLog entries within the specified range, or an empty list if none found or if an error occurs
   */
  @Override
  public List<AuditLog> findByTimestampRange(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
    try {
      TypedQuery<AuditLog> query = entityManager.createQuery(
        AuditLogQueries.GET_BY_TIMESTAMP_RANGE,
        AuditLog.class
      );
      query.setParameter("startTimestamp", startTimestamp);
      query.setParameter("endTimestamp", endTimestamp);
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error retrieving audit logs by timestamp range: ", e);
      return new ArrayList<>();
    }
  }
  
  /**
   * Retrieves all audit log entries for a specific operation type.
   *
   * @param operation the operation type to search for
   * @return a list of matching AuditLog entries, or an empty list if none found or if an error occurs
   */
  @Override
  public List<AuditLog> findByOperation(String operation) {
    try {
      TypedQuery<AuditLog> query = entityManager.createQuery(
        AuditLogQueries.GET_BY_OPERATION,
        AuditLog.class
      );
      query.setParameter("operation", operation);
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error retrieving audit logs by operation: ", e);
      return new ArrayList<>();
    }
  }
  
  /**
   * Retrieves the most recent audit log entries, limited to a specified number.
   * Results are typically ordered by timestamp in descending order.
   *
   * @param limit the maximum number of entries to retrieve
   * @return a list of the most recent AuditLog entries, or an empty list if none found or if an error occurs
   */
  @Override
  public List<AuditLog> findRecentLogs(int limit) {
    try {
      TypedQuery<AuditLog> query = entityManager.createQuery(
        AuditLogQueries.GET_RECENT_LOGS,
        AuditLog.class
      );
      query.setMaxResults(limit);
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error retrieving recent audit logs: ", e);
      return new ArrayList<>();
    }
  }
}