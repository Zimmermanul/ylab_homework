package com.mkhabibullin.presentation.controller;

import com.mkhabibullin.application.mapper.AuditMapper;
import com.mkhabibullin.application.service.AuditLogService;
import com.mkhabibullin.application.validation.AuditMapperValidator;
import com.mkhabibullin.domain.exception.AuthenticationException;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import com.mkhabibullin.presentation.dto.audit.AuditLogResponseDTO;
import com.mkhabibullin.presentation.dto.audit.AuditStatisticsDTO;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for managing audit logs in the application.
 * Provides endpoints for retrieving and analyzing audit log data.
 */
@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Log Management", description = "API endpoints for managing and retrieving audit logs")
@Validated
public class AuditRestController {
  private static final Logger log = LoggerFactory.getLogger(AuditRestController.class);
  private final AuditLogService auditLogService;
  private final AuditMapper auditMapper;
  private final AuditMapperValidator auditValidator;
  
  public AuditRestController(AuditLogService auditLogService,
                             AuditMapper auditMapper,
                             AuditMapperValidator auditValidator) {
    this.auditLogService = auditLogService;
    this.auditMapper = auditMapper;
    this.auditValidator = auditValidator;
  }
  
  @Operation(
    summary = "Get recent audit logs",
    description = "Retrieves the most recent audit logs up to the specified limit"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Recent logs retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = AuditLogResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid limit parameter",
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
  @GetMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AuditLogResponseDTO>> getRecentLogs(
    @Parameter(description = "Maximum number of logs to retrieve (1-100)", example = "10")
    @RequestParam(defaultValue = "10") Integer limit,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException, IOException {
    log.debug("Retrieving {} recent audit logs", limit);
    if (limit <= 0 || limit > 100) {
      throw new ValidationException("Limit must be between 1 and 100");
    }
    var logs = auditLogService.getRecentLogs(limit);
    var responseDtos = auditMapper.auditLogsToResponseDtos(logs);
    for (AuditLogResponseDTO dto : responseDtos) {
      auditValidator.validateAuditLogDTO(dto);
    }
    log.info("Retrieved {} recent audit logs for user {}", limit, currentUser.getId());
    return ResponseEntity.ok(responseDtos);
  }
  
  @Operation(
    summary = "Get audit logs for specific user",
    description = "Retrieves all audit logs associated with the specified username"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "User logs retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = AuditLogResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid username",
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
  @GetMapping(value = "/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AuditLogResponseDTO>> getUserLogs(
    @Parameter(description = "Username to retrieve logs for", example = "john.doe", required = true)
    @PathVariable String username,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException, IOException {
    log.debug("Retrieving audit logs for user {}", username);
    if (username == null || username.trim().isEmpty()) {
      throw new ValidationException("Username cannot be empty");
    }
    var logs = auditLogService.getUserLogs(username);
    var responseDtos = auditMapper.auditLogsToResponseDtos(logs);
    for (AuditLogResponseDTO dto : responseDtos) {
      auditValidator.validateAuditLogDTO(dto);
    }
    log.info("Retrieved {} audit logs for user {} by user {}",
      responseDtos.size(), username, currentUser.getId());
    return ResponseEntity.ok(responseDtos);
  }
  
  @Operation(
    summary = "Get audit logs for specific operation",
    description = "Retrieves all audit logs for the specified operation type"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Operation logs retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = AuditLogResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid operation",
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
  @GetMapping(value = "/operation/{operation}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AuditLogResponseDTO>> getOperationLogs(
    @Parameter(description = "Operation to retrieve logs for", example = "Create Habit", required = true)
    @PathVariable String operation,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException, IOException {
    log.debug("Retrieving audit logs for operation {}", operation);
    if (operation == null || operation.trim().isEmpty()) {
      throw new ValidationException("Operation cannot be empty");
    }
    var logs = auditLogService.getOperationLogs(operation);
    var responseDtos = auditMapper.auditLogsToResponseDtos(logs);
    log.info("Retrieved {} audit logs for operation {} by user {}",
      responseDtos.size(), operation, currentUser.getId());
    return ResponseEntity.ok(responseDtos);
  }
  
  @Operation(
    summary = "Get audit statistics for date range",
    description = "Retrieves audit statistics for the specified date-time range"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Statistics retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = AuditStatisticsDTO.class)
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
    )
  })
  @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuditStatisticsDTO> getStatistics(
    @Parameter(description = "Start date-time (ISO-8601 format)", example = "2024-03-01T00:00:00", required = true)
    @RequestParam LocalDateTime startDateTime,
    @Parameter(description = "End date-time (ISO-8601 format)", example = "2024-03-31T23:59:59", required = true)
    @RequestParam LocalDateTime endDateTime,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException, IOException {
    log.debug("Retrieving audit statistics from {} to {}", startDateTime, endDateTime);
    validateDateTimeRange(startDateTime, endDateTime);
    var statistics = auditLogService.getStatistics(startDateTime, endDateTime);
    var statisticsDto = auditMapper.statisticsToDto(statistics);
    auditValidator.validateAuditStatisticsDTO(statisticsDto);
    log.info("Retrieved audit statistics for period {}-{} by user {}",
      startDateTime, endDateTime, currentUser.getId());
    return ResponseEntity.ok(statisticsDto);
  }
  
  private void validateDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime)
    throws ValidationException {
    if (startDateTime == null || endDateTime == null) {
      throw new ValidationException("Start date-time and end date-time are required");
    }
    if (endDateTime.isBefore(startDateTime)) {
      throw new ValidationException("End date-time cannot be before start date-time");
    }
    if (startDateTime.isAfter(LocalDateTime.now()) || endDateTime.isAfter(LocalDateTime.now())) {
      throw new ValidationException("Date range cannot be in the future");
    }
  }
  
  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDTO> handleValidationException(ValidationException ex) {
    log.error("Validation error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ErrorDTO> handleAuthenticationException(AuthenticationException ex) {
    log.error("Authentication error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDTO> handleConstraintViolationException(ConstraintViolationException ex) {
    log.error("Validation error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorDTO> handleException(Exception ex) {
    log.error("Unexpected error: ", ex);
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(new ErrorDTO("Internal server error", System.currentTimeMillis()));
  }
}