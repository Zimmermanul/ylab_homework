package com.mkhabibullin;

import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.domain.model.HabitExecution;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.HabitDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.HabitExecutionDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.UserDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HabitExecutionDbRepositoryTest extends AbstractDatabaseTest {
  private HabitExecutionDbRepository executionRepository;
  private HabitDbRepository habitRepository;
  private UserDbRepository userRepository;
  private Long habitId;
  private Long userId;
  
  @BeforeEach
  void init() {
    executionRepository = new HabitExecutionDbRepository(dataSource);
    habitRepository = new HabitDbRepository(dataSource);
    userRepository = new UserDbRepository(dataSource);
    User user = new User("test@example.com", "Test User");
    user.setPasswordHash("hash");
    user.setSalt("salt");
    userRepository.createUser(user);
    userId = user.getId();
    Habit habit = new Habit();
    habit.setUserId(userId);
    habit.setName("Daily Exercise");
    habit.setFrequency(Habit.Frequency.DAILY);
    habit.setCreationDate(LocalDate.now());
    habit.setActive(true);
    habitRepository.create(habit);
    habitId = habit.getId();
  }
  
  @Test
  @DisplayName("Should save new execution with automatically generated ID")
  void shouldSaveExecutionWithGeneratedId() {
    HabitExecution execution = new HabitExecution(habitId, LocalDate.now(), true);
    executionRepository.save(execution);
    assertThat(execution.getId()).isNotNull();
    assertThat(execution.getId()).isGreaterThanOrEqualTo(100000L);
  }
  
  @Test
  @DisplayName("Should retrieve all executions for a specific habit")
  void shouldGetExecutionsByHabitId() {
    LocalDate today = LocalDate.now();
    HabitExecution execution1 = new HabitExecution(habitId, today, true);
    HabitExecution execution2 = new HabitExecution(habitId, today.minusDays(1), false);
    executionRepository.save(execution1);
    executionRepository.save(execution2);
    List<HabitExecution> executions = executionRepository.getByHabitId(habitId);
    assertThat(executions).hasSize(2);
    assertThat(executions)
      .extracting(HabitExecution::getDate)
      .containsExactlyInAnyOrder(today, today.minusDays(1));
  }
  
  @Test
  @DisplayName("Should retrieve executions within specified date range")
  void shouldGetExecutionsByDateRange() {
    LocalDate today = LocalDate.now();
    HabitExecution execution1 = new HabitExecution(habitId, today, true);
    HabitExecution execution2 = new HabitExecution(habitId, today.minusDays(1), true);
    HabitExecution execution3 = new HabitExecution(habitId, today.minusDays(5), false);
    executionRepository.save(execution1);
    executionRepository.save(execution2);
    executionRepository.save(execution3);
    List<HabitExecution> executions = executionRepository.getByHabitAndDateRange(
      habitId,
      today.minusDays(2),
      today
    );
    assertThat(executions).hasSize(2);
    assertThat(executions)
      .extracting(HabitExecution::getDate)
      .containsExactlyInAnyOrder(today, today.minusDays(1));
  }
  
  @Test
  @DisplayName("Should successfully update existing execution")
  void shouldUpdateExecution() {
    HabitExecution execution = new HabitExecution(habitId, LocalDate.now(), false);
    executionRepository.save(execution);
    execution.setCompleted(true);
    executionRepository.update(execution);
    HabitExecution updatedExecution = executionRepository.getById(execution.getId());
    assertThat(updatedExecution.isCompleted()).isTrue();
  }
  
  @Test
  @DisplayName("Should successfully delete execution")
  void shouldDeleteExecution() {
    HabitExecution execution = new HabitExecution(habitId, LocalDate.now(), true);
    executionRepository.save(execution);
    Long executionId = execution.getId();
    executionRepository.delete(executionId);
    assertThat(executionRepository.getById(executionId)).isNull();
  }
  
  @Test
  @DisplayName("Should return empty list when habit has no executions")
  void shouldReturnEmptyListForNonexistentHabit() {
    List<HabitExecution> executions = executionRepository.getByHabitId(999999L);
    assertThat(executions).isEmpty();
  }
  
  @Test
  @DisplayName("Should return empty list for date range with no executions")
  void shouldReturnEmptyListForDateRangeWithNoExecutions() {
    LocalDate futureDate = LocalDate.now().plusDays(10);
    List<HabitExecution> executions = executionRepository.getByHabitAndDateRange(
      habitId,
      futureDate,
      futureDate.plusDays(5)
    );
    assertThat(executions).isEmpty();
  }
  
  @Test
  @DisplayName("Should successfully retrieve execution by ID")
  void shouldGetExecutionById() {
    HabitExecution execution = new HabitExecution(habitId, LocalDate.now(), true);
    executionRepository.save(execution);
    HabitExecution foundExecution = executionRepository.getById(execution.getId());
    assertThat(foundExecution).isNotNull();
    assertThat(foundExecution.getId()).isEqualTo(execution.getId());
    assertThat(foundExecution.getHabitId()).isEqualTo(habitId);
    assertThat(foundExecution.isCompleted()).isTrue();
  }
  
  @Test
  @DisplayName("Should return null when execution ID does not exist")
  void shouldReturnNullForNonexistentExecutionId() {
    HabitExecution execution = executionRepository.getById(999999L);
    assertThat(execution).isNull();
  }
  
  @Test
  @DisplayName("Should handle multiple executions on the same date")
  void shouldHandleMultipleExecutionsOnSameDay() {
    LocalDate today = LocalDate.now();
    HabitExecution execution1 = new HabitExecution(habitId, today, true);
    HabitExecution execution2 = new HabitExecution(habitId, today, false);
    executionRepository.save(execution1);
    executionRepository.save(execution2);
    List<HabitExecution> executions = executionRepository.getByHabitId(habitId);
    assertThat(executions).hasSize(2);
    assertThat(executions)
      .extracting(HabitExecution::getDate)
      .containsOnly(today);
  }
  
  @Test
  @DisplayName("Should maintain chronological order when retrieving date range")
  void shouldRespectDateRangeOrdering() {
    LocalDate today = LocalDate.now();
    HabitExecution execution1 = new HabitExecution(habitId, today, true);
    HabitExecution execution2 = new HabitExecution(habitId, today.minusDays(2), true);
    HabitExecution execution3 = new HabitExecution(habitId, today.minusDays(1), true);
    executionRepository.save(execution2);
    executionRepository.save(execution3);
    executionRepository.save(execution1);
    List<HabitExecution> executions = executionRepository.getByHabitAndDateRange(
      habitId,
      today.minusDays(2),
      today
    );
    assertThat(executions)
      .extracting(HabitExecution::getDate)
      .containsExactly(
        today.minusDays(2),
        today.minusDays(1),
        today
      );
  }
}