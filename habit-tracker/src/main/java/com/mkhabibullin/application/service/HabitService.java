package com.mkhabibullin.application.service;

import com.mkhabibullin.domain.model.Habit;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing habits in a habit tracking application.
 * This interface defines the contract for creating, editing, deleting, and viewing habits.
 * It is designed to work with user data and habit persistence layers.
 */
public interface HabitService {
  /**
   * Creates a new habit for a specified user.
   * The habit is persisted in the database and associated with the user identified by the email.
   *
   * @param userEmail   the email of the user creating the habit
   * @param name        the name of the habit
   * @param description the description of the habit
   * @param frequency   the frequency at which the habit should be performed
   */
  void createHabit(String userEmail, String name, String description, Habit.Frequency frequency);
  
  /**
   * Updates an existing habit with new information.
   * All fields of the habit will be updated to the provided values.
   *
   * @param id          the unique identifier of the habit to edit
   * @param name        the new name for the habit
   * @param description the new description for the habit
   * @param frequency   the new frequency for the habit
   */
  void editHabit(String id, String name, String description, Habit.Frequency frequency);
  
  /**
   * Deletes a habit from the system.
   * Once deleted, the habit cannot be recovered.
   *
   * @param id the unique identifier of the habit to delete
   */
  void deleteHabit(Long id);
  
  /**
   * Retrieves a filtered list of habits for a specific user.
   * The results can be filtered by creation date and active status.
   *
   * @param userId     the unique identifier of the user whose habits to retrieve
   * @param filterDate optional date filter - if provided, only habits created on or after this date will be included
   * @param active     optional status filter - if provided, only habits matching this active status will be included
   * @return a List of habits matching the specified criteria, empty list if no habits are found
   */
  List<Habit> viewHabits(Long userId, LocalDate filterDate, Boolean active);
}