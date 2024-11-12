package com.mkhabibullin.audit.persistence.repository;

import com.mkhabibullin.audit.domain.model.AuditLog;
import com.mkhabibullin.audit.persistence.queries.AuditLogQueries;
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

@Repository
@Transactional
public class AuditLogRepositoryImpl implements AuditLogRepository {
  private static final Logger log = LoggerFactory.getLogger(AuditLogRepositoryImpl.class);
  private static final String ENTITY_NAME = "audit log";
  
  @PersistenceContext
  private EntityManager entityManager;
  
  @Override
  public void save(AuditLog auditLog) {
    try {
      Objects.requireNonNull(auditLog, "Audit log cannot be null");
      entityManager.persist(auditLog);
      log.debug("Saved audit log: {}", auditLog);
    } catch (NullPointerException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error saving audit log: ", e);
      throw new RuntimeException("Error saving audit log", e);
    }
  }
  
  @Override
  public AuditLog findById(Long id) {
    try {
      AuditLog auditLog = entityManager.find(AuditLog.class, id);
      if (auditLog == null) {
        throw new RuntimeException(String.format("Audit log with id %d not found", id));
      }
      return auditLog;
    } catch (Exception e) {
      log.error("Error finding audit log by id: ", e);
      throw new RuntimeException("Error retrieving audit log", e);
    }
  }
  
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
      log.error("Error finding audit logs by username: ", e);
      throw new RuntimeException("Error retrieving audit logs by username", e);
    }
  }
  
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
      log.error("Error finding audit logs by timestamp range: ", e);
      throw new RuntimeException("Error retrieving audit logs by timestamp range", e);
    }
  }
  
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
      log.error("Error finding audit logs by operation: ", e);
      throw new RuntimeException("Error retrieving audit logs by operation", e);
    }
  }
  
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
      log.error("Error finding recent audit logs: ", e);
      throw new RuntimeException("Error retrieving recent audit logs", e);
    }
  }
}