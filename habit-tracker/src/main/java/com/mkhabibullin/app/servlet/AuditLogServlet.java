package com.mkhabibullin.app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.app.controller.AuditLogController;
import com.mkhabibullin.app.dto.ErrorDTO;
import com.mkhabibullin.app.exception.AuthenticationException;
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
import java.time.format.DateTimeParseException;

/**
 * Servlet handling all audit log related operations.
 */
@WebServlet("/api/audit-logs/*")
public class AuditLogServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(AuditLogServlet.class);
  private final AuditLogController auditLogController;
  private final ObjectMapper objectMapper;
  private static final String CONTENT_TYPE = "application/json";
  private static final String CHARACTER_ENCODING = "UTF-8";
  
  public AuditLogServlet(AuditLogController auditLogController) {
    this.auditLogController = auditLogController;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = validateUserAuthentication(request);
      String pathInfo = request.getPathInfo();
      if (pathInfo != null) {
        if (pathInfo.startsWith("/user/")) {
          String username = pathInfo.substring("/user/".length());
          handleGetUserLogs(request, response, username, currentUser);
        } else if (pathInfo.startsWith("/operation/")) {
          String operation = pathInfo.substring("/operation/".length());
          handleGetOperationLogs(request, response, operation, currentUser);
        } else if (pathInfo.equals("/recent")) {
          handleGetRecentLogs(request, response, currentUser);
        } else if (pathInfo.equals("/statistics")) {
          handleGetStatistics(request, response, currentUser);
        } else {
          sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
        }
      } else {
        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Path info is required");
      }
    } catch (AuthenticationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing GET request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  private void handleGetUserLogs(HttpServletRequest request, HttpServletResponse response,
                                 String username, User currentUser) throws IOException {
    try {
      var logs = auditLogController.getUserLogs(username);
      sendJsonResponse(response, HttpServletResponse.SC_OK, logs);
      logger.info("Retrieved audit logs for user {} by admin {}", username, currentUser.getId());
    } catch (IllegalArgumentException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private void handleGetOperationLogs(HttpServletRequest request, HttpServletResponse response,
                                      String operation, User currentUser) throws IOException {
    try {
      var logs = auditLogController.getOperationLogs(operation);
      sendJsonResponse(response, HttpServletResponse.SC_OK, logs);
      logger.info("Retrieved audit logs for operation {} by user {}", operation, currentUser.getId());
    } catch (IllegalArgumentException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private void handleGetRecentLogs(HttpServletRequest request, HttpServletResponse response,
                                   User currentUser) throws IOException {
    try {
      int limit = parseLimit(request);
      var logs = auditLogController.getRecentLogs(limit);
      sendJsonResponse(response, HttpServletResponse.SC_OK, logs);
      logger.info("Retrieved {} recent audit logs by user {}", limit, currentUser.getId());
    } catch (IllegalArgumentException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private void handleGetStatistics(HttpServletRequest request, HttpServletResponse response,
                                   User currentUser) throws IOException {
    try {
      DateTimeRange dateRange = parseDateTimeRange(request);
      var statistics = auditLogController.getStatistics(dateRange.startDateTime(), dateRange.endDateTime());
      sendJsonResponse(response, HttpServletResponse.SC_OK, statistics);
      logger.info("Retrieved audit statistics by user {}", currentUser.getId());
    } catch (IllegalArgumentException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private User validateUserAuthentication(HttpServletRequest request) throws AuthenticationException {
    HttpSession session = request.getSession(false);
    User user = session != null ? (User) session.getAttribute("user") : null;
    if (user == null) {
      throw new AuthenticationException("User not authenticated");
    }
    return user;
  }
  
  private int parseLimit(HttpServletRequest request) {
    String limitStr = request.getParameter("limit");
    if (limitStr == null) {
      return 10; // default limit
    }
    try {
      int limit = Integer.parseInt(limitStr);
      if (limit <= 0 || limit > 100) {
        throw new IllegalArgumentException("Limit must be between 1 and 100");
      }
      return limit;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid limit format");
    }
  }
  
  private record DateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
  }
  
  private DateTimeRange parseDateTimeRange(HttpServletRequest request) {
    try {
      String startStr = request.getParameter("startDateTime");
      String endStr = request.getParameter("endDateTime");
      
      if (startStr == null || endStr == null) {
        throw new IllegalArgumentException("Start date/time and end date/time are required");
      }
      
      LocalDateTime startDateTime = LocalDateTime.parse(startStr);
      LocalDateTime endDateTime = LocalDateTime.parse(endStr);
      
      if (endDateTime.isBefore(startDateTime)) {
        throw new IllegalArgumentException("End date/time cannot be before start date/time");
      }
      
      return new DateTimeRange(startDateTime, endDateTime);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date/time format. Use ISO-8601 format");
    }
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
    response.setStatus(status);
    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(CHARACTER_ENCODING);
    objectMapper.writeValue(response.getOutputStream(),
      new ErrorDTO(message, System.currentTimeMillis()));
  }
}