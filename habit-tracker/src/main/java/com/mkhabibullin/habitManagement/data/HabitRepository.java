package com.mkhabibullin.habitManagement.data;

import com.mkhabibullin.habitManagement.model.Habit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Class responsible for creating, reading, updating, deleting habits data
 */
public class HabitRepository {
  private static final Path HABIT_FILE = Paths.get("habits.txt");
  
  public void create(Habit habit) throws IOException {
    List<Habit> habits = readAllHabits();
    habits.add(habit);
    writeHabits(habits);
  }
  
  public void update(Habit habit) throws IOException {
    List<Habit> habits = readAllHabits();
    for (int i = 0; i < habits.size(); i++) {
      if (habits.get(i).getId().equals(habit.getId())) {
        habits.set(i, habit);
        break;
      }
    }
    writeHabits(habits);
  }
  
  public void delete(String id) throws IOException {
    List<Habit> habits = readAllHabits();
    habits.removeIf(h -> h.getId().equals(id));
    writeHabits(habits);
  }
  
  public List<Habit> readAll() throws IOException {
    return readAllHabits();
  }
  
  public List<Habit> getByUserId(String userId) throws IOException {
    return readAllHabits().stream()
      .filter(h -> h.getUserId().equals(userId))
      .collect(Collectors.toList());
  }
  
  public Habit getById(String id) throws IOException {
    List<Habit> habits = readAllHabits();
    return habits.stream()
      .filter(h -> h.getId().equals(id))
      .findFirst()
      .orElse(null);
  }
  
  private List<Habit> readAllHabits() throws IOException {
    if (!Files.exists(HABIT_FILE)) {
      return new ArrayList<>();
    }
    List<String> lines = Files.readAllLines(HABIT_FILE);
    List<Habit> habits = new ArrayList<>();
    for (String line : lines) {
      try {
        habits.add(parseHabit(line));
      } catch (IllegalArgumentException e) {
        System.err.println("Error parsing habit: " + e.getMessage());
      }
    }
    return habits;
  }
  
  private void writeHabits(List<Habit> habits) throws IOException {
    List<String> lines = habits.stream()
      .map(this::formatHabit)
      .collect(Collectors.toList());
    Files.write(HABIT_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }
  
  private Habit parseHabit(String line) {
    String[] parts = line.split(",");
    if (parts.length != 7) {
      throw new IllegalArgumentException("Invalid habit data: " + line);
    }
    Habit habit = new Habit();
    habit.setId(parts[0]);
    habit.setUserId(parts[1]);
    habit.setName(parts[2]);
    habit.setDescription(parts[3]);
    try {
      habit.setFrequency(Habit.Frequency.valueOf(parts[4].toUpperCase()));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid frequency value: " + parts[4], e);
    }
    habit.setCreationDate(LocalDate.parse(parts[5]));
    habit.setActive(Boolean.parseBoolean(parts[6]));
    return habit;
  }
  
  private String formatHabit(Habit habit) {
    return String.join(",",
      habit.getId(),
      habit.getUserId(),
      habit.getName(),
      habit.getDescription(),
      habit.getFrequency().toString(),
      habit.getCreationDate().toString(),
      String.valueOf(habit.isActive())
    );
  }
}
