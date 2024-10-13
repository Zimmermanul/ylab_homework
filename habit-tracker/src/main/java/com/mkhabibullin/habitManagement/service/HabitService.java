package com.mkhabibullin.habitManagement.service;

import com.mkhabibullin.auth.data.UserRepository;
import com.mkhabibullin.auth.model.User;
import com.mkhabibullin.habitManagement.data.HabitRepository;
import com.mkhabibullin.habitManagement.model.Habit;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
/**
 * A service that encapsulates the application's business logic related to habits (CRUD)
 */
public class HabitService {
  private HabitRepository habitRepository;
  private UserRepository userRepository;
  
  public HabitService(HabitRepository habitRepository, UserRepository userRepository) {
    this.habitRepository = habitRepository;
    this.userRepository = userRepository;
  }
  
  public void createHabit(String userEmail, String name, String description, Habit.Frequency frequency) throws IOException {
    User user = userRepository.readUserByEmail(userEmail);
    Habit habit = new Habit();
    habit.setUserId(user.getId());
    habit.setName(name);
    habit.setDescription(description);
    habit.setFrequency(frequency);
    habitRepository.create(habit);
  }
  
  public void editHabit(String id, String name, String description, Habit.Frequency frequency) throws IOException {
    Habit habit = habitRepository.readAll().stream()
      .filter(h -> h.getId().equals(id))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Habit not found"));
    habit.setName(name);
    habit.setDescription(description);
    habit.setFrequency(frequency);
    habitRepository.update(habit);
  }
  
  public void deleteHabit(String id) throws IOException {
    habitRepository.delete(id);
  }
  
  public List<Habit> viewHabits(String userId, LocalDate filterDate, Boolean active) throws IOException {
    return habitRepository.getByUserId(userId).stream()
      .filter(h -> filterDate == null || !h.getCreationDate().isBefore(filterDate))
      .filter(h -> active == null || h.isActive() == active)
      .collect(Collectors.toList());
  }
}