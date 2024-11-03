package com.mkhabibullin.infrastructure.persistence.repository.implementation;

import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.infrastructure.persistence.queries.HabitRepositoryQueries;
import com.mkhabibullin.infrastructure.persistence.repository.HabitRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
  
  @PersistenceContext
  private EntityManager entityManager;
  
  /**
   * Creates a new habit record in the database.
   * Performs a flush operation to ensure the habit is persisted and ID is generated.
   *
   * @param habit the habit entity to create
   * @throws RuntimeException if there is an error during creation
   */
  @Override
  public void create(Habit habit) {
    try {
      entityManager.persist(habit);
      entityManager.flush();
    } catch (Exception e) {
      log.error("Error creating habit: ", e);
      throw new RuntimeException("Error creating habit", e);
    }
  }
  
  /**
   * Updates an existing habit record with new information.
   * If no habit is found with the given ID, a warning is logged.
   *
   * @param habit the habit entity containing updated information
   * @throws RuntimeException if there is an error during update
   */
  @Override
  public void update(Habit habit) {
    try {
      TypedQuery<Habit> query = entityManager.createQuery(HabitRepositoryQueries.UPDATE_HABIT, Habit.class);
      query.setParameter("name", habit.getName());
      query.setParameter("description", habit.getDescription());
      query.setParameter("frequency", habit.getFrequency());
      query.setParameter("active", habit.isActive());
      query.setParameter("id", habit.getId());
      
      int rowsAffected = query.executeUpdate();
      if (rowsAffected == 0) {
        log.warn("No habit found with ID: {}", habit.getId());
      }
    } catch (Exception e) {
      log.error("Error updating habit: ", e);
      throw new RuntimeException("Error updating habit", e);
    }
  }
  
  /**
   * Deletes a habit record by its ID.
   * If no habit is found with the given ID, a warning is logged.
   *
   * @param id the unique identifier of the habit to delete
   * @throws RuntimeException if there is an error during deletion
   */
  @Override
  public void delete(Long id) {
    try {
      Habit habit = entityManager.find(Habit.class, id);
      if (habit != null) {
        entityManager.remove(habit);
      } else {
        log.warn("No habit found with ID: {}", id);
      }
    } catch (Exception e) {
      log.error("Error deleting habit: ", e);
      throw new RuntimeException("Error deleting habit", e);
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
      return new ArrayList<>();
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
      return new ArrayList<>();
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
      return results.isEmpty() ? null : results.get(0);
    } catch (Exception e) {
      log.error("Error getting habit by ID: ", e);
      return null;
    }
  }
}