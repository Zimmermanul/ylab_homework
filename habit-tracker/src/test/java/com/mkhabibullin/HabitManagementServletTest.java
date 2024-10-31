package com.mkhabibullin;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.application.service.HabitService;
import com.mkhabibullin.aspect.AspectContext;
import com.mkhabibullin.aspect.TestAuditedServletAspect;
import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.HabitDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.UserDbRepository;
import com.mkhabibullin.presentation.controller.HabitController;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import com.mkhabibullin.presentation.dto.MessageDTO;
import com.mkhabibullin.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.presentation.dto.habit.UpdateHabitDTO;
import com.mkhabibullin.presentation.servlet.HabitManagementServlet;
import com.mkhabibullin.util.ServletTestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
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

public class HabitManagementServletTest extends AbstractDatabaseTest {
  
  private HabitManagementServlet servlet;
  private HabitDbRepository habitDbRepository;
  private HabitService habitService;
  private HabitController habitController;
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
    AspectContext.setTestContext(dataSource);
    TestAuditedServletAspect.setTestDataSource(dataSource);
    auditLogRepository = new AuditLogDbRepository(dataSource);
    userRepository = new UserDbRepository(dataSource);
    habitDbRepository = new HabitDbRepository(dataSource);
    habitService = new HabitService(habitDbRepository, userRepository);
    habitController = new HabitController(habitService);
    servlet = new HabitManagementServlet(habitController);
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
  
  @AfterEach
  void tearDown() {
    AspectContext.clearTestContext();
  }
  
  private String getResponseContent() {
    return outputStream.toString();
  }
  
  @Test
  @DisplayName("Should create audit log when creating a habit")
  void shouldCreateAuditLogWhenCreatingHabit() throws Exception {
    CreateHabitDTO createDTO = new CreateHabitDTO(
      "Test Habit",
      "Test Description",
      Habit.Frequency.DAILY
    );
    when(request.getPathInfo()).thenReturn("/");
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(createDTO)))
    );
    servlet.doPost(request, response);
    MessageDTO message = objectMapper.readValue(getResponseContent(), MessageDTO.class);
    assertThat(message.message()).isEqualTo("Habit created successfully");
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT * FROM audit.audit_logs WHERE operation = 'Create Habit'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo(testUser.getName());
      assertThat(rs.getLong("execution_time_ms")).isGreaterThanOrEqualTo(0);
    }
  }
  
  @Test
  @DisplayName("Should create audit log when viewing habits")
  void shouldCreateAuditLogWhenViewingHabits() throws Exception {
    when(request.getPathInfo()).thenReturn("/");
    when(request.getParameter("date")).thenReturn(LocalDate.now().toString());
    when(request.getParameter("active")).thenReturn("true");
    servlet.doGet(request, response);
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT * FROM audit.audit_logs WHERE operation = 'View Habits'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo(testUser.getName());
      assertThat(rs.getString("operation")).isEqualTo("View Habits");
    }
  }
  
  @Test
  @DisplayName("Should create audit log when updating a habit")
  void shouldCreateAuditLogWhenUpdatingHabit() throws Exception {
    UpdateHabitDTO updateDTO = new UpdateHabitDTO(
      "Updated Habit",
      "Updated Description",
      Habit.Frequency.WEEKLY
    );
    when(request.getPathInfo()).thenReturn("/1");
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(updateDTO)))
    );
    servlet.doPut(request, response);
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT * FROM audit.audit_logs WHERE operation = 'Update Habit'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo(testUser.getName());
      assertThat(rs.getLong("execution_time_ms")).isGreaterThanOrEqualTo(0);
    }
  }
  
  @Test
  @DisplayName("Should create audit log when deleting a habit")
  void shouldCreateAuditLogWhenDeletingHabit() throws Exception {
    when(request.getPathInfo()).thenReturn("/1");
    servlet.doDelete(request, response);
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT * FROM audit.audit_logs WHERE operation = 'Delete Habit'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo(testUser.getName());
      assertThat(rs.getString("operation")).isEqualTo("Delete Habit");
    }
  }
  
  @Test
  @DisplayName("Should return error when user is not authenticated")
  void shouldReturnErrorWhenUserNotAuthenticated() throws Exception {
    when(session.getAttribute("user")).thenReturn(null);
    when(request.getPathInfo()).thenReturn("/");
    servlet.doGet(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("User not authenticated");
  }
  
  @Test
  @DisplayName("Should return error for invalid habit ID")
  void shouldReturnErrorForInvalidHabitId() throws Exception {
    when(request.getPathInfo()).thenReturn("/invalid");
    servlet.doPut(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("Invalid habit ID format");
  }
  
  @Test
  @DisplayName("Should return error for invalid path info")
  void shouldReturnErrorForInvalidPathInfo() throws Exception {
    when(request.getPathInfo()).thenReturn("/invalid/path");
    servlet.doPost(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("Invalid endpoint");
  }
  
  @Test
  @DisplayName("Should return correct error mesage for empty habit name")
  void shouldReturnErrorForInvalidHabitCreationData() throws Exception {
    CreateHabitDTO invalidDTO = new CreateHabitDTO(
      "",
      "Description",
      Habit.Frequency.DAILY
    );
    when(request.getPathInfo()).thenReturn("/");
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(invalidDTO)))
    );
    servlet.doPost(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).contains("Habit name is required");
  }
}