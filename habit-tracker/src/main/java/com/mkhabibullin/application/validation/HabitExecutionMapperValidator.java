package com.mkhabibullin.application.validation;

import com.mkhabibullin.application.mapper.HabitExecutionMapper;
import org.mapstruct.Named;

import java.time.LocalDate;

/**
 * Validator class for habit execution mapping operations.
 * Provides validation methods used by {@link HabitExecutionMapper} to ensure
 * data integrity during the mapping process.
 */
public class HabitExecutionMapperValidator {
  /**
   * Validates the date for a habit execution.
   * Ensures that the date is not null and not in the future.
   *
   * @param date The date to validate
   * @return The validated date
   */
  @Named("validateDate")
  public LocalDate validateDate(LocalDate date) {
    if (date == null) {
      throw new IllegalArgumentException("Date is required");
    }
    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Cannot record executions for future dates");
    }
    return date;
  }
  
  /**
   * Validates the completion status of a habit execution.
   * Ensures that the completion status is not null.
   *
   * @param completed The completion status to validate
   * @return The validated completion status
   */
  @Named("validateCompleted")
  public Boolean validateCompleted(Boolean completed) {
    if (completed == null) {
      throw new IllegalArgumentException("Completion status is required");
    }
    return completed;
  }
  
  /**
   * Validates the habit ID associated with an execution.
   * Ensures that the habit ID is not null.
   *
   * @param habitId The habit ID to validate
   * @return The validated habit ID
   */
  @Named("validateHabitId")
  public Long validateHabitId(Long habitId) {
    if (habitId == null) {
      throw new IllegalArgumentException("Habit ID is required");
    }
    return habitId;
  }
}
