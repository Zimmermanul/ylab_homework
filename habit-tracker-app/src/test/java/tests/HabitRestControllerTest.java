package tests;

import com.mkhabibullin.habitTracker.application.mapper.HabitMapper;
import com.mkhabibullin.habitTracker.application.service.HabitService;
import com.mkhabibullin.habitTracker.application.validation.HabitValidator;
import com.mkhabibullin.habitTracker.domain.exception.CustomAuthenticationException;
import com.mkhabibullin.habitTracker.domain.exception.ValidationException;
import com.mkhabibullin.habitTracker.domain.model.Habit;
import com.mkhabibullin.habitTracker.presentation.controller.HabitRestController;
import com.mkhabibullin.habitTracker.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.habitTracker.presentation.dto.habit.HabitResponseDTO;
import com.mkhabibullin.habitTracker.presentation.dto.habit.UpdateHabitDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HabitRestControllerTest extends BaseTest {
  @Mock
  private HabitService habitService;
  @Mock
  private HabitMapper habitMapper;
  @Mock
  private HabitValidator habitValidator;
  private HabitRestController habitController;
  
  @Override
  protected void setupMockMvc() {
    habitController = new HabitRestController(habitService, habitMapper, habitValidator);
    mockMvc = buildMockMvc(habitController);
  }
  
  @Test
  void createHabitWithValidDataShouldCreateHabit() throws Exception {
    CreateHabitDTO createDTO = new CreateHabitDTO(
      "Morning Exercise",
      "30 minutes of exercise",
      Habit.Frequency.DAILY
    );
    performRequest(post("/api/habits")
      .content(toJson(createDTO)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.message").value("Habit created successfully"));
    verify(habitService).create(
      eq(TEST_USER_EMAIL),
      eq(createDTO)
    );
  }
  
  @Test
  void createHabitWithInvalidDataShouldReturnBadRequest() throws Exception {
    CreateHabitDTO createDTO = new CreateHabitDTO(
      "",  // Invalid empty name
      "Description",
      Habit.Frequency.DAILY
    );
    doThrow(new ValidationException("Habit name is required"))
      .when(habitValidator).validateCreateHabitDTO(any());
    performRequest(post("/api/habits")
      .content(toJson(createDTO)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Habit name is required"));
  }
  
  @Test
  void getHabitsShouldReturnFilteredHabits() throws Exception {
    LocalDate filterDate = LocalDate.now();
    List<Habit> habits = Arrays.asList(
      createTestHabit(1L, "Habit 1"),
      createTestHabit(2L, "Habit 2")
    );
    List<HabitResponseDTO> habitDTOs = Arrays.asList(
      createTestHabitDTO(1L, "Habit 1"),
      createTestHabitDTO(2L, "Habit 2")
    );
    given(habitService.getAll(TEST_USER_ID, filterDate, true))
      .willReturn(habits);
    given(habitMapper.habitsToResponseDtos(habits))
      .willReturn(habitDTOs);
    performRequest(get("/api/habits")
      .param("date", filterDate.toString())
      .param("active", "true"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].name").value("Habit 1"))
      .andExpect(jsonPath("$[1].name").value("Habit 2"));
  }
  
  @Test
  void updateHabitWithValidDataShouldUpdateHabit() throws Exception {
    String habitId = "123";
    UpdateHabitDTO updateDTO = new UpdateHabitDTO(
      "Morning Yoga",
      "15 minutes of yoga routine",
      Habit.Frequency.DAILY
    );
    performRequest(put("/api/habits/" + habitId)
      .content(toJson(updateDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("Habit updated successfully"));
    verify(habitService).edit(
      eq(habitId),
      eq(updateDTO)
    );
  }
  
  @Test
  void updateHabitWithInvalidDataShouldReturnBadRequest() throws Exception {
    String habitId = "1";
    UpdateHabitDTO updateDTO = new UpdateHabitDTO(
      "",
      "Description",
      Habit.Frequency.DAILY
    );
    doThrow(new ValidationException("Habit name cannot be empty"))
      .when(habitValidator).validateUpdateHabitDTO(any());
    performRequest(put("/api/habits/" + habitId)
      .content(toJson(updateDTO)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Habit name cannot be empty"));
  }
  
  @Test
  void deleteHabitWithValidIdShouldDeleteHabit() throws Exception {
    Long habitId = 1L;
    performRequest(delete("/api/habits/" + habitId))
      .andExpect(status().isNoContent());
    verify(habitService).delete(habitId);
  }
  
  @Test
  void deleteHabitWhenNotAuthenticatedShouldReturnUnauthorized() throws Exception {
    Long habitId = 1L;
    doThrow(new CustomAuthenticationException("User not authenticated"))
      .when(habitService).delete(habitId);
    performRequest(delete("/api/habits/" + habitId))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.message").value("User not authenticated"));
  }
  
  private Habit createTestHabit(Long id, String name) {
    Habit habit = new Habit();
    habit.setId(id);
    habit.setName(name);
    habit.setDescription("Test description");
    habit.setFrequency(Habit.Frequency.DAILY);
    habit.setCreationDate(LocalDate.now());
    habit.setActive(true);
    return habit;
  }
  
  private HabitResponseDTO createTestHabitDTO(Long id, String name) {
    return new HabitResponseDTO(
      id,
      TEST_USER_ID,
      name,
      "Test description",
      Habit.Frequency.DAILY,
      LocalDate.now(),
      true
    );
  }
}