package com.mkhabibullin.auth;

import com.mkhabibullin.auth.controller.UserController;
import com.mkhabibullin.auth.data.UserRepository;
import com.mkhabibullin.auth.model.User;
import com.mkhabibullin.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserTest {
  
  @Test
  void setPasswordShouldHashPasswordAndGenerateSalt() {
    User user = new User("test@example.com", "Test User");
    user.setPassword("password123");
    assertThat(user.getPasswordHash()).isNotNull().isNotEmpty();
    assertThat(user.getSalt()).isNotNull().isNotEmpty();
    assertThat(user.getPasswordHash()).isNotEqualTo("password123");
  }
  
  @Test
  void setPasswordDifferentUsersWithSamePasswordShouldHaveDifferentHashesAndSalts() {
    User user1 = new User("user1@example.com", "User One");
    User user2 = new User("user2@example.com", "User Two");
    user1.setPassword("samePassword");
    user2.setPassword("samePassword");
    assertThat(user1.getPasswordHash()).isNotEqualTo(user2.getPasswordHash());
    assertThat(user1.getSalt()).isNotEqualTo(user2.getSalt());
  }
}