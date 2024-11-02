package com.mkhabibullin.infrastructure.persistence.repository.implementation;


import com.mkhabibullin.domain.model.HabitExecution;
import com.mkhabibullin.infrastructure.persistence.queries.HabitExecutionRepositoryQueries;
import com.mkhabibullin.infrastructure.persistence.repository.HabitExecutionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of HabitExecutionRepository interface.
 * Provides JPA-based implementation for managing habit execution entries.
 */
@Repository
@Transactional
public class HabitExecutionRepositoryImpl implements HabitExecutionRepository {
  private static final Logger log = LoggerFactory.getLogger(HabitExecutionRepositoryImpl.class);
  
  @PersistenceContext
  private EntityManager entityManager;
  
  @Override
  public void save(HabitExecution execution) {
    try {
      entityManager.persist(execution);
      entityManager.flush(); // to get the generated ID
    } catch (Exception e) {
      log.error("Error saving habit execution: ", e);
      throw new RuntimeException("Error saving habit execution", e);
    }
  }
  
  @Override
  public List<HabitExecution> getByHabitId(Long habitId) {
    try {
      TypedQuery<HabitExecution> query = entityManager.createQuery(
        HabitExecutionRepositoryQueries.GET_BY_HABIT_ID,
        HabitExecution.class
      );
      query.setParameter("habitId", habitId);
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error retrieving habit executions: ", e);
      return new ArrayList<>();
    }
  }
  
  @Override
  public void update(HabitExecution execution) {
    try {
      TypedQuery<HabitExecution> query = entityManager.createQuery(
        HabitExecutionRepositoryQueries.UPDATE_EXECUTION,
        HabitExecution.class
      );
      query.setParameter("date", execution.getDate());
      query.setParameter("completed", execution.isCompleted());
      query.setParameter("id", execution.getId());
      
      int rowsAffected = query.executeUpdate();
      if (rowsAffected == 0) {
        log.warn("Habit execution not found with ID: {}", execution.getId());
      }
    } catch (Exception e) {
      log.error("Error updating habit execution: ", e);
      throw new RuntimeException("Error updating habit execution", e);
    }
  }
  
  @Override
  public void delete(Long executionId) {
    try {
      HabitExecution execution = entityManager.find(HabitExecution.class, executionId);
      if (execution != null) {
        entityManager.remove(execution);
      } else {
        log.warn("Habit execution not found with ID: {}", executionId);
      }
    } catch (Exception e) {
      log.error("Error deleting habit execution: ", e);
      throw new RuntimeException("Error deleting habit execution", e);
    }
  }
  
  @Override
  public HabitExecution getById(Long id) {
    try {
      TypedQuery<HabitExecution> query = entityManager.createQuery(
        HabitExecutionRepositoryQueries.GET_BY_ID,
        HabitExecution.class
      );
      query.setParameter("id", id);
      List<HabitExecution> results = query.getResultList();
      return results.isEmpty() ? null : results.get(0);
    } catch (Exception e) {
      log.error("Error getting habit execution by ID: ", e);
      return null;
    }
  }
  
  @Override
  public List<HabitExecution> getByHabitAndDateRange(Long habitId, LocalDate startDate, LocalDate endDate) {
    try {
      TypedQuery<HabitExecution> query = entityManager.createQuery(
        HabitExecutionRepositoryQueries.GET_BY_HABIT_AND_DATE_RANGE,
        HabitExecution.class
      );
      query.setParameter("habitId", habitId);
      query.setParameter("startDate", startDate);
      query.setParameter("endDate", endDate);
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error retrieving habit executions by date range: ", e);
      return new ArrayList<>();
    }
  }
}