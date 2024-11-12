package com.mkhabibullin.habitTracker.application.service;

import com.mkhabibullin.habitTracker.domain.model.Habit;
import com.mkhabibullin.habitTracker.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.habitTracker.presentation.dto.habit.UpdateHabitDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing habits in a habit tracking application.
 * This interface defines the contract for creating, editing, deleting, and viewing habits.
 * It is designed to work with user data and habit persistence layers.
 */
@Validated
public interface HabitService {
  /**
   * Creates a new habit for a specified user.
   * The habit is persisted in the database and associated with the user identified by the email.
   *
   * @param userEmail      the email of the user creating the habit
   * @param createHabitDTO the DTO containing habit creation data
   */
  void create(@NotBlank String userEmail, @NotNull @Valid CreateHabitDTO createHabitDTO);
  
  /**
   * Updates an existing habit with new information.
   * All fields of the habit will be updated to the provided values.
   *
   * @param id          the unique identifier of the habit to edit
   * @param updateDTO   the DTO containing habit edition data
   */
  void edit(@NotBlank String id, @NotNull @Valid UpdateHabitDTO updateDTO);
  
  /**
   * Deletes a habit from the system.
   * Once deleted, the habit cannot be recovered.
   *
   * @param id the unique identifier of the habit to delete
   */
  void delete(@NotNull Long id);
  
  /**
   * Retrieves a filtered list of habits for a specific user.
   * The results can be filtered by creation date and active status.
   *
   * @param userId     the unique identifier of the user whose habits to retrieve
   * @param filterDate optional date filter - if provided, only habits created on or after this date will be included
   * @param active     optional status filter - if provided, only habits matching this active status will be included
   * @return a List of habits matching the specified criteria, empty list if no habits are found
   */
  List<Habit> getAll(@NotNull Long userId, LocalDate filterDate, Boolean active);
}