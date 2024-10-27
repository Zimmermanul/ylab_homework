package com.mkhabibullin.app.validation;

import org.mapstruct.Named;

import java.time.LocalDate;

public class HabitExecutionMapperValidator {
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
  
  @Named("validateCompleted")
  public Boolean validateCompleted(Boolean completed) {
    if (completed == null) {
      throw new IllegalArgumentException("Completion status is required");
    }
    return completed;
  }
  
  @Named("validateHabitId")
  public Long validateHabitId(Long habitId) {
    if (habitId == null) {
      throw new IllegalArgumentException("Habit ID is required");
    }
    return habitId;
  }
}
