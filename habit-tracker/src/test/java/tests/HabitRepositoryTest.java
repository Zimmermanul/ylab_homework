package tests;

import com.mkhabibullin.config.TestConfig;
import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.HabitRepository;
import com.mkhabibullin.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(TestConfig.class)
@ActiveProfiles("test")
@Testcontainers
class HabitRepositoryTest extends BaseTest {
  
  @Autowired
  private HabitRepository habitRepository;
  
  @Autowired
  private UserRepository userRepository;
  private Long userId;
  
  @BeforeEach
  void init() {
    User user = new User("test@example.com", "Test User");
    user.setPasswordHash("hash");
    user.setSalt("salt");
    userRepository.createUser(user);
    userId = user.getId();
  }
  
  @Test
  @DisplayName("Should create new habit with automatically generated ID")
  void shouldCreateHabitWithGeneratedId() {
    Habit habit = createTestHabit("Daily Exercise", "30 minutes workout");
    habitRepository.create(habit);
    assertThat(habit.getId()).isNotNull();
    assertThat(habit.getId()).isGreaterThanOrEqualTo(100000L);
  }
  
  @Test
  @DisplayName("Should successfully retrieve habit by its ID")
  void shouldGetHabitById() {
    Habit habit = createTestHabit("Daily Exercise", "30 minutes workout");
    habitRepository.create(habit);
    Habit foundHabit = habitRepository.getById(habit.getId());
    assertThat(foundHabit).isNotNull();
    assertThat(foundHabit.getName()).isEqualTo("Daily Exercise");
    assertThat(foundHabit.getUserId()).isEqualTo(userId);
  }
  
  @Test
  @DisplayName("Should retrieve all habits for a specific user")
  void shouldGetHabitsByUserId() {
    habitRepository.create(createTestHabit("Daily Exercise", null));
    habitRepository.create(createTestHabit("Weekly Reading", null));
    List<Habit> habits = habitRepository.getByUserId(userId);
    assertThat(habits).hasSize(2);
    assertThat(habits).extracting(Habit::getName)
      .containsExactlyInAnyOrder("Daily Exercise", "Weekly Reading");
  }
  
  @Test
  @DisplayName("Should successfully update existing habit")
  void shouldUpdateHabit() {
    Habit habit = createTestHabit("Daily Exercise", null);
    habitRepository.create(habit);
    habit.setName("Morning Exercise");
    habitRepository.update(habit);
    Habit updatedHabit = habitRepository.getById(habit.getId());
    assertThat(updatedHabit.getName()).isEqualTo("Morning Exercise");
  }
  
  @Test
  @DisplayName("Should successfully delete existing habit")
  void shouldDeleteHabit() {
    Habit habit = createTestHabit("Daily Exercise", null);
    habitRepository.create(habit);
    habitRepository.delete(habit.getId());
    assertThat(habitRepository.getById(habit.getId())).isNull();
  }
  
  private Habit createTestHabit(String name, String description) {
    Habit habit = new Habit();
    habit.setUserId(userId);
    habit.setName(name);
    habit.setDescription(description);
    habit.setFrequency(Habit.Frequency.DAILY);
    habit.setCreationDate(LocalDate.now());
    habit.setActive(true);
    return habit;
  }
}