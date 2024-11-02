package com.mkhabibullin.presentation.controller;


import com.mkhabibullin.application.mapper.HabitExecutionMapper;
import com.mkhabibullin.application.service.HabitExecutionService;
import com.mkhabibullin.application.validation.HabitExecutionMapperValidator;
import com.mkhabibullin.common.Audited;
import com.mkhabibullin.domain.exception.AuthenticationException;
import com.mkhabibullin.domain.exception.EntityNotFoundException;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.domain.model.HabitExecution;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import com.mkhabibullin.presentation.dto.MessageDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionRequestDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionResponseDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitProgressReportDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitStatisticsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for managing habit executions in the habit tracking application.
 * Provides endpoints for tracking, analyzing, and reporting habit execution progress.
 */
@RestController
@RequestMapping("/api/habit-executions")
@Tag(name = "Habit Execution Management", description = "API endpoints for tracking and analyzing habit execution progress")
@Validated
public class HabitExecutionRestController {
  private final HabitExecutionService executionService;
  private final HabitExecutionMapper executionMapper;
  private final HabitExecutionMapperValidator executionValidator;
  private static final Logger log = LoggerFactory.getLogger(HabitExecutionRestController.class);
  
  public HabitExecutionRestController(HabitExecutionService executionService,
                                      HabitExecutionMapper executionMapper,
                                      HabitExecutionMapperValidator executionValidator) {
    this.executionService = executionService;
    this.executionMapper = executionMapper;
    this.executionValidator = executionValidator;
  }
  
  @PostMapping(value = "/track/{habitId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Track habit execution",
    description = "Records a new habit execution entry")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Execution recorded successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid input data"),
    @ApiResponse(responseCode = "401", description = "User not authenticated"),
    @ApiResponse(responseCode = "404", description = "Habit not found")
  })
  @Audited(audited = "Track Habit Execution")
  public ResponseEntity<MessageDTO> trackExecution(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable Long habitId,
    @RequestBody HabitExecutionRequestDTO executionDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException {
    log.debug("Recording execution for habit {} by user {}", habitId, currentUser.getEmail());
    executionValidator.validateHabitExecutionRequestDTO(executionDTO);
    HabitExecution execution = executionMapper.requestDtoToExecution(executionDTO, habitId);
    executionService.markHabitExecution(
      execution.getHabitId(),
      execution.getDate(),
      execution.isCompleted()
    );
    log.info("Execution recorded for habit {} by user {}", habitId, currentUser.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new MessageDTO("Habit execution recorded successfully"));
  }
  
  @GetMapping(value = "/history/{habitId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get execution history",
    description = "Retrieves the complete execution history for a specific habit")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "User not authenticated"),
    @ApiResponse(responseCode = "404", description = "Habit not found")
  })
  @Audited(audited = "Get Execution History")
  public ResponseEntity<List<HabitExecutionResponseDTO>> getExecutionHistory(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable Long habitId,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Retrieving execution history for habit {} by user {}",
      habitId, currentUser.getEmail());
    List<HabitExecution> history = executionService.getHabitExecutionHistory(habitId);
    List<HabitExecutionResponseDTO> historyDTOs = executionMapper.executionsToResponseDtos(history);
    log.info("Retrieved {} execution records for habit {} by user {}",
      historyDTOs.size(), habitId, currentUser.getEmail());
    return ResponseEntity.ok(historyDTOs);
  }
  
  @GetMapping(value = "/statistics/{habitId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get habit execution statistics",
    description = "Retrieves detailed statistics for a habit within a specified date range")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid date range"),
    @ApiResponse(responseCode = "401", description = "User not authenticated"),
    @ApiResponse(responseCode = "404", description = "Habit not found")
  })
  @Audited(audited = "Get Statistics")
  public ResponseEntity<HabitStatisticsDTO> getStatistics(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable Long habitId,
    @Parameter(description = "Start date (YYYY-MM-DD)", required = true)
    @RequestParam LocalDate startDate,
    @Parameter(description = "End date (YYYY-MM-DD)", required = true)
    @RequestParam LocalDate endDate,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Retrieving statistics for habit {} by user {} from {} to {}",
      habitId, currentUser.getEmail(), startDate, endDate);
    validateDateRange(startDate, endDate);
    List<HabitExecution> history = executionService.getHabitExecutionHistory(habitId);
    List<HabitExecution> filteredHistory = filterHistoryByDateRange(history, startDate, endDate);
    int currentStreak = executionService.getCurrentStreak(habitId);
    double successPercentage = executionService.getSuccessPercentage(habitId, startDate, endDate);
    Map<DayOfWeek, Long> completionsByDay = calculateCompletionsByDay(filteredHistory);
    HabitStatisticsDTO statistics = executionMapper.createStatisticsDto(
      currentStreak,
      successPercentage,
      filteredHistory.size(),
      filteredHistory.stream().filter(HabitExecution::isCompleted).count(),
      filteredHistory.stream().filter(e -> !e.isCompleted()).count(),
      completionsByDay
    );
    log.info("Retrieved statistics for habit {} by user {}", habitId, currentUser.getEmail());
    return ResponseEntity.ok(statistics);
  }
  
  @GetMapping(value = "/progress/{habitId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get habit progress report",
    description = "Generates a comprehensive progress report for a habit within a specified date range")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Progress report retrieved successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid date range"),
    @ApiResponse(responseCode = "401", description = "User not authenticated"),
    @ApiResponse(responseCode = "404", description = "Habit not found")
  })
  @Audited(audited = "Get Progress Report")
  public ResponseEntity<HabitProgressReportDTO> getProgressReport(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable Long habitId,
    @Parameter(description = "Start date (YYYY-MM-DD)", required = true)
    @RequestParam LocalDate startDate,
    @Parameter(description = "End date (YYYY-MM-DD)", required = true)
    @RequestParam LocalDate endDate,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Generating progress report for habit {} by user {} from {} to {}",
      habitId, currentUser.getEmail(), startDate, endDate);
    validateDateRange(startDate, endDate);
    List<HabitExecution> history = executionService.getHabitExecutionHistory(habitId);
    List<HabitExecution> filteredHistory = filterHistoryByDateRange(history, startDate, endDate);
    String report = executionService.generateProgressReport(habitId, startDate, endDate);
    boolean improving = executionService.isImprovingTrend(filteredHistory);
    int longestStreak = executionService.calculateLongestStreak(filteredHistory);
    List<String> suggestions = executionService.generateSuggestions(null, filteredHistory);
    HabitProgressReportDTO progressReport = executionMapper.createProgressReportDto(
      report,
      improving,
      longestStreak,
      suggestions
    );
    log.info("Generated progress report for habit {} by user {}", habitId, currentUser.getEmail());
    return ResponseEntity.ok(progressReport);
  }
  
  @GetMapping(value = "/{habitId}/streak", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get current habit streak",
    description = "Retrieves the current streak count for a specific habit")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Streak retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "User not authenticated"),
    @ApiResponse(responseCode = "404", description = "Habit not found")
  })
  @Audited(audited = "Get Current Streak")
  public ResponseEntity<Integer> getCurrentStreak(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable Long habitId,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Retrieving current streak for habit {} by user {}",
      habitId, currentUser.getEmail());
    int currentStreak = executionService.getCurrentStreak(habitId);
    log.info("Retrieved current streak of {} days for habit {} by user {}",
      currentStreak, habitId, currentUser.getEmail());
    return ResponseEntity.ok(currentStreak);
  }
  
  @GetMapping(value = "/{habitId}/success-rate", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get habit success rate",
    description = "Calculates the success rate for a habit within a specified date range")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Success rate retrieved successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid date range"),
    @ApiResponse(responseCode = "401", description = "User not authenticated"),
    @ApiResponse(responseCode = "404", description = "Habit not found")
  })
  @Audited(audited = "Get Success Rate")
  public ResponseEntity<Double> getSuccessRate(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable Long habitId,
    @Parameter(description = "Start date (YYYY-MM-DD)", required = true)
    @RequestParam LocalDate startDate,
    @Parameter(description = "End date (YYYY-MM-DD)", required = true)
    @RequestParam LocalDate endDate,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Calculating success rate for habit {} by user {} from {} to {}",
      habitId, currentUser.getEmail(), startDate, endDate);
    validateDateRange(startDate, endDate);
    double successRate = executionService.getSuccessPercentage(habitId, startDate, endDate);
    log.info("Calculated success rate of {}% for habit {} by user {}",
      String.format("%.2f", successRate), habitId, currentUser.getEmail());
    return ResponseEntity.ok(successRate);
  }
  
  private void validateDateRange(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Start date and end date are required");
    }
    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("End date cannot be before start date");
    }
  }
  
  private List<HabitExecution> filterHistoryByDateRange(
    List<HabitExecution> history,
    LocalDate startDate,
    LocalDate endDate) {
    return history.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .sorted(Comparator.comparing(HabitExecution::getDate))
      .collect(Collectors.toList());
  }
  
  private Map<DayOfWeek, Long> calculateCompletionsByDay(List<HabitExecution> history) {
    return history.stream()
      .filter(HabitExecution::isCompleted)
      .collect(Collectors.groupingBy(
        e -> e.getDate().getDayOfWeek(),
        Collectors.counting()
      ));
  }
  
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error("Validation error: {}", ex.getMessage());
    return new ErrorDTO(ex.getMessage(), System.currentTimeMillis());
  }
  
  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO handleEntityNotFoundException(EntityNotFoundException ex) {
    log.error("Entity not found: {}", ex.getMessage());
    return new ErrorDTO(ex.getMessage(), System.currentTimeMillis());
  }
  
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorDTO handleAuthenticationException(AuthenticationException ex) {
    log.error("Authentication error: {}", ex.getMessage());
    return new ErrorDTO(ex.getMessage(), System.currentTimeMillis());
  }
  
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO handleException(Exception ex) {
    log.error("Unexpected error: ", ex);
    return new ErrorDTO("Internal server error", System.currentTimeMillis());
  }
  
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO handleConstraintViolationException(ConstraintViolationException ex) {
    log.error("Validation error: {}", ex.getMessage());
    return new ErrorDTO(ex.getMessage(), System.currentTimeMillis());
  }
}
