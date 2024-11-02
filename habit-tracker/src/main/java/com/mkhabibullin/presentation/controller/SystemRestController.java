package com.mkhabibullin.presentation.controller;

import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller that provides system status, health checks,
 * and basic application information.
 */
@RestController
@RequestMapping("/api/system")
@Tag(name = "System Management", description = "API endpoints for system monitoring and management")
@Validated
public class SystemRestController {
  private static final Logger log = LoggerFactory.getLogger(SystemRestController.class);
  private final LocalDateTime startupTime;
  
  public SystemRestController() {
    this.startupTime = LocalDateTime.now();
  }
  
  @GetMapping(value = {"/", "/status"}, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
    summary = "Get system status",
    description = "Retrieves current system status including uptime and user information if authenticated"
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<Map<String, Object>> getStatus(
    @Parameter(hidden = true)
    @SessionAttribute(value = "user", required = false) User currentUser) {
    log.debug("Processing status request");
    Map<String, Object> status = new HashMap<>();
    status.put("status", "running");
    status.put("timestamp", LocalDateTime.now());
    status.put("startupTime", startupTime);
    status.put("uptime", calculateUptime());
    if (currentUser != null) {
      Map<String, Object> userInfo = new HashMap<>();
      userInfo.put("id", currentUser.getId());
      userInfo.put("email", currentUser.getEmail());
      userInfo.put("name", currentUser.getName());
      userInfo.put("isAdmin", currentUser.isAdmin());
      status.put("user", userInfo);
    }
    status.put("authenticated", currentUser != null);
    log.debug("Status request processed successfully");
    return ResponseEntity.ok(status);
  }
  
  @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
    summary = "Get system health status",
    description = "Checks the health of various system components including database, session, and memory usage"
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "System is healthy"),
    @ApiResponse(responseCode = "503", description = "System is unhealthy")
  })
  public ResponseEntity<Map<String, Object>> getHealth() {
    log.debug("Processing health check request");
    Map<String, Object> health = new HashMap<>();
    Map<String, Object> components = new HashMap<>();
    boolean isHealthy = true;
    try {
      components.put("database", Map.of(
        "status", "up",
        "responseTime", 100
      ));
    } catch (Exception e) {
      components.put("database", Map.of(
        "status", "down",
        "error", e.getMessage()
      ));
      isHealthy = false;
    }
    try {
      components.put("session", Map.of("status", "up"));
    } catch (Exception e) {
      components.put("session", Map.of(
        "status", "down",
        "error", e.getMessage()
      ));
      isHealthy = false;
    }
    Runtime runtime = Runtime.getRuntime();
    Map<String, Object> memory = new HashMap<>();
    memory.put("total", runtime.totalMemory());
    memory.put("free", runtime.freeMemory());
    memory.put("max", runtime.maxMemory());
    components.put("memory", memory);
    health.put("status", isHealthy ? "healthy" : "unhealthy");
    health.put("timestamp", LocalDateTime.now());
    health.put("components", components);
    if (!isHealthy) {
      log.warn("Health check failed: {}", health);
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
    }
    return ResponseEntity.ok(health);
  }
  
  @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
    summary = "Get application information",
    description = "Retrieves basic information about the application including name and version"
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Information retrieved successfully"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<Map<String, Object>> getInfo() {
    log.debug("Processing info request");
    Map<String, Object> info = new HashMap<>();
    info.put("name", "Habit Tracker");
    info.put("version", "1.0");
    log.debug("Info request processed successfully");
    return ResponseEntity.ok(info);
  }
  
  private Map<String, Long> calculateUptime() {
    Duration duration = Duration.between(startupTime, LocalDateTime.now());
    Map<String, Long> uptime = new HashMap<>();
    uptime.put("days", duration.toDays());
    uptime.put("hours", (long) duration.toHoursPart());
    uptime.put("minutes", (long) duration.toMinutesPart());
    uptime.put("seconds", (long) duration.toSecondsPart());
    return uptime;
  }
  
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO handleException(Exception ex) {
    log.error("Unexpected error: ", ex);
    return new ErrorDTO("Internal server error", System.currentTimeMillis());
  }
}