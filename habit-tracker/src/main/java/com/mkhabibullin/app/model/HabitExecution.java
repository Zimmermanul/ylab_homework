package com.mkhabibullin.app.model;

import java.time.LocalDate;
/**
 * A model class that describes a habit execution.
 */
public class HabitExecution {
  private String id;
  private String habitId;
  private LocalDate date;
  private boolean completed;
  
  public HabitExecution(String habitId, LocalDate date, boolean completed) {
    this.id = java.util.UUID.randomUUID().toString();
    this.habitId = habitId;
    this.date = date;
    this.completed = completed;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getHabitId() {
    return habitId;
  }
  
  public void setHabitId(String habitId) {
    this.habitId = habitId;
  }
  
  public LocalDate getDate() {
    return date;
  }
  
  public void setDate(LocalDate date) {
    this.date = date;
  }
  
  public boolean isCompleted() {
    return completed;
  }
  
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
}
