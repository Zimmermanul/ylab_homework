package com.mkhabibullin.presentation.controller;

import com.mkhabibullin.application.mapper.HabitMapper;
import com.mkhabibullin.application.service.HabitService;
import com.mkhabibullin.application.validation.HabitMapperValidator;
import com.mkhabibullin.common.Audited;
import com.mkhabibullin.domain.exception.AuthenticationException;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import com.mkhabibullin.presentation.dto.MessageDTO;
import com.mkhabibullin.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.presentation.dto.habit.HabitResponseDTO;
import com.mkhabibullin.presentation.dto.habit.UpdateHabitDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing habits in the habit tracking application.
 * Provides endpoints for creating, reading, updating, and deleting habits.
 */
@RestController
@RequestMapping("/api/habits")
@Tag(name = "Habit Management", description = "API endpoints for managing habits")
@Validated
public class HabitRestController {
  private static final Logger log = LoggerFactory.getLogger(HabitRestController.class);
  private final HabitService habitService;
  private final HabitMapper habitMapper;
  private final HabitMapperValidator habitValidator;
  
  public HabitRestController(HabitService habitService,
                             HabitMapper habitMapper,
                             HabitMapperValidator habitValidator) {
    this.habitService = habitService;
    this.habitMapper = habitMapper;
    this.habitValidator = habitValidator;
  }
  
  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(habitValidator);
  }
  
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
    habitService.createHabit(
      currentUser.getEmail(),
      createDTO.name(),
      createDTO.description(),
      createDTO.frequency()
    );
    log.info("Habit created successfully for user: {}", currentUser.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new MessageDTO("Habit created successfully"));
  }
  
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
    List<Habit> habits = habitService.viewHabits(currentUser.getId(), date, active);
    List<HabitResponseDTO> habitDTOs = habitMapper.habitsToResponseDtos(habits);
    log.info("Retrieved {} habits for user: {}", habitDTOs.size(), currentUser.getEmail());
    return ResponseEntity.ok(habitDTOs);
  }
  
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
    @PathVariable String id,
    @RequestBody UpdateHabitDTO updateDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException {
    log.debug("Updating habit {} for user: {}", id, currentUser.getEmail());
    habitValidator.validateUpdateHabitDTO(updateDTO);
    habitService.editHabit(
      id,
      updateDTO.name(),
      updateDTO.description(),
      updateDTO.frequency()
    );
    
    log.info("Habit {} updated successfully for user: {}", id, currentUser.getEmail());
    return ResponseEntity.ok(new MessageDTO("Habit updated successfully"));
  }
  
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
    @PathVariable Long id,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) {
    log.debug("Deleting habit {} for user: {}", id, currentUser.getEmail());
    habitService.deleteHabit(id);
    log.info("Habit {} deleted successfully for user: {}", id, currentUser.getEmail());
  }
  
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ErrorDTO> handleAuthenticationException(
    AuthenticationException ex,
    WebRequest request) {
    log.error("Authentication error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDTO> handleValidationException(
    ValidationException ex,
    WebRequest request) {
    log.error("Validation error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorDTO> handleGlobalException(
    Exception ex,
    WebRequest request) {
    log.error("Unexpected error:", ex);
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(new ErrorDTO("Internal server error", System.currentTimeMillis()));
  }
}