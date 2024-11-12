package com.mkhabibullin.habitTracker.application.service.implementation;

import com.mkhabibullin.habitTracker.application.mapper.HabitMapper;
import com.mkhabibullin.habitTracker.application.service.HabitService;
import com.mkhabibullin.habitTracker.common.MessageConstants;
import com.mkhabibullin.habitTracker.domain.exception.HabitNotFoundException;
import com.mkhabibullin.habitTracker.domain.exception.InvalidHabitIdException;
import com.mkhabibullin.habitTracker.domain.model.Habit;
import com.mkhabibullin.habitTracker.domain.model.User;
import com.mkhabibullin.habitTracker.infrastructure.persistence.repository.HabitRepository;
import com.mkhabibullin.habitTracker.infrastructure.persistence.repository.UserRepository;
import com.mkhabibullin.habitTracker.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.habitTracker.presentation.dto.habit.UpdateHabitDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitServiceImpl implements HabitService {
  private final HabitRepository habitRepository;
  private final UserRepository userRepository;
  private final HabitMapper habitMapper;
  
  /**
   * Creates a new habit for a user using the provided DTO and user email.
   *
   * @param userEmail the email of the user creating the habit
   * @param createHabitDTO the DTO containing habit creation data
   */
  @Override
  @Transactional
  public void create(String userEmail, CreateHabitDTO createHabitDTO) {
    log.debug("Creating new habit for user: {}", userEmail);
    User user = userRepository.readUserByEmail(userEmail);
    Habit habit = habitMapper.createDtoToHabit(createHabitDTO);
    habit.setUserId(user.getId());
    habitRepository.create(habit);
    log.info("Created new habit with ID {} for user {}", habit.getId(), userEmail);
  }
  
  /**
   * Edits an existing habit using the provided update DTO.
   *
   * @param id         the ID of the habit to edit
   * @param updateDTO  the DTO containing the updated habit data
   * @throws InvalidHabitIdException if the habit ID format is invalid
   * @throws HabitNotFoundException if no habit exists with the given ID
   */
  @Override
  @Transactional
  public void edit(String id, UpdateHabitDTO updateDTO) {
    log.debug("Editing habit with ID: {}", id);
    try {
      Long habitId = Long.parseLong(id);
      Habit habit = habitRepository.getById(habitId);
      if (habit == null) {
        throw new HabitNotFoundException(String.format(MessageConstants.HABIT_NOT_FOUND, habitId));
      }
      habitMapper.updateHabitFromDto(updateDTO, habit);
      habitRepository.update(habit);
      log.info("Updated habit with ID: {}", id);
    } catch (NumberFormatException e) {
      log.error("Invalid habit ID format: {}", id);
      throw new InvalidHabitIdException(String.format(MessageConstants.HABIT_NOT_FOUND, id), e);
    }
  }
  
  /**
   * Deletes a habit.
   *
   * @param id the ID of the habit to delete
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("Deleting habit with ID: {}", id);
    habitRepository.delete(id);
    log.info("Deleted habit with ID: {}", id);
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
  public List<Habit> getAll(Long userId, LocalDate filterDate, Boolean active) {
    log.debug("Retrieving habits for user ID: {}, filterDate: {}, active: {}", userId, filterDate, active);
    return habitRepository.getByUserId(userId).stream()
      .filter(h -> filterDate == null || !h.getCreationDate().isBefore(filterDate))
      .filter(h -> active == null || h.isActive() == active)
      .collect(Collectors.toList());
  }
}