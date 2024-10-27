package com.mkhabibullin.app;
import com.mkhabibullin.app.data.HabitDbRepository;
import com.mkhabibullin.app.data.UserDbRepository;
import com.mkhabibullin.app.model.Habit;
import com.mkhabibullin.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class HabitDbRepositoryTest extends AbstractDatabaseTest {
  private HabitDbRepository habitRepository;
  private UserDbRepository userRepository;
  private Long userId;
  
  @BeforeEach
  void init() {
    habitRepository = new HabitDbRepository(dataSource);
    userRepository = new UserDbRepository(dataSource);
    User user = new User("test@example.com", "Test User");
    user.setPasswordHash("hash");
    user.setSalt("salt");
    userRepository.createUser(user);
    userId = user.getId();
  }
  
  @Test
  @DisplayName("Should create new habit with automatically generated ID")
  void shouldCreateHabitWithGeneratedId() {
    Habit habit = new Habit();
    habit.setUserId(userId);
    habit.setName("Daily Exercise");
    habit.setDescription("30 minutes workout");
    habit.setFrequency(Habit.Frequency.DAILY);
    habit.setCreationDate(LocalDate.now());
    habit.setActive(true);
    habitRepository.create(habit);
    assertThat(habit.getId()).isNotNull();
    assertThat(habit.getId()).isGreaterThanOrEqualTo(100000L);
  }
  
  @Test
  @DisplayName("Should successfully retrieve habit by its ID")
  void shouldGetHabitById() {
    Habit habit = new Habit();
    habit.setUserId(userId);
    habit.setName("Daily Exercise");
    habit.setDescription("30 minutes workout");
    habit.setFrequency(Habit.Frequency.DAILY);
    habit.setCreationDate(LocalDate.now());
    habit.setActive(true);
    habitRepository.create(habit);
    Habit foundHabit = habitRepository.getById(habit.getId());
    assertThat(foundHabit).isNotNull();
    assertThat(foundHabit.getName()).isEqualTo("Daily Exercise");
    assertThat(foundHabit.getUserId()).isEqualTo(userId);
  }
  
  @Test
  @DisplayName("Should retrieve all habits for a specific user")
  void shouldGetHabitsByUserId() {
    Habit habit1 = new Habit();
    habit1.setUserId(userId);
    habit1.setName("Daily Exercise");
    habit1.setFrequency(Habit.Frequency.DAILY);
    habit1.setCreationDate(LocalDate.now());
    habitRepository.create(habit1);
    Habit habit2 = new Habit();
    habit2.setUserId(userId);
    habit2.setName("Weekly Reading");
    habit2.setFrequency(Habit.Frequency.WEEKLY);
    habit2.setCreationDate(LocalDate.now());
    habitRepository.create(habit2);
    List<Habit> habits = habitRepository.getByUserId(userId);
    assertThat(habits).hasSize(2);
    assertThat(habits).extracting(Habit::getName)
      .containsExactlyInAnyOrder("Daily Exercise", "Weekly Reading");
  }
  
  @Test
  @DisplayName("Should successfully update existing habit")
  void shouldUpdateHabit() {
    Habit habit = new Habit();
    habit.setUserId(userId);
    habit.setName("Daily Exercise");
    habit.setFrequency(Habit.Frequency.DAILY);
    habit.setCreationDate(LocalDate.now());
    habitRepository.create(habit);
    habit.setName("Morning Exercise");
    habitRepository.update(habit);
    Habit updatedHabit = habitRepository.getById(habit.getId());
    assertThat(updatedHabit.getName()).isEqualTo("Morning Exercise");
  }
  
  @Test
  @DisplayName("Should successfully delete existing habit")
  void shouldDeleteHabit() {
    Habit habit = new Habit();
    habit.setUserId(userId);
    habit.setName("Daily Exercise");
    habit.setFrequency(Habit.Frequency.DAILY);
    habit.setCreationDate(LocalDate.now());
    habitRepository.create(habit);
    habitRepository.delete(habit.getId());
    assertThat(habitRepository.getById(habit.getId())).isNull();
  }
}