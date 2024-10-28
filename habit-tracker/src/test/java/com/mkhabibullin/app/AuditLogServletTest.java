package com.mkhabibullin.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.app.application.service.AuditLogService;
import com.mkhabibullin.app.domain.model.User;
import com.mkhabibullin.app.infrastructure.persistence.repository.AuditLogDbRepository;
import com.mkhabibullin.app.infrastructure.persistence.repository.UserDbRepository;
import com.mkhabibullin.app.presentation.controller.AuditLogController;
import com.mkhabibullin.app.presentation.dto.ErrorDTO;
import com.mkhabibullin.app.presentation.dto.audit.AuditStatisticsDTO;
import com.mkhabibullin.app.presentation.servlet.AuditLogServlet;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditLogServletTest extends AbstractDatabaseTest {
  private AuditLogServlet servlet;
  private AuditLogController auditLogController;
  private AuditLogService auditLogService;
  private AuditLogDbRepository auditLogRepository;
  private UserDbRepository userRepository;
  private ObjectMapper objectMapper;
  private User testUser;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private HttpSession session;
  private ByteArrayOutputStream outputStream;
  
  @BeforeEach
  void init() throws Exception {
    super.setUp();
    auditLogRepository = new AuditLogDbRepository(dataSource);
    userRepository = new UserDbRepository(dataSource);
    auditLogService = new AuditLogService(auditLogRepository);
    auditLogController = new AuditLogController(auditLogService);
    servlet = new AuditLogServlet(auditLogController);
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    testUser = new User("test@example.com", "Test User");
    testUser.setPasswordHash("hash");
    testUser.setSalt("salt");
    userRepository.createUser(testUser);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    session = mock(HttpSession.class);
    outputStream = new ByteArrayOutputStream();
    ServletOutputStream servletOutputStream = new ServletOutputStream() {
      @Override
      public boolean isReady() {
        return true;
      }
      
      @Override
      public void setWriteListener(WriteListener writeListener) {
      }
      
      @Override
      public void write(int b) throws IOException {
        outputStream.write(b);
      }
    };
    when(response.getOutputStream()).thenReturn(servletOutputStream);
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute("user")).thenReturn(testUser);
  }
  
  private String getResponseContent() {
    return outputStream.toString();
  }
  
  @Test
  @DisplayName("Should return user's audit logs when requesting user logs")
  void shouldReturnUserAuditLogs() throws Exception {
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      stmt.execute("""
            INSERT INTO audit.audit_logs (username, method_name, operation, timestamp, execution_time_ms, request_uri, request_method)
            VALUES
            ('test@example.com', 'handleLogin', 'User Login', NOW(), 100, '/api/users/login', 'POST'),
            ('test@example.com', 'handleLogout', 'User Logout', NOW(), 50, '/api/users/logout', 'POST')
        """);
    }
    when(request.getPathInfo()).thenReturn("/user/" + testUser.getEmail());
    servlet.doGet(request, response);
    List<?> logs = objectMapper.readValue(getResponseContent(), List.class);
    assertThat(logs).hasSize(2);
  }
  
  @Test
  @DisplayName("Should return audit statistics for specified date range")
  void shouldReturnAuditStatistics() throws Exception {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startDateTime = now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
    LocalDateTime endDateTime = now.minusDays(1).withHour(23).withMinute(59).withSecond(59);
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      stmt.execute(String.format("""
              INSERT INTO audit.audit_logs (username, method_name, operation, timestamp, execution_time_ms, request_uri, request_method)
              VALUES
              ('test@example.com', 'handleLogin', 'User Login', '%s', 100, '/api/users/login', 'POST'),
              ('test@example.com', 'handleCreate', 'Create Habit', '%s', 150, '/api/habits', 'POST'),
              ('other@example.com', 'handleLogin', 'User Login', '%s', 120, '/api/users/login', 'POST')
          """,
        startDateTime.plusHours(1),
        startDateTime.plusHours(2),
        startDateTime.plusHours(3)
      ));
    }
    when(request.getPathInfo()).thenReturn("/statistics");
    when(request.getParameter("startDateTime")).thenReturn(startDateTime.toString());
    when(request.getParameter("endDateTime")).thenReturn(endDateTime.toString());
    servlet.doGet(request, response);
    AuditStatisticsDTO stats = objectMapper.readValue(outputStream.toString(), AuditStatisticsDTO.class);
    assertThat(stats)
      .isNotNull()
      .satisfies(s -> {
        assertThat(s.totalOperations()).as("Should have 3 operations in total").isEqualTo(3);
        assertThat(s.mostActiveUser()).as("Most active user should be test@example.com").isEqualTo("test@example.com");
        assertThat(s.operationCounts())
          .as("Operation counts should include User Login")
          .containsKey("User Login");
        assertThat(s.operationCounts().get("User Login"))
          .as("Should have 2 User Login operations")
          .isEqualTo(2L);
        assertThat(s.averageExecutionTime())
          .as("Average execution time should be between 100 and 150 ms")
          .isBetween(100.0, 150.0);
      });
  }
  
  
  @Test
  @DisplayName("Should return recent logs with specified limit")
  void shouldReturnRecentLogs() throws Exception {
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      stmt.execute("""
            INSERT INTO audit.audit_logs (username, method_name, operation, timestamp, execution_time_ms, request_uri, request_method)
            VALUES
            ('test@example.com', 'handleLogin', 'User Login', NOW(), 100, '/api/users/login', 'POST'),
            ('test@example.com', 'handleCreate', 'Create Habit', NOW(), 150, '/api/habits', 'POST'),
            ('test@example.com', 'handleLogout', 'User Logout', NOW(), 80, '/api/users/logout', 'POST')
        """);
    }
    when(request.getPathInfo()).thenReturn("/recent");
    when(request.getParameter("limit")).thenReturn("2");
    servlet.doGet(request, response);
    List<?> logs = objectMapper.readValue(outputStream.toString(), List.class);
    assertThat(logs).hasSize(2);
  }
  
  @Test
  @DisplayName("Should return error when user is not authenticated")
  void shouldReturnErrorWhenUserNotAuthenticated() throws Exception {
    when(session.getAttribute("user")).thenReturn(null);
    when(request.getPathInfo()).thenReturn("/recent");
    servlet.doGet(request, response);
    ErrorDTO error = objectMapper.readValue(outputStream.toString(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("User not authenticated");
  }
  
  @Test
  @DisplayName("Should return error when date range is invalid")
  void shouldReturnErrorWhenDateRangeInvalid() throws Exception {
    when(request.getPathInfo()).thenReturn("/statistics");
    when(request.getParameter("startDateTime")).thenReturn("2024-10-28T23:59:59");
    when(request.getParameter("endDateTime")).thenReturn("2024-10-28T00:00:00");
    servlet.doGet(request, response);
    ErrorDTO error = objectMapper.readValue(outputStream.toString(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("End date/time cannot be before start date/time");
  }
  
  @Test
  @DisplayName("Should return logs filtered by operation")
  void shouldReturnOperationLogs() throws Exception {
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      stmt.execute("""
            INSERT INTO audit.audit_logs (username, method_name, operation, timestamp, execution_time_ms, request_uri, request_method)
            VALUES
            ('test@example.com', 'handleLogin', 'User Login', NOW(), 100, '/api/users/login', 'POST'),
            ('other@example.com', 'handleLogin', 'User Login', NOW(), 120, '/api/users/login', 'POST'),
            ('test@example.com', 'handleCreate', 'Create Habit', NOW(), 150, '/api/habits', 'POST')
        """);
    }
    when(request.getPathInfo()).thenReturn("/operation/User Login");
    servlet.doGet(request, response);
    List<?> logs = objectMapper.readValue(outputStream.toString(), List.class);
    assertThat(logs).hasSize(2);
  }
}