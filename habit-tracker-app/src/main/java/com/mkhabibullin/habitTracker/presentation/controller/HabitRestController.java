package com.mkhabibullin.habitTracker.presentation.controller;

import com.mkhabibullin.audit.annotation.Audited;
import com.mkhabibullin.habitTracker.application.mapper.HabitMapper;
import com.mkhabibullin.habitTracker.application.service.HabitService;
import com.mkhabibullin.habitTracker.application.validation.HabitValidator;
import com.mkhabibullin.habitTracker.domain.exception.ValidationException;
import com.mkhabibullin.habitTracker.domain.model.Habit;
import com.mkhabibullin.habitTracker.domain.model.User;
import com.mkhabibullin.audit.presentation.dto.ErrorDTO;
import com.mkhabibullin.habitTracker.presentation.dto.MessageDTO;
import com.mkhabibullin.habitTracker.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.habitTracker.presentation.dto.habit.HabitResponseDTO;
import com.mkhabibullin.habitTracker.presentation.dto.habit.UpdateHabitDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing habits in the application.
 * Provides endpoints for creating, retrieving, updating, and deleting habits.
 * Handles user-specific habit management with authentication and validation.
 * All operations are audited and require user authentication.
 * The controller provides comprehensive error handling for various scenarios.
 */
@RestController
@RequestMapping("/api/habits")
@Tag(name = "Habit Management", description = "API endpoints for managing habits")
@Validated
@RequiredArgsConstructor
@Slf4j
public class HabitRestController {
  private final HabitService habitService;
  private final HabitMapper habitMapper;
  private final HabitValidator habitValidator;
  
  /**
   * Initializes the WebDataBinder with the habit validator.
   * Sets up automatic validation for incoming habit-related requests.
   *
   * @param binder WebDataBinder to be configured with validators
   */
  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(habitValidator);
  }
  
  /**
   * Creates a new habit for the authenticated user.
   * The habit details are validated before creation.
   *
   * @param currentUser Currently authenticated user
   * @param createDTO DTO containing the new habit details
   * @return ResponseEntity with creation confirmation message
   * @throws ValidationException if the habit data is invalid
   */
  @Operation(
    summary = "Create a new habit",
    description = "Creates a new habit for the authenticated user"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "201",
      description = "Habit created successfully",
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
    )
  })
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Audited(audited = "Create Habit")
  public ResponseEntity<MessageDTO> createHabit(
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser,
    @RequestBody CreateHabitDTO createDTO) throws ValidationException {
    log.debug("Creating new habit for user: {}", currentUser.getEmail());
    habitValidator.validateCreateHabitDTO(createDTO);
    habitService.create(currentUser.getEmail(), createDTO);
    log.info("Habit created successfully for user: {}", currentUser.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new MessageDTO("Habit created successfully"));
  }
  
  /**
   * Retrieves habits for the authenticated user with optional filtering.
   * Supports filtering by date and active status.
   *
   * @param date Optional date filter (YYYY-MM-DD format)
   * @param active Optional active status filter
   * @param currentUser Currently authenticated user
   * @return ResponseEntity containing list of matching habits
   */
  @Operation(
    summary = "Get user habits",
    description = "Retrieves all habits for the authenticated user with optional filtering"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Habits retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = HabitResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "User not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "View Habits")
  public ResponseEntity<List<HabitResponseDTO>> getHabits(
    @Parameter(description = "Filter by date (YYYY-MM-DD)")
    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
    @Parameter(description = "Filter by active status")
    @RequestParam(required = false) Boolean active,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Retrieving habits for user: {}, date: {}, active: {}",
      currentUser.getEmail(), date, active);
    List<Habit> habits = habitService.getAll(currentUser.getId(), date, active);
    List<HabitResponseDTO> habitDTOs = habitMapper.habitsToResponseDtos(habits);
    log.info("Retrieved {} habits for user: {}", habitDTOs.size(), currentUser.getEmail());
    return ResponseEntity.ok(habitDTOs);
  }
  
  /**
   * Updates an existing habit's details.
   * Validates the updated information before applying changes.
   *
   * @param id ID of the habit to update
   * @param updateDTO DTO containing the updated habit details
   * @param currentUser Currently authenticated user
   * @return ResponseEntity with update confirmation message
   * @throws ValidationException if the updated data is invalid
   */
  @Operation(
    summary = "Update a habit",
    description = "Updates an existing habit's details"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Habit updated successfully",
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
  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Update Habit")
  public ResponseEntity<MessageDTO> updateHabit(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable("id") String id,
    @RequestBody UpdateHabitDTO updateDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException {
    log.debug("Updating habit {} for user: {}", id, currentUser.getEmail());
    habitValidator.validateUpdateHabitDTO(updateDTO);
    habitService.edit(id, updateDTO);
    log.info("Habit {} updated successfully for user: {}", id, currentUser.getEmail());
    return ResponseEntity.ok(new MessageDTO("Habit updated successfully"));
  }
  
  /**
   * Permanently deletes a habit.
   * Verifies the habit exists and belongs to the authenticated user.
   *
   * @param id ID of the habit to delete
   * @param currentUser Currently authenticated user
   */
  @Operation(
    summary = "Delete a habit",
    description = "Permanently deletes a habit"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "204",
      description = "Habit deleted successfully"
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
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Audited(audited = "Delete Habit")
  public void deleteHabit(
    @Parameter(description = "Habit ID", required = true)
    @PathVariable("id") Long id,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Deleting habit {} for user: {}", id, currentUser.getEmail());
    habitService.delete(id);
    log.info("Habit {} deleted successfully for user: {}", id, currentUser.getEmail());
  }
}