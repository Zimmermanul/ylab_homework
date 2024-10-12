package com.mkhabibullin.habitManagement.controller;

import com.mkhabibullin.habitManagement.model.Habit;
import com.mkhabibullin.habitManagement.service.HabitService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Класс-контроллер для обработки запросов, вызовов соответствующих методов сервис-классов
 */
public class HabitController {
  private HabitService habitService;
  
  public HabitController(HabitService habitService) {
    this.habitService = habitService;
  }
  
  public void createHabit(String userEmail, String name, String description, Habit.Frequency frequency) throws IOException {
    habitService.createHabit(userEmail, name, description, frequency);
  }
  
  public void editHabit(String id, String name, String description, Habit.Frequency frequency) throws IOException {
    habitService.editHabit(id, name, description, frequency);
  }
  
  public void deleteHabit(String id) throws IOException {
    habitService.deleteHabit(id);
  }
  
  public List<Habit> viewHabits(String userEmail, LocalDate filterDate, Boolean active) throws IOException {
    return habitService.viewHabits(userEmail, filterDate, active);
  }
}
