package com.mkhabibullin.app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.app.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application servlet that provides system status, health checks,
 * and basic application information.
 */
@WebServlet("/api/system/*")
public class MainApplicationServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(MainApplicationServlet.class);
  private final ObjectMapper objectMapper;
  private static final String CONTENT_TYPE = "application/json";
  private static final String CHARACTER_ENCODING = "UTF-8";
  private final LocalDateTime startupTime;
  
  public MainApplicationServlet() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.startupTime = LocalDateTime.now();
  }
  
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String pathInfo = request.getPathInfo();
    try {
      if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/status")) {
        handleGetStatus(request, response);
      } else if (pathInfo.equals("/health")) {
        handleHealthCheck(request, response);
      } else if (pathInfo.equals("/info")) {
        handleGetInfo(request, response);
      } else {
        sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
      }
    } catch (Exception e) {
      logger.error("Error processing GET request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  /**
   * Handles the status endpoint which provides current application status
   * and user session information.
   */
  private void handleGetStatus(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    User currentUser = getCurrentUser(request);
    
    Map<String, Object> status = new HashMap<>();
    status.put("status", "running");
    status.put("timestamp", LocalDateTime.now());
    status.put("startupTime", startupTime);
    status.put("uptime", getUptime());
    
    if (currentUser != null) {
      Map<String, Object> userInfo = new HashMap<>();
      userInfo.put("id", currentUser.getId());
      userInfo.put("email", currentUser.getEmail());
      userInfo.put("name", currentUser.getName());
      userInfo.put("isAdmin", currentUser.isAdmin());
      status.put("user", userInfo);
    }
    
    status.put("authenticated", currentUser != null);
    
    sendJsonResponse(response, HttpServletResponse.SC_OK, status);
    logger.debug("Status request processed successfully");
  }
  
  /**
   * Handles the health check endpoint which verifies critical system components.
   */
  private void handleHealthCheck(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    Map<String, Object> health = new HashMap<>();
    Map<String, Object> components = new HashMap<>();
    boolean isHealthy = true;
    
    // Check database connection
    try {
      // Implement database health check
      components.put("database", Map.of(
        "status", "up",
        "responseTime", 100 // Add actual response time measurement
      ));
    } catch (Exception e) {
      components.put("database", Map.of(
        "status", "down",
        "error", e.getMessage()
      ));
      isHealthy = false;
    }
    
    // Check session store
    try {
      request.getSession(false);
      components.put("session", Map.of("status", "up"));
    } catch (Exception e) {
      components.put("session", Map.of(
        "status", "down",
        "error", e.getMessage()
      ));
      isHealthy = false;
    }
    
    // Add memory status
    Runtime runtime = Runtime.getRuntime();
    Map<String, Object> memory = new HashMap<>();
    memory.put("total", runtime.totalMemory());
    memory.put("free", runtime.freeMemory());
    memory.put("max", runtime.maxMemory());
    components.put("memory", memory);
    
    health.put("status", isHealthy ? "healthy" : "unhealthy");
    health.put("timestamp", LocalDateTime.now());
    health.put("components", components);
    
    int status = isHealthy ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE;
    sendJsonResponse(response, status, health);
    
    if (!isHealthy) {
      logger.warn("Health check failed: {}", health);
    }
  }
  
  /**
   * Handles the info endpoint which provides general application information.
   */
  private void handleGetInfo(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    Map<String, Object> info = new HashMap<>();
    
    // Application information
    info.put("name", "Habit Tracker");
    info.put("version", getApplicationVersion());
    info.put("environment", getEnvironment());
    
    // System information
    Map<String, Object> system = new HashMap<>();
    system.put("javaVersion", System.getProperty("java.version"));
    system.put("os", System.getProperty("os.name"));
    system.put("processors", Runtime.getRuntime().availableProcessors());
    info.put("system", system);
    
    // Configuration information (non-sensitive)
    Map<String, Object> config = new HashMap<>();
    config.put("timezone", System.getProperty("user.timezone"));
    config.put("encoding", System.getProperty("file.encoding"));
    info.put("config", config);
    
    sendJsonResponse(response, HttpServletResponse.SC_OK, info);
    logger.debug("Info request processed successfully");
  }
  
  /**
   * Gets the current logged-in user from the session.
   */
  private User getCurrentUser(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    return session != null ? (User) session.getAttribute("user") : null;
  }
  
  /**
   * Calculates the application uptime.
   */
  private Map<String, Long> getUptime() {
    LocalDateTime now = LocalDateTime.now();
    java.time.Duration duration = java.time.Duration.between(startupTime, now);
    
    Map<String, Long> uptime = new HashMap<>();
    uptime.put("days", duration.toDays());
    uptime.put("hours", (long) duration.toHoursPart());
    uptime.put("minutes", (long) duration.toMinutesPart());
    uptime.put("seconds", (long) duration.toSecondsPart());
    
    return uptime;
  }
  
  /**
   * Gets the application version from configuration or manifest.
   */
  private String getApplicationVersion() {
    // You can implement this to read from a properties file or manifest
    return "1.0.0";
  }
  
  /**
   * Gets the current environment (dev, test, prod).
   */
  private String getEnvironment() {
    // Implement to read from configuration or environment variable
    return System.getProperty("app.environment", "development");
  }
  
  private void sendJsonResponse(HttpServletResponse response, int status, Object data)
    throws IOException {
    response.setStatus(status);
    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(CHARACTER_ENCODING);
    objectMapper.writeValue(response.getOutputStream(), data);
  }
  
  private void sendError(HttpServletResponse response, int status, String message)
    throws IOException {
    Map<String, Object> error = new HashMap<>();
    error.put("error", message);
    error.put("status", status);
    error.put("timestamp", LocalDateTime.now());
    
    response.setStatus(status);
    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(CHARACTER_ENCODING);
    objectMapper.writeValue(response.getOutputStream(), error);
  }
}