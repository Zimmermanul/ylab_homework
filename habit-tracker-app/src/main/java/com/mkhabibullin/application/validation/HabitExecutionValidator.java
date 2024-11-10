package com.mkhabibullin.application.validation;

import com.mkhabibullin.common.MessageConstants;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionRequestDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Validator component for habit execution-related DTOs.
 * Provides validation methods for ensuring data integrity during habit execution operations.
 */
@Component
public class HabitExecutionValidator {
  
  /**
   * Validates a DTO for habit execution tracking.
   *
   * @param dto The habit execution request DTO to validate
   * @throws ValidationException if validation fails
   */
  public void validateHabitExecutionRequestDTO(HabitExecutionRequestDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException(MessageConstants.HABIT_EXECUTION_NULL);
    }
    
    validateDate(dto.date());
    validateCompleted(dto.completed());
  }
  
  /**
   * Validates a progress report request parameters.
   *
   * @param startDate start date of the report period
   * @param endDate   end date of the report period
   * @throws ValidationException if validation fails
   */
  public void validateProgressReportRequest(LocalDate startDate, LocalDate endDate) throws ValidationException {
    if (startDate == null) {
      throw new ValidationException(MessageConstants.START_DATE_REQUIRED);
    }
    if (endDate == null) {
      throw new ValidationException(MessageConstants.END_DATE_REQUIRED);
    }
    if (endDate.isBefore(startDate)) {
      throw new ValidationException(MessageConstants.INVALID_DATE_RANGE);
    }
    if (startDate.isAfter(LocalDate.now())) {
      throw new ValidationException(MessageConstants.START_DATE_FUTURE);
    }
    if (endDate.isAfter(LocalDate.now())) {
      throw new ValidationException(MessageConstants.END_DATE_FUTURE);
    }
  }
  
  private void validateDate(LocalDate date) throws ValidationException {
    if (date == null) {
      throw new ValidationException(MessageConstants.DATE_REQUIRED);
    }
    if (date.isAfter(LocalDate.now())) {
      throw new ValidationException(MessageConstants.DATE_FUTURE);
    }
  }
  
  private void validateCompleted(Boolean completed) throws ValidationException {
    if (completed == null) {
      throw new ValidationException(MessageConstants.COMPLETION_STATUS_REQUIRED);
    }
  }
}
