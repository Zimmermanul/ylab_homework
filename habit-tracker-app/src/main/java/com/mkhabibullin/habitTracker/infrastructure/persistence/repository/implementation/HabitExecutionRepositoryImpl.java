package com.mkhabibullin.habitTracker.infrastructure.persistence.repository.implementation;


import com.mkhabibullin.habitTracker.common.MessageConstants;
import com.mkhabibullin.habitTracker.domain.exception.EntityNotFoundException;
import com.mkhabibullin.habitTracker.domain.exception.RepositoryException;
import com.mkhabibullin.habitTracker.domain.model.HabitExecution;
import com.mkhabibullin.habitTracker.infrastructure.persistence.queries.HabitExecutionRepositoryQueries;
import com.mkhabibullin.habitTracker.infrastructure.persistence.repository.HabitExecutionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
@Slf4j
public class HabitExecutionRepositoryImpl implements HabitExecutionRepository {
  
  private static final String ENTITY_NAME = "habit execution";
  
  @PersistenceContext
  private EntityManager entityManager;
  
  /**
   * Persists a new habit execution record to the database.
   * Performs a flush operation to retrieve the generated ID.
   *
   * @param execution the habit execution record to save
   * @throws RepositoryException if there is an error during persistence
   */
  @Override
  public void save(HabitExecution execution) {
    try {
      entityManager.persist(execution);
      entityManager.flush();
    } catch (Exception e) {
      log.error("Error saving habit execution: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_SAVING, ENTITY_NAME),
        e
      );
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
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING, ENTITY_NAME),
        e
      );
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
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_ID, ENTITY_NAME, execution.getId())
        );
      }
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error updating habit execution: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_UPDATING, ENTITY_NAME),
        e
      );
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
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_ID, ENTITY_NAME, executionId)
        );
      }
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error deleting habit execution: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_DELETING, ENTITY_NAME),
        e
      );
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
      if (results.isEmpty()) {
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_ID, ENTITY_NAME, id)
        );
      }
      return results.get(0);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error getting habit execution by ID: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING, ENTITY_NAME),
        e
      );
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
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING_BY_DATE, ENTITY_NAME),
        e
      );
    }
  }
}