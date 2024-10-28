package com.mkhabibullin.app;

import com.mkhabibullin.app.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
  
  @Test
  @DisplayName("setPassword should hash password and generate salt")
  void setPasswordShouldHashPasswordAndGenerateSalt() {
    User user = new User("test@example.com", "Test User");
    user.setPassword("password123");
    assertThat(user.getPasswordHash())
      .isNotNull()
      .isNotEmpty()
      .isNotEqualTo("password123");
    assertThat(user.getSalt())
      .isNotNull()
      .isNotEmpty();
  }
  
  @Test
  @DisplayName("setPassword for different users with same password should result in different hashes and salts")
  void setPasswordDifferentUsersWithSamePasswordShouldHaveDifferentHashesAndSalts() {
    User user1 = new User("user1@example.com", "User One");
    User user2 = new User("user2@example.com", "User Two");
    user1.setPassword("samePassword");
    user2.setPassword("samePassword");
    assertThat(user1.getPasswordHash())
      .isNotEqualTo(user2.getPasswordHash());
    assertThat(user1.getSalt())
      .isNotEqualTo(user2.getSalt());
  }
}