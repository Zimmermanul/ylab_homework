package com.mkhabibullin.habitTracker.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents the execution or tracking of a habit on a specific date.
 * This class encapsulates information about when a habit was performed and whether it was completed.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
