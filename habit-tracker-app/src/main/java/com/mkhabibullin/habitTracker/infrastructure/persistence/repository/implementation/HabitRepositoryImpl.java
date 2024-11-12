package com.mkhabibullin.habitTracker.infrastructure.persistence.repository.implementation;

import com.mkhabibullin.habitTracker.common.MessageConstants;
import com.mkhabibullin.habitTracker.domain.exception.EntityNotFoundException;
import com.mkhabibullin.habitTracker.domain.exception.RepositoryException;
import com.mkhabibullin.habitTracker.domain.model.Habit;
import com.mkhabibullin.habitTracker.infrastructure.persistence.queries.HabitRepositoryQueries;
import com.mkhabibullin.habitTracker.infrastructure.persistence.repository.HabitRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of HabitRepository interface.
 * Provides JPA-based implementation for managing habit entries using EntityManager.
 * Handles CRUD operations for habits with error handling and logging.
 *
 * @see HabitRepository
 */
@Repository
@Transactional
public class HabitRepositoryImpl implements HabitRepository {
  private static final Logger log = LoggerFactory.getLogger(HabitRepositoryImpl.class);
  private static final String ENTITY_NAME = "habit";
  
  @PersistenceContext
  private EntityManager entityManager;
  
  /**
   * Creates a new habit record in the database.
   * Performs a flush operation to ensure the habit is persisted and ID is generated.
   *
   * @param habit the habit entity to create
   * @throws RepositoryException if there is an error during creation
   */
  @Override
  public void create(Habit habit) {
    try {
      Objects.requireNonNull(habit, "Habit must not be null");
      entityManager.persist(habit);
      entityManager.flush();
    } catch (NullPointerException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error creating habit: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_SAVING, ENTITY_NAME),
        e
      );
    }
  }
  
  /**
   * Updates an existing habit record with new information.
   * If no habit is found with the given ID, a warning is logged.
   *
   * @param habit the habit entity containing updated information
   * @throws RepositoryException if there is an error during update
   */
  @Override
  public void update(Habit habit) {
    try {
      TypedQuery<Habit> query = entityManager.createQuery(
        HabitRepositoryQueries.UPDATE_HABIT,
        Habit.class
      );
      query.setParameter("name", habit.getName());
      query.setParameter("description", habit.getDescription());
      query.setParameter("frequency", habit.getFrequency());
      query.setParameter("active", habit.isActive());
      query.setParameter("id", habit.getId());
      
      int rowsAffected = query.executeUpdate();
      if (rowsAffected == 0) {
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_ID, ENTITY_NAME, habit.getId())
        );
      }
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error updating habit: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_UPDATING, ENTITY_NAME),
        e
      );
    }
  }
  
  /**
   * Deletes a habit record by its ID.
   * If no habit is found with the given ID, a warning is logged.
   *
   * @param id the unique identifier of the habit to delete
   * @throws RepositoryException if there is an error during deletion
   */
  @Override
  public void delete(Long id) {
    try {
      Habit habit = entityManager.find(Habit.class, id);
      if (habit != null) {
        entityManager.remove(habit);
      } else {
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_ID, ENTITY_NAME, id)
        );
      }
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error deleting habit: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_DELETING, ENTITY_NAME),
        e
      );
    }
  }
  
  /**
   * Retrieves all habit records from the database.
   *
   * @return a list of all habits, or an empty list if none found or if an error occurs
   */
  @Override
  public List<Habit> readAll() {
    try {
      TypedQuery<Habit> query = entityManager.createQuery(
        HabitRepositoryQueries.READ_ALL_HABITS,
        Habit.class
      );
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error reading all habits: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING, ENTITY_NAME),
        e
      );
    }
  }
  
  /**
   * Retrieves all habits associated with a specific user.
   *
   * @param userId the unique identifier of the user
   * @return a list of habits belonging to the specified user, or an empty list if none found or if an error occurs
   */
  @Override
  public List<Habit> getByUserId(Long userId) {
    try {
      TypedQuery<Habit> query = entityManager.createQuery(
        HabitRepositoryQueries.GET_HABITS_BY_USER_ID,
        Habit.class
      );
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error getting habits by user ID: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING_BY_USER, ENTITY_NAME, userId),
        e
      );
    }
  }
  
  /**
   * Retrieves a specific habit by its ID.
   *
   * @param id the unique identifier of the habit
   * @return the found habit entity, or null if not found or if an error occurs
   */
  @Override
  public Habit getById(Long id) {
    try {
      TypedQuery<Habit> query = entityManager.createQuery(
        HabitRepositoryQueries.GET_HABIT_BY_ID,
        Habit.class
      );
      query.setParameter("id", id);
      List<Habit> results = query.getResultList();
      if (results.isEmpty()) {
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_ID, ENTITY_NAME, id)
        );
      }
      return results.get(0);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error getting habit by ID: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING, ENTITY_NAME),
        e
      );
    }
  }
}