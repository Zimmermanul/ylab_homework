package com.mkhabibullin.app.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents the execution or tracking of a habit on a specific date.
 * This class encapsulates information about when a habit was performed and whether it was completed.
 */
public class HabitExecution {
  private String id;
  private String habitId;
  private LocalDate date;
  private boolean completed;
  
  /**
   * Constructs a new HabitExecution with the specified details.
   * Automatically generates a unique identifier for this execution.
   *
   * @param habitId   the identifier of the associated habit
   * @param date      the date of this habit execution
   * @param completed whether the habit was completed on this date
   */
  public HabitExecution(String habitId, LocalDate date, boolean completed) {
    this.id = UUID.randomUUID().toString();
    this.habitId = habitId;
    this.date = date;
    this.completed = completed;
  }
  
  /**
   * Gets the unique identifier of this habit execution.
   * @return the habit execution's ID
   */
  public String getId() {
    return id;
  }
  
  /**
   * Sets the unique identifier for this habit execution.
   * @param id the ID to set
   */
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Gets the identifier of the associated habit.
   * @return the associated habit's ID
   */
  public String getHabitId() {
    return habitId;
  }
  
  /**
   * Sets the identifier of the associated habit.
   * @param habitId the habit ID to set
   */
  public void setHabitId(String habitId) {
    this.habitId = habitId;
  }
  
  /**
   * Gets the date of this habit execution.
   * @return the date of execution
   */
  public LocalDate getDate() {
    return date;
  }
  
  /**
   * Sets the date of this habit execution.
   * @param date the date to set
   */
  public void setDate(LocalDate date) {
    this.date = date;
  }
  
  /**
   * Checks if the habit was completed on this execution date.
   * @return true if the habit was completed, false otherwise
   */
  public boolean isCompleted() {
    return completed;
  }
  
  /**
   * Sets the completion status of this habit execution.
   * @param completed the completion status to set
   */
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
}
