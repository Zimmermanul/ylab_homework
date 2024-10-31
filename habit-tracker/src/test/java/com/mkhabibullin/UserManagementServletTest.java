package com.mkhabibullin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.application.service.UserService;
import com.mkhabibullin.aspect.AspectContext;
import com.mkhabibullin.aspect.TestAuditedServletAspect;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.UserDbRepository;
import com.mkhabibullin.presentation.controller.UserController;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import com.mkhabibullin.presentation.dto.user.LoginDTO;
import com.mkhabibullin.presentation.dto.user.RegisterUserDTO;
import com.mkhabibullin.presentation.dto.user.UpdateNameDTO;
import com.mkhabibullin.presentation.dto.user.UserEmailDTO;
import com.mkhabibullin.presentation.servlet.UserManagementServlet;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserManagementServletTest extends AbstractDatabaseTest {
  
  private UserManagementServlet servlet;
  private UserController userController;
  private UserDbRepository userRepository;
  private UserService userService;
  private AuditLogDbRepository auditLogRepository;
  private ObjectMapper objectMapper;
  private User testUser;
  private User adminUser;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private HttpSession session;
  private ByteArrayOutputStream outputStream;
  
  @BeforeEach
  void init() throws Exception {
    super.setUp();
    AspectContext.setTestContext(dataSource);
    TestAuditedServletAspect.setTestDataSource(dataSource);
    userRepository = new UserDbRepository(dataSource);
    auditLogRepository = new AuditLogDbRepository(dataSource);
    userService = new UserService(userRepository);
    userController = new UserController(userService);
    servlet = new UserManagementServlet(userController);
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    testUser = new User("test@example.com", "Test User");
    testUser.setPassword("password123");
    userRepository.createUser(testUser);
    adminUser = new User("admin@example.com", "Admin User");
    adminUser.setPassword("adminpass");
    adminUser.setAdmin(true);
    userRepository.createUser(adminUser);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    session = mock(HttpSession.class);
    outputStream = new ByteArrayOutputStream();
    when(response.getOutputStream()).thenReturn(ServletTestUtils.createServletOutputStream(outputStream));
    when(request.getSession()).thenReturn(session);
    when(request.getSession(false)).thenReturn(session);
    when(request.getSession(true)).thenReturn(session);
    when(request.getContextPath()).thenReturn("");
    when(request.getServletPath()).thenReturn("/api/users");
  }
  
  @AfterEach
  void tearDown() {
    AspectContext.clearTestContext();
  }
  
  private String getResponseContent() {
    return outputStream.toString();
  }
  
  @Test
  @DisplayName("Should create audit log when registering a new user")
  void shouldCreateAuditLogWhenRegisteringUser() throws Exception {
    RegisterUserDTO registerDTO = new RegisterUserDTO(
      "newuser@example.com",
      "password123",
      "New User"
    );
    when(request.getPathInfo()).thenReturn("/");
    when(request.getRequestURI()).thenReturn("/api/users/");
    when(request.getMethod()).thenReturn("POST");
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(registerDTO)))
    );
    servlet.doPost(request, response);
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT username, request_uri, request_method FROM audit.audit_logs WHERE operation = 'User Management - registration, login, logout'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo("anonymous"); // Pre-registration, user is anonymous
      assertThat(rs.getString("request_uri")).isEqualTo("/api/users/");
      assertThat(rs.getString("request_method")).isEqualTo("POST");
    }
  }
  
  @Test
  @DisplayName("Should create audit log when user logs in")
  void shouldCreateAuditLogWhenUserLogsIn() throws Exception {
    LoginDTO loginDTO = new LoginDTO("test@example.com", "password123");
    when(request.getPathInfo()).thenReturn("/login");
    when(request.getRequestURI()).thenReturn("/api/users/login");
    when(request.getMethod()).thenReturn("POST");
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(loginDTO)))
    );
    servlet.doPost(request, response);
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT username, request_uri, request_method FROM audit.audit_logs WHERE operation = 'User Management - registration, login, logout'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo("anonymous"); // Pre-login, user is anonymous
      assertThat(rs.getString("request_uri")).isEqualTo("/api/users/login");
      assertThat(rs.getString("request_method")).isEqualTo("POST");
    }
  }
  
  @Test
  @DisplayName("Should create audit log when updating user profile")
  void shouldCreateAuditLogWhenUpdatingProfile() throws Exception {
    when(session.getAttribute("user")).thenReturn(testUser);
    UpdateNameDTO updateDTO = new UpdateNameDTO("Updated Name");
    when(request.getPathInfo()).thenReturn("/name");
    when(request.getRequestURI()).thenReturn("/api/users/name");
    when(request.getMethod()).thenReturn("PUT");
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(updateDTO)))
    );
    servlet.doPut(request, response);
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT username, request_uri, request_method FROM audit.audit_logs WHERE operation = 'User Profile Update'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo("Test User"); // Original name in audit log
      assertThat(rs.getString("request_uri")).isEqualTo("/api/users/name");
      assertThat(rs.getString("request_method")).isEqualTo("PUT");
    }
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT name FROM entity.users WHERE email = ?"
         )) {
      stmt.setString(1, testUser.getEmail());
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("name")).isEqualTo("Updated Name");
    }
  }
  
  @Test
  @DisplayName("Should create audit log when admin blocks user")
  void shouldCreateAuditLogWhenBlockingUser() throws Exception {
    when(session.getAttribute("user")).thenReturn(adminUser);
    UserEmailDTO blockDTO = new UserEmailDTO("test@example.com");
    when(request.getPathInfo()).thenReturn("/block");
    when(request.getRequestURI()).thenReturn("/api/users/block");
    when(request.getMethod()).thenReturn("PUT");
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(blockDTO)))
    );
    servlet.doPut(request, response);
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
           "SELECT username, request_uri, request_method FROM audit.audit_logs WHERE operation = 'User Profile Update'"
         )) {
      ResultSet rs = stmt.executeQuery();
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString("username")).isEqualTo(adminUser.getName());
      assertThat(rs.getString("request_uri")).isEqualTo("/api/users/block");
      assertThat(rs.getString("request_method")).isEqualTo("PUT");
    }
  }
  
  @Test
  @DisplayName("Should return error when non-admin tries to view all users")
  void shouldReturnErrorWhenNonAdminViewsUsers() throws Exception {
    when(session.getAttribute("user")).thenReturn(testUser);
    when(request.getPathInfo()).thenReturn("/");
    servlet.doGet(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("Admin privileges required");
  }
  
  @Test
  @DisplayName("Should return error for invalid registration data")
  void shouldReturnErrorForInvalidRegistration() throws Exception {
    RegisterUserDTO invalidDTO = new RegisterUserDTO(
      "",
      "password123",
      "New User"
    );
    when(request.getPathInfo()).thenReturn("/");
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(invalidDTO)))
    );
    servlet.doPost(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("Email is required");
  }
  
  @Test
  @DisplayName("Should return error for invalid login credentials")
  void shouldReturnErrorForInvalidLogin() throws Exception {
    LoginDTO invalidDTO = new LoginDTO("nonexistent@example.com", "wrongpassword");
    when(request.getPathInfo()).thenReturn("/login");
    when(request.getReader()).thenReturn(
      new BufferedReader(new StringReader(objectMapper.writeValueAsString(invalidDTO)))
    );
    servlet.doPost(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("Invalid credentials");
  }
  
  @Test
  @DisplayName("Should successfully logout user")
  void shouldSuccessfullyLogoutUser() throws Exception {
    HttpSession session = mock(HttpSession.class);
    when(request.getSession(false)).thenReturn(session);
    when(request.getPathInfo()).thenReturn("/logout");
    servlet.doPost(request, response);
    verify(session).invalidate();
    verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }
  
  @Test
  @DisplayName("Should return error when updating profile without authentication")
  void shouldReturnErrorWhenUpdatingWithoutAuth() throws Exception {
    when(session.getAttribute("user")).thenReturn(null);
    when(request.getPathInfo()).thenReturn("/name");
    servlet.doPut(request, response);
    ErrorDTO error = objectMapper.readValue(getResponseContent(), ErrorDTO.class);
    assertThat(error.message()).isEqualTo("User not authenticated");
  }
}