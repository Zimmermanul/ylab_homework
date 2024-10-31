package com.mkhabibullin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.application.service.HabitExecutionService;
import com.mkhabibullin.aspect.TestAuditedServletAspect;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.HabitDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.HabitExecutionDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.UserDbRepository;
import com.mkhabibullin.presentation.controller.HabitExecutionController;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import com.mkhabibullin.presentation.dto.MessageDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionRequestDTO;
import com.mkhabibullin.presentation.servlet.HabitExecutionServlet;
import com.mkhabibullin.util.ServletTestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HabitExecutionServletTest extends AbstractDatabaseTest {
  
  private HabitExecutionServlet servlet;
  private HabitExecutionDbRepository habitExecutionDbRepository;
  private HabitDbRepository habitDbRepository;
  private HabitExecutionController executionController;
  private HabitExecutionService executionService;
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
    TestAuditedServletAspect.setTestDataSource(dataSource);
    auditLogRepository = new AuditLogDbRepository(dataSource);
    userRepository = new UserDbRepository(dataSource);
    habitDbRepository = new HabitDbRepository(dataSource);
    habitExecutionDbRepository = new HabitExecutionDbRepository(dataSource);
    executionService = new HabitExecutionService(habitExecutionDbRepository, habitDbRepository);
    executionController = new HabitExecutionController(executionService);
    servlet = new HabitExecutionServlet(executionController);
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
    when(response.getOutputStream()).thenReturn(ServletTestUtils.createServletOutputStream(outputStream));
    when(request.getSession()).thenReturn(session);
    when(request.getSession(false)).thenReturn(session);
    when(request.getSession(true)).thenReturn(session);
    when(session.getAttribute("user")).thenReturn(testUser);
  }
  
  private String getResponseContent() {
    return outputStream.toString();
  }
  
  @Test
  @DisplayName("Should create audit log when tracking habit execution")
  void shouldCreateAuditLogWhenTrackingHabit() throws Exception {
    long habitId = 1L;
    HabitExecutionRequestDTO executionDTO = new HabitExecutionRequestDTO(
      LocalDate.now(),
      true
    );
    when(request.getPathInfo()).thenReturn("/track/" + habitId);
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(executionDTO)))
    );
    servlet.doPost(request, response);
    MessageDTO message = objectMapper.readValue(getResponseContent(), MessageDTO.class);
    assertThat(message.message()).isEqualTo("Habit execution recorded successfully");
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT * FROM audit.audit_logs WHERE operation = 'Track Habit Execution'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo(testUser.getName());
      assertThat(rs.getLong("execution_time_ms")).isGreaterThanOrEqualTo(0);
    }
  }
  
  @Test
  @DisplayName("Should return error when user is not authenticated")
  void shouldReturnErrorWhenUserNotAuthenticated() throws Exception {
    when(session.getAttribute("user")).thenReturn(null);
    when(request.getPathInfo()).thenReturn("/history/1");
    servlet.doGet(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("User not authenticated");
  }
  
  @Test
  @DisplayName("Should get habit execution statistics with audit log")
  void shouldGetHabitExecutionStatistics() throws Exception {
    long habitId = 1L;
    LocalDate startDate = LocalDate.now().minusDays(7);
    LocalDate endDate = LocalDate.now();
    when(request.getPathInfo()).thenReturn("/statistics/" + habitId);
    when(request.getParameter("startDate")).thenReturn(startDate.toString());
    when(request.getParameter("endDate")).thenReturn(endDate.toString());
    servlet.doGet(request, response);
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT * FROM audit.audit_logs WHERE operation = 'Get Progress Report'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo(testUser.getName());
      assertThat(rs.getString("operation")).isEqualTo("Get Progress Report");
    }
  }
  
  @Test
  @DisplayName("Should return error when date range is invalid")
  void shouldReturnErrorWhenDateRangeInvalid() throws Exception {
    when(request.getPathInfo()).thenReturn("/statistics/1");
    when(request.getParameter("startDate")).thenReturn(LocalDate.now().toString());
    when(request.getParameter("endDate")).thenReturn(LocalDate.now().minusDays(1).toString());
    servlet.doGet(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("End date cannot be before start date");
  }
  
  @Test
  @DisplayName("Should return error for invalid habit ID")
  void shouldReturnErrorForInvalidHabitId() throws Exception {
    when(request.getPathInfo()).thenReturn("/history/invalid");
    servlet.doGet(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("Invalid habit ID format");
  }
}