package com.mkhabibullin.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Represents the execution or tracking of a habit on a specific date.
 * This class encapsulates information about when a habit was performed and whether it was completed.
 */
@Entity
@Table(name = "habit_executions", schema = "entity")
public class HabitExecution {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "habit_id", nullable = false)
  private Long habitId;
  @Column(nullable = false)
  private LocalDate date;
  @Column
  private boolean completed;
  
  protected HabitExecution() {
  }
  
  /**
   * Constructs a new HabitExecution with the specified details.
   * Automatically generates a unique identifier for this execution.
   *
   * @param habitId   the identifier of the associated habit
   * @param date      the date of this habit execution
   * @param completed whether the habit was completed on this date
   */
  public HabitExecution(Long habitId, LocalDate date, boolean completed) {
    this.habitId = habitId;
    this.date = date;
    this.completed = completed;
  }
  
  /**
   * Gets the unique identifier of this habit execution.
   * @return the habit execution's ID
   */
  public Long getId() {
    return id;
  }
  
  /**
   * Sets the unique identifier for this habit execution.
   * @param id the ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * Gets the identifier of the associated habit.
   * @return the associated habit's ID
   */
  public Long getHabitId() {
    return habitId;
  }
  
  /**
   * Sets the identifier of the associated habit.
   * @param habitId the habit ID to set
   */
  public void setHabitId(Long habitId) {
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
