package com.mkhabibullin.presentation.controller;

import com.mkhabibullin.application.service.HabitService;
import com.mkhabibullin.domain.model.Habit;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for managing habits in a habit tracking application.
 * This class acts as an intermediary between the user interface and the business logic,
 * delegating operations to the HabitService.
 */
public class HabitController {
  private HabitService habitService;
  
  /**
   * Constructs a new HabitController with the specified HabitService.
   *
   * @param habitService the service to handle habit-related operations
   */
  public HabitController(HabitService habitService) {
    this.habitService = habitService;
  }
  
  /**
   * Creates a new habit for a user.
   *
   * @param userEmail   the email of the user creating the habit
   * @param name        the name of the habit
   * @param description the description of the habit
   * @param frequency   the frequency of the habit
   * @throws IOException if there's an error during the habit creation process
   */
  public void createHabit(String userEmail, String name, String description, Habit.Frequency frequency) {
    habitService.createHabit(userEmail, name, description, frequency);
  }
  
  /**
   * Edits an existing habit.
   *
   * @param id          the unique identifier of the habit to edit
   * @param name        the new name of the habit
   * @param description the new description of the habit
   * @param frequency   the new frequency of the habit
   * @throws IOException if there's an error during the habit editing process
   */
  public void editHabit(String id, String name, String description, Habit.Frequency frequency) {
    habitService.editHabit(id, name, description, frequency);
  }
  
  /**
   * Deletes a habit.
   *
   * @param id the unique identifier of the habit to delete
   * @throws IOException if there's an error during the habit deletion process
   */
  public void deleteHabit(Long id) throws IOException {
    habitService.deleteHabit(id);
  }
  
  /**
   * Retrieves a list of habits for a user, optionally filtered by date and active status.
   *
   * @param userId     the unique identifier of the user whose habits to retrieve
   * @param filterDate the date to filter habits by (can be null for no date filter)
   * @param active     whether to retrieve only active habits (can be null for all habits)
   * @return a list of habits matching the specified criteria
   * @throws IOException if there's an error during the habit retrieval process
   */
  public List<Habit> viewHabits(Long userId, LocalDate filterDate, Boolean active) {
    return habitService.viewHabits(userId, filterDate, active);
  }
}
