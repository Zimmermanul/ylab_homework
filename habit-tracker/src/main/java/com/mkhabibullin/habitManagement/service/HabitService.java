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
 * Сервис, инкапсулирующий бизнес-логику приложения, связанную с пользователями (регистрация, аутентификация,
 * обновление, удаление аккаунтов)
 */
public class HabitService {
  private HabitRepository habitRepository;
  private UserRepository userRepository;
  
  public HabitService(HabitRepository habitRepository, UserRepository userRepository) {
    this.habitRepository = habitRepository;
    this.userRepository = userRepository;
  }
  
  public void createHabit(String userEmail, String name, String description, Habit.Frequency frequency) throws IOException {
    User user = userRepository.readUser(userEmail);
    Habit habit = new Habit();
    habit.setUserId(user.getEmail());
    habit.setName(name);
    habit.setDescription(description);
    habit.setFrequency(frequency);
    habitRepository.save(habit);
  }
  
  public void editHabit(String id, String name, String description, Habit.Frequency frequency) throws IOException {
    Habit habit = habitRepository.getAll().stream()
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
  
  public List<Habit> viewHabits(String userEmail, LocalDate filterDate, Boolean active) throws IOException {
    return habitRepository.getByUserId(userEmail).stream()
      .filter(h -> filterDate == null || !h.getCreationDate().isBefore(filterDate))
      .filter(h -> active == null || h.isActive() == active)
      .collect(Collectors.toList());
  }
}