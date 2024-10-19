package com.mkhabibullin.app.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a habit in a habit tracking application.
 * This class encapsulates all the information related to a single habit.
 * It implements Serializable to allow for easy saving and transmission of habit objects.
 */
public class Habit implements Serializable {
  private String id;
  private String userId;
  private String name;
  private String description;
  private Frequency frequency;
  private LocalDate creationDate;
  private boolean isActive;
  
  /**
   * Enumeration representing the frequency of a habit.
   */
  public enum Frequency {
    DAILY, WEEKLY
  }
  
  /**
   * Default constructor for Habit.
   * Initializes a new habit with a random UUID, sets the creation date to the current date,
   * and marks the habit as active.
   */
  public Habit() {
    this.id = UUID.randomUUID().toString();
    this.creationDate = LocalDate.now();
    this.isActive = true;
  }
  
  /**
   * Gets the user ID associated with this habit.
   *
   * @return the user ID
   */
  public String getUserId() {
    return userId;
  }
  
  /**
   * Sets the user ID for this habit.
   *
   * @param userId the user ID to set
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  /**
   * Gets the unique identifier of this habit.
   *
   * @return the habit's ID
   */
  public String getId() {
    return id;
  }
  
  /**
   * Sets the unique identifier for this habit.
   *
   * @param id the ID to set
   */
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Gets the name of this habit.
   *
   * @return the habit's name
   */
  public String getName() {
    return name;
  }
  
  /**
   * Sets the name of this habit.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Gets the description of this habit.
   *
   * @return the habit's description
   */
  public String getDescription() {
    return description;
  }
  
  /**
   * Sets the description of this habit.
   *
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }
  
  /**
   * Gets the frequency of this habit.
   *
   * @return the habit's frequency
   */
  public Frequency getFrequency() {
    return frequency;
  }
  
  
  /**
   * Sets the frequency of this habit.
   *
   * @param frequency the frequency to set
   */
  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }
  
  /**
   * Gets the creation date of this habit.
   *
   * @return the habit's creation date
   */
  public LocalDate getCreationDate() {
    return creationDate;
  }
  
  /**
   * Sets the creation date of this habit.
   *
   * @param creationDate the creation date to set
   */
  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }
  
  /**
   * Checks if this habit is active.
   *
   * @return true if the habit is active, false otherwise
   */
  public boolean isActive() {
    return isActive;
  }
  
  /**
   * Sets the active status of this habit.
   *
   * @param active the active status to set
   */
  public void setActive(boolean active) {
    isActive = active;
  }
  
  /**
   * Compares this habit to another object for equality.
   * Two habits are considered equal if they have the same id, name, description,
   * frequency, creation date, and active status.
   *
   * @param o the object to compare with
   * @return true if the objects are equal, false otherwise
   */
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
  
  /**
   * Generates a hash code for this habit.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, frequency, creationDate, isActive);
  }
  
  /**
   * Returns a string representation of this habit.
   *
   * @return a string representation of the habit
   */
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