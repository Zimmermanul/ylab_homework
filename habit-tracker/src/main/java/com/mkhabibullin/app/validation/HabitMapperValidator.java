package com.mkhabibullin.app.validation;

import com.mkhabibullin.app.dto.habit.CreateHabitDTO;
import com.mkhabibullin.app.dto.habit.UpdateHabitDTO;
import com.mkhabibullin.app.exception.ValidationException;

public class HabitMapperValidator {
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
  
  public void validateUpdateHabitDTO(UpdateHabitDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("UpdateHabitDTO cannot be null");
    }
    if (dto.name() != null && dto.name().trim().isEmpty()) {
      throw new ValidationException("Habit name cannot be empty");
    }
  }
}
