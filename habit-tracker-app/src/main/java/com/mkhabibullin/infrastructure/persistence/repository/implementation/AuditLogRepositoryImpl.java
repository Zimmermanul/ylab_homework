package com.mkhabibullin.infrastructure.persistence.repository.implementation;

import com.mkhabibullin.common.MessageConstants;
import com.mkhabibullin.domain.exception.EntityNotFoundException;
import com.mkhabibullin.domain.exception.RepositoryException;
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
  private static final String ENTITY_NAME = "audit log";
  
  @PersistenceContext
  private EntityManager entityManager;
  
  /**
   * Persists a new audit log entry to the database.
   *
   * @param auditLog the audit log entry to save
   * @throws RepositoryException     if there is an error during persistence
   */
  @Override
  public void save(AuditLog auditLog) {
    try {
      Objects.requireNonNull(auditLog, MessageConstants.AUDIT_LOG_REQUIRED);
      entityManager.persist(auditLog);
    } catch (NullPointerException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error saving audit log: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_SAVING, ENTITY_NAME),
        e
      );
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
      AuditLog auditLog = entityManager.find(AuditLog.class, id);
      if (auditLog == null) {
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_ID, ENTITY_NAME, id)
        );
      }
      return auditLog;
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error retrieving audit log by ID: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING, ENTITY_NAME),
        e
      );
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
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING_BY_USERNAME, ENTITY_NAME, username),
        e
      );
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
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING_BY_DATE, ENTITY_NAME),
        e
      );
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
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING_BY_OPERATION, ENTITY_NAME, operation),
        e
      );
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
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING_RECENT, ENTITY_NAME),
        e
      );
    }
  }
}