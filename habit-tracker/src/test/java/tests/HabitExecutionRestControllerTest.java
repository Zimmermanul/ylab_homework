package tests;

import com.mkhabibullin.application.mapper.HabitExecutionMapper;
import com.mkhabibullin.application.service.HabitExecutionService;
import com.mkhabibullin.application.validation.HabitExecutionMapperValidator;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.domain.model.HabitExecution;
import com.mkhabibullin.presentation.controller.HabitExecutionRestController;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionRequestDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionResponseDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitProgressReportDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitStatisticsDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HabitExecutionRestControllerTest extends BaseTest {
  @Mock
  private HabitExecutionService executionService;
  @Mock
  private HabitExecutionMapper executionMapper;
  @Mock
  private HabitExecutionMapperValidator executionValidator;
  private HabitExecutionRestController executionController;
  
  @Override
  protected void setupMockMvc() {
    executionController = new HabitExecutionRestController(
      executionService, executionMapper, executionValidator);
    mockMvc = buildMockMvc(executionController);
  }
  
  @Test
  void trackExecutionWithValidDataShouldRecordExecution() throws Exception {
    Long habitId = 1L;
    HabitExecutionRequestDTO requestDTO = new HabitExecutionRequestDTO(
      LocalDate.now(),
      true
    );
    HabitExecution execution = new HabitExecution(habitId, LocalDate.now(), true);
    given(executionMapper.requestDtoToExecution(requestDTO, habitId))
      .willReturn(execution);
    performRequest(post("/api/habit-executions/track/" + habitId)
      .content(toJson(requestDTO)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.message").value("Habit execution recorded successfully"));
    verify(executionService).markHabitExecution(
      eq(habitId),
      eq(requestDTO.date()),
      eq(requestDTO.completed())
    );
  }
  
  @Test
  void getExecutionHistoryShouldReturnHistory() throws Exception {
    Long habitId = 1L;
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.now(), true),
      new HabitExecution(habitId, LocalDate.now().minusDays(1), false)
    );
    List<HabitExecutionResponseDTO> responseDTOs = Arrays.asList(
      createTestExecutionDTO(1L, habitId, true),
      createTestExecutionDTO(2L, habitId, false)
    );
    given(executionService.getHabitExecutionHistory(habitId))
      .willReturn(executions);
    given(executionMapper.executionsToResponseDtos(executions))
      .willReturn(responseDTOs);
    performRequest(get("/api/habit-executions/history/" + habitId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[0].completed").value(true))
      .andExpect(jsonPath("$[1].completed").value(false));
  }
  
  @Test
  void getStatisticsShouldReturnStatistics() throws Exception {
    Long habitId = 1L;
    LocalDate startDate = LocalDate.now().minusDays(7);
    LocalDate endDate = LocalDate.now();
    Map<DayOfWeek, Long> completionsByDay = new HashMap<>();
    completionsByDay.put(DayOfWeek.MONDAY, 2L);
    completionsByDay.put(DayOfWeek.TUESDAY, 1L);
    HabitStatisticsDTO statisticsDTO = new HabitStatisticsDTO(
      5,
      75.0,
      10L,
      8L,
      2L,
      completionsByDay
    );
    given(executionService.getCurrentStreak(habitId)).willReturn(5);
    given(executionService.getSuccessPercentage(eq(habitId), any(), any()))
      .willReturn(75.0);
    given(executionMapper.createStatisticsDto(
      eq(5),
      eq(75.0),
      eq(10L),
      eq(8L),
      eq(2L),
      any()))
      .willReturn(statisticsDTO);
    performRequest(get("/api/habit-executions/statistics/" + habitId)
      .param("startDate", startDate.toString())
      .param("endDate", endDate.toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.currentStreak").value(5))
      .andExpect(jsonPath("$.successPercentage").value(75.0))
      .andExpect(jsonPath("$.totalExecutions").value(10))
      .andExpect(jsonPath("$.completedExecutions").value(8))
      .andExpect(jsonPath("$.missedExecutions").value(2));
  }
  
  @Test
  void getCurrentStreakShouldReturnCurrentStreak() throws Exception {
    Long habitId = 1L;
    int expectedStreak = 5;
    given(executionService.getCurrentStreak(habitId))
      .willReturn(expectedStreak);
    performRequest(get("/api/habit-executions/" + habitId + "/streak"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").value(expectedStreak));
  }
  
  @Test
  void getSuccessRateShouldReturnSuccessRate() throws Exception {
    Long habitId = 1L;
    LocalDate startDate = LocalDate.now().minusDays(30);
    LocalDate endDate = LocalDate.now();
    double expectedRate = 85.5;
    given(executionService.getSuccessPercentage(habitId, startDate, endDate))
      .willReturn(expectedRate);
    performRequest(get("/api/habit-executions/" + habitId + "/success-rate")
      .param("startDate", startDate.toString())
      .param("endDate", endDate.toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").value(expectedRate));
  }
  
  @Test
  void getProgressReportShouldReturnReport() throws Exception {
    Long habitId = 1L;
    LocalDate startDate = LocalDate.now().minusDays(30);
    LocalDate endDate = LocalDate.now();
    List<String> suggestions = Arrays.asList(
      "Try setting a reminder",
      "Start with smaller goals"
    );
    HabitProgressReportDTO reportDTO = new HabitProgressReportDTO(
      "Good progress over the last month",
      true,
      7,
      suggestions
    );
    given(executionService.generateProgressReport(habitId, startDate, endDate))
      .willReturn("Good progress over the last month");
    given(executionService.isImprovingTrend(any())).willReturn(true);
    given(executionService.calculateLongestStreak(any())).willReturn(7);
    given(executionService.generateSuggestions(any(), any())).willReturn(suggestions);
    given(executionMapper.createProgressReportDto(
      anyString(), anyBoolean(), anyInt(), anyList()))
      .willReturn(reportDTO);
    performRequest(get("/api/habit-executions/progress/" + habitId)
      .param("startDate", startDate.toString())
      .param("endDate", endDate.toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.report").value("Good progress over the last month"))
      .andExpect(jsonPath("$.improvingTrend").value(true))
      .andExpect(jsonPath("$.longestStreak").value(7))
      .andExpect(jsonPath("$.suggestions").isArray())
      .andExpect(jsonPath("$.suggestions[0]").value("Try setting a reminder"))
      .andExpect(jsonPath("$.suggestions[1]").value("Start with smaller goals"));
    verify(executionService).generateProgressReport(habitId, startDate, endDate);
    verify(executionService).isImprovingTrend(any());
    verify(executionService).calculateLongestStreak(any());
    verify(executionService).generateSuggestions(any(), any());
    verify(executionMapper).createProgressReportDto(
      eq("Good progress over the last month"),
      eq(true),
      eq(7),
      eq(suggestions)
    );
  }
  
  @Test
  void trackExecutionWithFutureDateShouldReturnBadRequest() throws Exception {
    Long habitId = 1L;
    HabitExecutionRequestDTO requestDTO = new HabitExecutionRequestDTO(
      LocalDate.now().plusDays(1),
      true
    );
    doThrow(new ValidationException("Cannot record executions for future dates"))
      .when(executionValidator)
      .validateHabitExecutionRequestDTO(requestDTO);
    performRequest(post("/api/habit-executions/track/" + habitId)
      .content(toJson(requestDTO)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message")
        .value("Cannot record executions for future dates"));
  }
  
  @Test
  void getStatisticsWithInvalidDateRangeShouldReturnBadRequest() throws Exception {
    Long habitId = 1L;
    LocalDate endDate = LocalDate.now().minusDays(7);
    LocalDate startDate = LocalDate.now();
    performRequest(get("/api/habit-executions/statistics/" + habitId)
      .param("startDate", startDate.toString())
      .param("endDate", endDate.toString()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message")
        .value("End date cannot be before start date"));
  }
  
  @Test
  void getProgressReportWithMissingDatesShouldReturnBadRequest() throws Exception {
    Long habitId = 1L;
    performRequest(get("/api/habit-executions/progress/" + habitId))
      .andExpect(status().isBadRequest());
  }
  
  private HabitExecutionResponseDTO createTestExecutionDTO(
    Long id, Long habitId, boolean completed) {
    return new HabitExecutionResponseDTO(
      id,
      habitId,
      LocalDate.now(),
      completed
    );
  }
}