package com.mkhabibullin.app;

import com.mkhabibullin.app.controller.UserController;
import com.mkhabibullin.app.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class UserControllerTest {
  @Mock
  private UserService userService;
  private UserController userController;
  private AutoCloseable closeable;
  
  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    userController = new UserController(userService);
  }
  
  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }
  
  @Test
  @DisplayName("registerUser should call UserService")
  void registerUserShouldCallUserService() throws Exception {
    String email = "test@example.com";
    String password = "password123";
    String name = "Test User";
    userController.registerUser(email, password, name);
    verify(userService).registerUser(email, password, name);
  }
  
  @Test
  @DisplayName("registerUser should throw IllegalArgumentException when UserService throws exception")
  void registerUserWhenUserServiceThrowsExceptionShouldThrowIllegalArgumentException() throws Exception {
    String email = "test@example.com";
    String password = "password123";
    String name = "Test User";
    doThrow(new IllegalArgumentException("User already exists")).when(userService).registerUser(email, password, name);
    assertThatThrownBy(() -> userController.registerUser(email, password, name))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("User already exists");
  }
}