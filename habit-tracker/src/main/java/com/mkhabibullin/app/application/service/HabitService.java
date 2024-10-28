package com.mkhabibullin.app.application.service;

import com.mkhabibullin.app.domain.model.Habit;
import com.mkhabibullin.app.domain.model.User;
import com.mkhabibullin.app.infrastructure.persistence.repository.HabitDbRepository;
import com.mkhabibullin.app.infrastructure.persistence.repository.UserDbRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing habits in a habit tracking application.
 * This class provides methods for creating, editing, deleting, and viewing habits.
 * It interacts with HabitRepository for habit data persistence and UserRepository for user information.
 */
public class HabitService {
  private HabitDbRepository habitRepository;
  private UserDbRepository userRepository;
  
  /**
   * Constructs a new HabitService with the specified repositories.
   *
   * @param habitRepository the repository for habit data
   * @param userRepository  the repository for user data
   */
  public HabitService(HabitDbRepository habitRepository, UserDbRepository userRepository) {
    this.habitRepository = habitRepository;
    this.userRepository = userRepository;
  }
  
  /**
   * Creates a new habit for a user.
   *
   * @param userEmail   the email of the user creating the habit
   * @param name        the name of the habit
   * @param description the description of the habit
   * @param frequency   the frequency of the habit
   * @throws IllegalArgumentException if the user is not found
   */
  public void createHabit(String userEmail, String name, String description, Habit.Frequency frequency) {
    User user = userRepository.readUserByEmail(userEmail);
    Habit habit = new Habit();
    habit.setUserId(user.getId());
    habit.setName(name);
    habit.setDescription(description);
    habit.setFrequency(frequency);
    habitRepository.create(habit);
  }
  
  /**
   * Edits an existing habit.
   *
   * @param id          the ID of the habit to edit
   * @param name        the new name of the habit
   * @param description the new description of the habit
   * @param frequency   the new frequency of the habit
   * @throws IllegalArgumentException if the habit is not found
   */
  public void editHabit(String id, String name, String description, Habit.Frequency frequency) {
    Habit habit = habitRepository.readAll().stream()
      .filter(h -> h.getId().equals(id))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Habit not found"));
    habit.setName(name);
    habit.setDescription(description);
    habit.setFrequency(frequency);
    habitRepository.update(habit);
  }
  
  /**
   * Deletes a habit.
   *
   * @param id the ID of the habit to delete
   */
  public void deleteHabit(Long id) {
    habitRepository.delete(id);
  }
  
  /**
   * Retrieves a list of habits for a user, optionally filtered by date and active status.
   *
   * @param userId     the ID of the user whose habits to retrieve
   * @param filterDate the date to filter habits by (habits created on or after this date will be included), can be null
   * @param active     whether to retrieve only active habits, can be null to retrieve all habits
   * @return a list of habits matching the specified criteria
   */
  public List<Habit> viewHabits(Long userId, LocalDate filterDate, Boolean active) {
    return habitRepository.getByUserId(userId).stream()
      .filter(h -> filterDate == null || !h.getCreationDate().isBefore(filterDate))
      .filter(h -> active == null || h.isActive() == active)
      .collect(Collectors.toList());
  }
}