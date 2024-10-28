package com.mkhabibullin.app.application.validation;

import com.mkhabibullin.app.application.mapper.HabitMapper;
import com.mkhabibullin.app.domain.exception.ValidationException;
import com.mkhabibullin.app.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.app.presentation.dto.habit.UpdateHabitDTO;

/**
 * Validator class for habit-related DTOs.
 * Provides validation methods used by {@link HabitMapper} to ensure
 * data integrity during habit creation and updates.
 */
public class HabitMapperValidator {
  /**
   * Validates a DTO for habit creation.
   * Ensures that all required fields are present and valid.
   *
   * @param dto The habit creation DTO to validate
   */
  public void validateCreateHabitDTO(CreateHabitDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("CreateHabitDTO cannot be null");
    }
    if (dto.name() == null || dto.name().trim().isEmpty()) {
      throw new ValidationException("Habit name is required");
    }
    if (dto.frequency() == null) {
      throw new ValidationException("Habit frequency is required");
    }
  }
  
  /**
   * Validates a DTO for habit updates.
   * Ensures that provided fields meet validation criteria.
   *
   * @param dto The habit update DTO to validate
   */
  public void validateUpdateHabitDTO(UpdateHabitDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("UpdateHabitDTO cannot be null");
    }
    if (dto.name() != null && dto.name().trim().isEmpty()) {
      throw new ValidationException("Habit name cannot be empty");
    }
  }
}
