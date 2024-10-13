package com.mkhabibullin.auth;

import com.mkhabibullin.auth.data.UserRepository;
import com.mkhabibullin.auth.model.User;
import com.mkhabibullin.auth.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  private UserService userService;
  private AutoCloseable closeable;
  
  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    userService = new UserService(userRepository);
  }
  
  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }
  
  @Test
  void registerUserShouldCreateNewUser() throws Exception {
    String email = "test@example.com";
    String password = "password123";
    String name = "Test User";
    userService.registerUser(email, password, name);
    verify(userRepository).createUser(argThat(user ->
      user.getEmail().equals(email) &&
      user.getName().equals(name) &&
      user.getPasswordHash() != null &&
      user.getSalt() != null
    ));
  }
  
  @Test
  void authenticateUserWithValidCredentialsShouldReturnTrue() throws Exception {
    String email = "test@example.com";
    String password = "password123";
    User user = new User(email, "Test User");
    user.setPassword(password);
    when(userRepository.readUserByEmail(email)).thenReturn(user);
    boolean result = userService.authenticateUser(email, password);
    assertThat(result).isTrue();
  }
  
  @Test
  void authenticateUserWithInvalidCredentialsShouldReturnFalse() throws Exception {
    String email = "test@example.com";
    String password = "password123";
    String wrongPassword = "wrongPassword";
    User user = new User(email, "Test User");
    user.setPassword(password);
    when(userRepository.readUserByEmail(email)).thenReturn(user);
    boolean result = userService.authenticateUser(email, wrongPassword);
    assertThat(result).isFalse();
  }
  
  @Test
  void deleteUserAccountShouldCallDeleteUserInRepository() throws Exception {
    String email = "test@example.com";
    userService.deleteUserAccount(email);
    verify(userRepository).deleteUser(email);
  }
  
  @Test
  void registerUserWithInvalidEmailShouldThrowIllegalArgumentException() {
    String invalidEmail = "invalid.email";
    String password = "password123";
    String name = "Test User";
    assertThatThrownBy(() -> userService.registerUser(invalidEmail, password, name))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Invalid email format");
  }
}