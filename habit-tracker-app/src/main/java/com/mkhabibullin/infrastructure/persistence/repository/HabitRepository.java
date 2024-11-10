package com.mkhabibullin.infrastructure.persistence.repository;

import com.mkhabibullin.domain.model.Habit;

import java.util.List;

/**
 * Repository interface for Habit entities.
 * Defines CRUD operations for habits management.
 */
public interface HabitRepository {
  
  /**
   * Creates a new habit record.
   *
   * @param habit The habit to create
   */
  void create(Habit habit);
  
  /**
   * Updates an existing habit record.
   *
   * @param habit The habit to update
   */
  void update(Habit habit);
  
  /**
   * Deletes a habit record.
   *
   * @param id The ID of the habit to delete
   */
  void delete(Long id);
  
  /**
   * Retrieves all habit records.
   *
   * @return List of all habits
   */
  List<Habit> readAll();
  
  /**
   * Retrieves all habits for a specific user.
   *
   * @param userId The ID of the user
   * @return List of habits belonging to the user
   */
  List<Habit> getByUserId(Long userId);
  
  /**
   * Retrieves a specific habit by its ID.
   *
   * @param id The ID of the habit to retrieve
   * @return The habit if found, null otherwise
   */
  Habit getById(Long id);
}