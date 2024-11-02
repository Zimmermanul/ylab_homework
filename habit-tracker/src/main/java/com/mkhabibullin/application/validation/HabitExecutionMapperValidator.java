package com.mkhabibullin.application.validation;

import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionRequestDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
/**
 * Validator component for habit execution-related DTOs.
 * Provides validation methods for ensuring data integrity during habit execution operations.
 */
@Component
public class HabitExecutionMapperValidator {
  
  /**
   * Validates a DTO for habit execution tracking.
   *
   * @param dto The habit execution request DTO to validate
   * @throws ValidationException if validation fails
   */
  public void validateHabitExecutionRequestDTO(HabitExecutionRequestDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("HabitExecutionRequestDTO cannot be null");
    }
    
    validateDate(dto.date());
    validateCompleted(dto.completed());
  }
  
  private void validateDate(LocalDate date) throws ValidationException {
    if (date == null) {
      throw new ValidationException("Date is required");
    }
    if (date.isAfter(LocalDate.now())) {
      throw new ValidationException("Cannot record executions for future dates");
    }
  }
  
  private void validateCompleted(Boolean completed) throws ValidationException {
    if (completed == null) {
      throw new ValidationException("Completion status is required");
    }
  }
}
