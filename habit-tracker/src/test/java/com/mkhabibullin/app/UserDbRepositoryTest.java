package com.mkhabibullin.app;

import com.mkhabibullin.app.domain.model.User;
import com.mkhabibullin.app.infrastructure.persistence.repository.UserDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserDbRepositoryTest extends AbstractDatabaseTest {
  private UserDbRepository repository;
  
  @BeforeEach
  void init() {
    repository = new UserDbRepository(dataSource);
  }
  
  @Test
  @DisplayName("Should create new user with automatically generated ID")
  void shouldCreateUserWithGeneratedId() {
    User user = new User("test@example.com", "Test User");
    user.setPasswordHash("hash");
    user.setSalt("salt");
    repository.createUser(user);
    assertThat(user.getId()).isNotNull();
    assertThat(user.getId()).isGreaterThanOrEqualTo(100000L);
  }
  
  @Test
  @DisplayName("Should successfully retrieve user by email")
  void shouldReadUserByEmail() {
    User user = new User("test@example.com", "Test User");
    user.setPasswordHash("hash");
    user.setSalt("salt");
    repository.createUser(user);
    User foundUser = repository.readUserByEmail("test@example.com");
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
    assertThat(foundUser.getName()).isEqualTo("Test User");
  }
  
  @Test
  @DisplayName("Should return null when user email does not exist")
  void shouldReturnNullWhenUserNotFound() {
    User user = repository.readUserByEmail("nonexistent@example.com");
    assertThat(user).isNull();
  }
  
  @Test
  @DisplayName("Should successfully update existing user")
  void shouldUpdateUser() {
    User user = new User("test@example.com", "Test User");
    user.setPasswordHash("hash");
    user.setSalt("salt");
    repository.createUser(user);
    user.setName("Updated Name");
    repository.updateUser(user);
    User updatedUser = repository.readUserByEmail("test@example.com");
    assertThat(updatedUser.getName()).isEqualTo("Updated Name");
  }
  
  @Test
  @DisplayName("Should successfully delete user by email")
  void shouldDeleteUser() {
    User user = new User("test@example.com", "Test User");
    user.setPasswordHash("hash");
    user.setSalt("salt");
    repository.createUser(user);
    repository.deleteUser("test@example.com");
    assertThat(repository.readUserByEmail("test@example.com")).isNull();
  }
  
  @Test
  @DisplayName("Should retrieve all users from database")
  void shouldGetAllUsers() {
    User user1 = new User("test1@example.com", "Test User 1");
    user1.setPasswordHash("hash1");
    user1.setSalt("salt1");
    repository.createUser(user1);
    User user2 = new User("test2@example.com", "Test User 2");
    user2.setPasswordHash("hash2");
    user2.setSalt("salt2");
    repository.createUser(user2);
    List<User> users = repository.getAllUsers();
    assertThat(users).hasSize(2);
    assertThat(users).extracting(User::getEmail)
      .containsExactlyInAnyOrder("test1@example.com", "test2@example.com");
  }
}