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
 * Provides JPA-based implementation for managing habit execution entries using EntityManager.
 * Handles CRUD operations for habit execution records with error handling and logging.
 *
 * @see HabitExecutionRepository
 */
@Repository
@Transactional
public class HabitExecutionRepositoryImpl implements HabitExecutionRepository {
  private static final Logger log = LoggerFactory.getLogger(HabitExecutionRepositoryImpl.class);
  
  @PersistenceContext
  private EntityManager entityManager;
  
  /**
   * Persists a new habit execution record to the database.
   * Performs a flush operation to retrieve the generated ID.
   *
   * @param execution the habit execution record to save
   * @throws RuntimeException if there is an error during persistence
   */
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
  
  /**
   * Retrieves all execution records for a specific habit.
   *
   * @param habitId the unique identifier of the habit
   * @return a list of habit execution records, or an empty list if none found or if an error occurs
   */
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
  
  /**
   * Updates an existing habit execution record.
   * If no record is found with the given ID, a warning is logged.
   *
   * @param execution the habit execution record with updated values
   * @throws RuntimeException if there is an error during update
   */
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
  
  /**
   * Deletes a habit execution record by its ID.
   * If no record is found with the given ID, a warning is logged.
   *
   * @param executionId the unique identifier of the execution record to delete
   * @throws RuntimeException if there is an error during deletion
   */
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
  
  /**
   * Retrieves a habit execution record by its ID.
   *
   * @param id the unique identifier of the execution record
   * @return the found habit execution record, or null if not found or if an error occurs
   */
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
  
  /**
   * Retrieves all habit execution records for a specific habit within a date range.
   *
   * @param habitId   the unique identifier of the habit
   * @param startDate the start date of the range (inclusive)
   * @param endDate   the end date of the range (inclusive)
   * @return a list of habit execution records within the specified date range, or an empty list if none found or if an error occurs
   */
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