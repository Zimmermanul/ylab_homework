package com.mkhabibullin.app.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A model class that describes a habit.
 */
public class Habit implements Serializable {
  private String id;
  private String userId;
  private String name;
  private String description;
  private Frequency frequency;
  private LocalDate creationDate;
  private boolean isActive;
  
  public enum Frequency {
    DAILY, WEEKLY
  }
  
  public Habit() {
    this.id = UUID.randomUUID().toString();
    this.creationDate = LocalDate.now();
    this.isActive = true;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public Frequency getFrequency() {
    return frequency;
  }
  
  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }
  
  public LocalDate getCreationDate() {
    return creationDate;
  }
  
  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }
  
  public boolean isActive() {
    return isActive;
  }
  
  public void setActive(boolean active) {
    isActive = active;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Habit habit = (Habit) o;
    return isActive == habit.isActive &&
           Objects.equals(id, habit.id) &&
           Objects.equals(name, habit.name) &&
           Objects.equals(description, habit.description) &&
           frequency == habit.frequency &&
           Objects.equals(creationDate, habit.creationDate);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, frequency, creationDate, isActive);
  }
  
  @Override
  public String toString() {
    return "Habit{" +
           "id='" + id + '\'' +
           ", name='" + name + '\'' +
           ", description='" + description + '\'' +
           ", frequency=" + frequency +
           ", creationDate=" + creationDate +
           ", isActive=" + isActive +
           '}';
  }
}