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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * REST Controller for managing habit execution tracking and analysis.
 * Provides endpoints for recording habit completions, retrieving execution history,
 * and generating various statistics and progress reports.
 *
 * This controller handles all aspects of habit execution tracking including:
 * - Recording individual habit executions
 * - Retrieving execution history
 * - Calculating statistics and progress metrics
 * - Generating detailed progress reports
 * - Tracking streaks and success rates
 */
@RestController
@RequestMapping("/api/habit-executions")
@Tag(name = "Habit Execution Management", description = "API endpoints for tracking and analyzing habit execution progress")
@Validated
public class HabitExecutionRestController {
  private static final Logger log = LoggerFactory.getLogger(HabitExecutionRestController.class);
  private final HabitExecutionService executionService;
  private final HabitExecutionMapper executionMapper;
  private final HabitExecutionMapperValidator executionValidator;
  
  /**
   * Constructs a new HabitExecutionRestController with required dependencies.
   *
   * @param executionService   Service for handling habit execution operations
   * @param executionMapper    Mapper for converting between domain models and DTOs
   * @param executionValidator Validator for ensuring execution data integrity
   */
  public HabitExecutionRestController(HabitExecutionService executionService,
                                      HabitExecutionMapper executionMapper,
                                      HabitExecutionMapperValidator executionValidator) {
    this.executionService = executionService;
    this.executionMapper = executionMapper;
    this.executionValidator = executionValidator;
  }
  
  
  /**
   * Records a new habit execution entry.
   *
   * @param habitId ID of the habit being tracked
   * @param executionDTO Details of the habit execution
   * @param currentUser Currently authenticated user
   * @return ResponseEntity containing confirmation message
   * @throws ValidationException if the execution data is invalid
   */
  @Operation(
    summary = "Track habit execution",
    description = "Records a new habit execution entry"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "201",
      description = "Execution recorded successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = MessageDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid input data",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "User not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Habit not found",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @PostMapping(value = "/track/{habitId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Audited(audited = "Track Habit Execution")
  public ResponseEntity<MessageDTO> trackExecution(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable("habitId") Long habitId,
    @RequestBody HabitExecutionRequestDTO executionDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException {
    log.debug("Recording execution for habit {} by user {}", habitId, currentUser.getEmail());
    executionValidator.validateHabitExecutionRequestDTO(executionDTO);
    HabitExecution execution = executionMapper.requestDtoToExecution(executionDTO, habitId);
    executionService.markHabitExecution(execution.getHabitId(), execution.getDate(), execution.isCompleted());
    log.info("Execution recorded for habit {} by user {}", habitId, currentUser.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new MessageDTO("Habit execution recorded successfully"));
  }
  
  /**
   * Retrieves the complete execution history for a specific habit.
   *
   * @param habitId ID of the habit to retrieve history for
   * @param currentUser Currently authenticated user
   * @return ResponseEntity containing list of execution records
   */
  @Operation(
    summary = "Get execution history",
    description = "Retrieves the complete execution history for a specific habit"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "History retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = HabitExecutionResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "User not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Habit not found",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @GetMapping(value = "/history/{habitId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Get Execution History")
  public ResponseEntity<List<HabitExecutionResponseDTO>> getExecutionHistory(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable("habitId") Long habitId,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Retrieving execution history for habit {} by user {}", habitId, currentUser.getEmail());
    List<HabitExecution> history = executionService.getHabitExecutionHistory(habitId);
    List<HabitExecutionResponseDTO> historyDTOs = executionMapper.executionsToResponseDtos(history);
    log.info("Retrieved {} execution records for habit {} by user {}",
      historyDTOs.size(), habitId, currentUser.getEmail());
    return ResponseEntity.ok(historyDTOs);
  }
  
  /**
   * Retrieves detailed statistics for a habit within a specified date range.
   *
   * @param habitId ID of the habit to analyze
   * @param startDate Beginning of the date range
   * @param endDate End of the date range
   * @param currentUser Currently authenticated user
   * @return ResponseEntity containing habit statistics
   */
  @Operation(
    summary = "Get habit execution statistics",
    description = "Retrieves detailed statistics for a habit within a specified date range"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Statistics retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = HabitStatisticsDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid date range",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "User not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Habit not found",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @GetMapping(value = "/statistics/{habitId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Get Statistics")
  public ResponseEntity<HabitStatisticsDTO> getStatistics(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable("habitId") Long habitId,
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
  
  /**
   * Generates a comprehensive progress report for a habit within a specified date range.
   *
   * @param habitId ID of the habit to analyze
   * @param startDate Beginning of the date range
   * @param endDate End of the date range
   * @param currentUser Currently authenticated user
   * @return ResponseEntity containing detailed progress report
   */
  @Operation(
    summary = "Get habit progress report",
    description = "Generates a comprehensive progress report for a habit within a specified date range"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Progress report retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = HabitProgressReportDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid date range",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "User not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Habit not found",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @GetMapping(value = "/progress/{habitId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Get Progress Report")
  public ResponseEntity<HabitProgressReportDTO> getProgressReport(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable("habitId") Long habitId,
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
  
  /**
   * Retrieves the current streak count for a specific habit.
   *
   * @param habitId ID of the habit to check
   * @param currentUser Currently authenticated user
   * @return ResponseEntity containing current streak count
   */
  @Operation(
    summary = "Get current habit streak",
    description = "Retrieves the current streak count for a specific habit"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Streak retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = Integer.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "User not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Habit not found",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @GetMapping(value = "/{habitId}/streak", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Get Current Streak")
  public ResponseEntity<Integer> getCurrentStreak(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable("habitId") Long habitId,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Retrieving current streak for habit {} by user {}", habitId, currentUser.getEmail());
    int currentStreak = executionService.getCurrentStreak(habitId);
    log.info("Retrieved current streak of {} days for habit {} by user {}",
      currentStreak, habitId, currentUser.getEmail());
    return ResponseEntity.ok(currentStreak);
  }
  
  /**
   * Calculates the success rate for a habit within a specified date range.
   *
   * @param habitId ID of the habit to analyze
   * @param startDate Beginning of the date range
   * @param endDate End of the date range
   * @param currentUser Currently authenticated user
   * @return ResponseEntity containing success rate percentage
   */
  @Operation(
    summary = "Get habit success rate",
    description = "Calculates the success rate for a habit within a specified date range"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Success rate retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = Double.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid date range",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "User not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Habit not found",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @GetMapping(value = "/{habitId}/success-rate", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Get Success Rate")
  public ResponseEntity<Double> getSuccessRate(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable("habitId") Long habitId,
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
  
  /**
   * Handles illegal argument exceptions thrown during request processing.
   *
   * @param ex The illegal argument exception that was thrown
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error("Validation error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  /**
   * Handles entity not found exceptions thrown during request processing.
   *
   * @param ex The entity not found exception that was thrown
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorDTO> handleEntityNotFoundException(EntityNotFoundException ex) {
    log.error("Entity not found: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  /**
   * Handles authentication exceptions thrown during request processing.
   *
   * @param ex The authentication exception that was thrown
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ErrorDTO> handleAuthenticationException(AuthenticationException ex) {
    log.error("Authentication error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  /**
   * Handles constraint violation exceptions thrown during request processing.
   *
   * @param ex The constraint violation exception that was thrown
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDTO> handleConstraintViolationException(ConstraintViolationException ex) {
    log.error("Validation error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  /**
   * Handles any unexpected exceptions thrown during request processing.
   *
   * @param ex The unexpected exception that was thrown
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorDTO> handleException(Exception ex) {
    log.error("Unexpected error: ", ex);
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(new ErrorDTO("Internal server error", System.currentTimeMillis()));
  }
}