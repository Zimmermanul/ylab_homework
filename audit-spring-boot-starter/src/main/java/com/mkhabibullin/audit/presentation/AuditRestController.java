package com.mkhabibullin.audit.presentation;

import com.mkhabibullin.audit.application.mapper.AuditMapper;
import com.mkhabibullin.audit.application.service.AuditLogService;
import com.mkhabibullin.audit.application.validation.AuditValidator;
import com.mkhabibullin.audit.domain.exception.AuditValidationException;
import com.mkhabibullin.audit.presentation.dto.AuditLogResponseDTO;
import com.mkhabibullin.audit.presentation.dto.AuditStatisticsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for managing audit logs in the application.
 * Provides endpoints for retrieving and analyzing audit log data.
 * <p>
 * This controller handles various audit log related operations including:
 * - Retrieving recent audit logs
 * - Fetching user-specific audit logs
 * - Getting operation-specific audit logs
 * - Generating audit statistics for specified time periods
 * <p>
 * All endpoints require user authentication and include appropriate validation
 * for input parameters.
 */
@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Log Management", description = "API endpoints for managing and retrieving audit logs")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AuditRestController {
  private final AuditLogService auditLogService;
  private final AuditMapper auditMapper;
  private final AuditValidator auditValidator;
  
  /**
   * Retrieves the most recent audit logs up to the specified limit.
   *
   * @param limit Maximum number of logs to retrieve (must be between 1 and 100)
   * @param currentUser Currently authenticated user making the request
   * @return ResponseEntity containing a list of recent audit logs
   */
  @Operation(summary = "Get recent audit logs")
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Recent logs retrieved successfully",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid limit parameter",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
  })
  @GetMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AuditLogResponseDTO>> getRecentLogs(
    @Parameter(description = "Maximum number of logs to retrieve (1-100)", example = "10")
    @RequestParam(defaultValue = "10") Integer limit,
    @AuthenticationPrincipal UserDetails currentUser) {
    
    log.debug("Retrieving {} recent audit logs", limit);
    if (limit <= 0 || limit > 100) {
      throw new AuditValidationException("Limit must be between 1 and 100");
    }
    var logs = auditLogService.getRecentLogs(limit);
    var responseDtos = auditMapper.auditLogsToResponseDtos(logs);
    responseDtos.forEach(auditValidator::validateAuditLogDTO);
    log.info("Retrieved {} recent audit logs for user {}", limit, currentUser.getUsername());
    return ResponseEntity.ok(responseDtos);
  }
  
  /**
   * Retrieves all audit logs associated with the specified username.
   *
   * @param username Username whose logs are to be retrieved
   * @param currentUser Currently authenticated user making the request
   * @return ResponseEntity containing a list of audit logs for the specified user
   */
  @Operation(summary = "Get audit logs for specific user")
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "User logs retrieved successfully",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid username",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
  })
  @GetMapping(value = "/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AuditLogResponseDTO>> getUserLogs(
    @Parameter(description = "Username to retrieve logs for", example = "john.doe")
    @PathVariable String username,
    @AuthenticationPrincipal UserDetails currentUser) {
    log.debug("Retrieving audit logs for user {}", username);
    if (username == null || username.trim().isEmpty()) {
      throw new AuditValidationException("Username cannot be empty");
    }
    var logs = auditLogService.getUserLogs(username);
    var responseDtos = auditMapper.auditLogsToResponseDtos(logs);
    responseDtos.forEach(auditValidator::validateAuditLogDTO);
    log.info("Retrieved {} audit logs for user {} by user {}",
      responseDtos.size(), username, currentUser.getUsername());
    return ResponseEntity.ok(responseDtos);
  }
  
  /**
   * Retrieves all audit logs for the specified operation type.
   *
   * @param operation Operation type to retrieve logs for
   * @param currentUser Currently authenticated user making the request
   * @return ResponseEntity containing a list of audit logs for the specified operation
   */
  @Operation(summary = "Get audit logs for specific operation")
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Operation logs retrieved successfully",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid operation",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
  })
  @GetMapping(value = "/operation/{operation}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AuditLogResponseDTO>> getOperationLogs(
    @Parameter(description = "Operation to retrieve logs for", example = "Create Habit")
    @PathVariable String operation,
    @AuthenticationPrincipal UserDetails currentUser) {
    log.debug("Retrieving audit logs for operation {}", operation);
    if (operation == null || operation.trim().isEmpty()) {
      throw new AuditValidationException("Operation cannot be empty");
    }
    var logs = auditLogService.getOperationLogs(operation);
    var responseDtos = auditMapper.auditLogsToResponseDtos(logs);
    log.info("Retrieved {} audit logs for operation {} by user {}",
      responseDtos.size(), operation, currentUser.getUsername());
    return ResponseEntity.ok(responseDtos);
  }
  
  
  /**
   * Retrieves audit statistics for the specified date-time range.
   *
   * @param startDateTime Start of the date range (ISO-8601 format)
   * @param endDateTime End of the date range (ISO-8601 format)
   * @param currentUser Currently authenticated user making the request
   * @return ResponseEntity containing audit statistics for the specified period
   */
  @Operation(summary = "Get audit statistics for date range")
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Statistics retrieved successfully",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid date range",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
  })
  @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuditStatisticsDTO> getStatistics(
    @Parameter(description = "Start date-time (ISO-8601)", example = "2024-03-01T00:00:00")
    @RequestParam LocalDateTime startDateTime,
    @Parameter(description = "End date-time (ISO-8601)", example = "2024-03-31T23:59:59")
    @RequestParam LocalDateTime endDateTime,
    @AuthenticationPrincipal UserDetails currentUser) {
    log.debug("Retrieving audit statistics from {} to {}", startDateTime, endDateTime);
    validateDateTimeRange(startDateTime, endDateTime);
    var statistics = auditLogService.getStatistics(startDateTime, endDateTime);
    var statisticsDto = auditMapper.statisticsToDto(statistics);
    auditValidator.validateAuditStatisticsDTO(statisticsDto);
    log.info("Retrieved audit statistics for period {}-{} by user {}",
      startDateTime, endDateTime, currentUser.getUsername());
    return ResponseEntity.ok(statisticsDto);
  }
  
  private void validateDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (startDateTime == null || endDateTime == null) {
      throw new AuditValidationException("Start date-time and end date-time are required");
    }
    if (endDateTime.isBefore(startDateTime)) {
      throw new AuditValidationException("End date-time cannot be before start date-time");
    }
    if (startDateTime.isAfter(LocalDateTime.now()) || endDateTime.isAfter(LocalDateTime.now())) {
      throw new AuditValidationException("Date range cannot be in the future");
    }
  }
}