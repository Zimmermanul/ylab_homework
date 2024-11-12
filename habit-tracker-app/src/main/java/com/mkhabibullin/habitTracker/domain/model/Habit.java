package com.mkhabibullin.habitTracker.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a habit in a habit tracking application.
 * This class encapsulates all the information related to a single habit.
 * It implements Serializable to allow for easy saving and transmission of habit objects.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "habits", schema = "entity")
public class Habit implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "user_id")
  private Long userId;
  
  @Column(nullable = false)
  private String name;
  
  @Column
  private String description;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Frequency frequency;
  
  @Column(name = "creation_date", nullable = false)
  private LocalDate creationDate = LocalDate.now();
  
  @Column(name = "is_active")
  private boolean isActive = true;
  
  /**
   * Enumeration representing the frequency of a habit.
   */
  public enum Frequency {
    DAILY, WEEKLY
  }
}