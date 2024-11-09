package com.mkhabibullin.application.service.implementation;

import com.mkhabibullin.application.mapper.HabitMapper;
import com.mkhabibullin.application.service.HabitService;
import com.mkhabibullin.common.MessageConstants;
import com.mkhabibullin.domain.exception.HabitNotFoundException;
import com.mkhabibullin.domain.exception.InvalidHabitIdException;
import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.HabitRepository;
import com.mkhabibullin.infrastructure.persistence.repository.UserRepository;
import com.mkhabibullin.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.presentation.dto.habit.UpdateHabitDTO;
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
  private final HabitMapper habitMapper;
  
  /**
   * Constructs a new HabitServiceImpl with the required repositories.
   * Uses Spring's dependency injection to autowire the repositories.
   *
   * @param habitRepository repository for habit data operations
   * @param userRepository  repository for user data operations
   */
  
  public HabitServiceImpl(HabitRepository habitRepository, UserRepository userRepository, HabitMapper habitMapper) {
    this.habitRepository = habitRepository;
    this.userRepository = userRepository;
    this.habitMapper = habitMapper;
  }
  
  /**
   * Creates a new habit for a user using the provided DTO and user email.
   *
   * @param userEmail the email of the user creating the habit
   * @param createHabitDTO the DTO containing habit creation data
   */
  @Override
  public void create(String userEmail, CreateHabitDTO createHabitDTO) {
    User user = userRepository.readUserByEmail(userEmail);
    Habit habit = habitMapper.createDtoToHabit(createHabitDTO);
    habit.setUserId(user.getId());
    habitRepository.create(habit);
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
  public void edit(String id, UpdateHabitDTO updateDTO) {
    try {
      Long habitId = Long.parseLong(id);
      Habit habit = habitRepository.getById(habitId);
      if (habit == null) {
        throw new HabitNotFoundException(String.format(MessageConstants.HABIT_NOT_FOUND, habitId));
      }
      habitMapper.updateHabitFromDto(updateDTO, habit);
      habitRepository.update(habit);
    } catch (NumberFormatException e) {
      throw new InvalidHabitIdException(String.format(MessageConstants.HABIT_NOT_FOUND, id), e);
    }
  }
  
  /**
   * Deletes a habit.
   *
   * @param id the ID of the habit to delete
   */
  @Override
  public void delete(Long id) {
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
  public List<Habit> getAll(Long userId, LocalDate filterDate, Boolean active) {
    return habitRepository.getByUserId(userId).stream()
      .filter(h -> filterDate == null || !h.getCreationDate().isBefore(filterDate))
      .filter(h -> active == null || h.isActive() == active)
      .collect(Collectors.toList());
  }
}