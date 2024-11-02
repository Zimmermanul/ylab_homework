package com.mkhabibullin.application.service.implementation;

import com.mkhabibullin.application.service.HabitService;
import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.HabitRepository;
import com.mkhabibullin.infrastructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the HabitService interface that provides habit management functionality.
 * This class handles the business logic for creating, updating, deleting, and retrieving habits
 * while interacting with the necessary repositories for data persistence.
 *
 * @see HabitService
 * @see HabitRepository
 * @see UserRepository
 */
@Service
@Transactional
public class HabitServiceImpl implements HabitService {
  private final HabitRepository habitRepository;
  private final UserRepository userRepository;
  
  /**
   * Constructs a new HabitServiceImpl with the required repositories.
   * Uses Spring's dependency injection to autowire the repositories.
   *
   * @param habitRepository repository for habit data operations
   * @param userRepository  repository for user data operations
   */
  
  public HabitServiceImpl(HabitRepository habitRepository, UserRepository userRepository) {
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public List<Habit> viewHabits(Long userId, LocalDate filterDate, Boolean active) {
    return habitRepository.getByUserId(userId).stream()
      .filter(h -> filterDate == null || !h.getCreationDate().isBefore(filterDate))
      .filter(h -> active == null || h.isActive() == active)
      .collect(Collectors.toList());
  }
}