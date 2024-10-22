package com.mkhabibullin.app.data;

import com.mkhabibullin.app.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing User entities in the database.
 * Provides CRUD operations and user authentication-related functionality.
 * All database operations are performed on the 'entity.users' table.
 */
public class UserDbRepository {
  private final DataSource dataSource;
  
  /**
   * Constructs a new UserDbRepository with the specified data source.
   *
   * @param dataSource The data source to be used for database connections
   */
  public UserDbRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  
  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
  
  /**
   * Retrieves all users from the database.
   *
   * @return List of all users in the database
   */
  public List<User> getAllUsers() {
    String sql = "SELECT * FROM entity.users";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
      
      List<User> users = new ArrayList<>();
      while (rs.next()) {
        users.add(mapResultSetToUser(rs));
      }
      return users;
    } catch (SQLException e) {
      System.out.println("Error retrieving all users: \n " + e.getMessage());
    }
    return new ArrayList<>();
  }
  
  /**
   * Creates a new user in the database.
   * The user's ID will be set after successful creation.
   *
   * @param user The user object to be persisted
   */
  public void createUser(User user) {
    String sql = "INSERT INTO entity.users (email, password_hash, salt, name, is_admin, is_blocked) " +
                 "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, user.getEmail());
      pstmt.setString(2, user.getPasswordHash());
      pstmt.setString(3, user.getSalt());
      pstmt.setString(4, user.getName());
      pstmt.setBoolean(5, user.isAdmin());
      pstmt.setBoolean(6, user.isBlocked());
      
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          user.setId(rs.getLong("id")); // Set the generated ID back to the user object
        }
      }
    } catch (SQLException e) {
      if (e.getSQLState().equals("23505")) { // PostgreSQL unique violation code
        System.out.println("User with this email already exists");
      }
      System.out.println("Error creating user: \n " + e.getMessage());
    }
  }
  
  /**
   * Retrieves a user by their ID.
   *
   * @param id The ID of the user to retrieve
   * @return The user object if found, null otherwise
   */
  public User readUserById(Long id) {
    String sql = "SELECT * FROM entity.users WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, id);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToUser(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      System.out.println("Error reading user by ID: \n " + e.getMessage());
    }
    return null;
  }
  
  /**
   * Retrieves a user by their email address.
   *
   * @param email The email address of the user to retrieve
   * @return The user object if found, null otherwise
   */
  public User readUserByEmail(String email) {
    String sql = "SELECT * FROM entity.users WHERE email = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToUser(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      System.out.println("Error reading user by email: \n " + e.getMessage());
    }
    return null;
  }
  
  /**
   * Updates an existing user's information in the database.
   *
   * @param updatedUser The user object with updated values
   */
  public void updateUser(User updatedUser) {
    String sql = "UPDATE entity.users " +
                 "SET email = ?, password_hash = ?, salt = ?, name = ?, is_admin = ?, is_blocked = ? " +
                 "WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, updatedUser.getEmail());
      pstmt.setString(2, updatedUser.getPasswordHash());
      pstmt.setString(3, updatedUser.getSalt());
      pstmt.setString(4, updatedUser.getName());
      pstmt.setBoolean(5, updatedUser.isAdmin());
      pstmt.setBoolean(6, updatedUser.isBlocked());
      pstmt.setLong(7, updatedUser.getId());
      
      int rowsAffected = pstmt.executeUpdate();
      if (rowsAffected == 0) {
        throw new RuntimeException("User not found with ID: " + updatedUser.getId());
      }
    } catch (SQLException e) {
      if (e.getSQLState().equals("23505")) {
        System.out.println("Email address already in use");
      }
      System.out.println("Error updating user: \n" + e.getMessage());
      ;
    }
  }
  
  /**
   * Deletes a user from the database by their email address.
   *
   * @param email The email address of the user to delete
   */
  public void deleteUser(String email) {
    String sql = "DELETE FROM entity.users WHERE email = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, email);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error deleting user: \n " + e.getMessage());
    }
  }
  
  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    User user = new User(
      rs.getString("email"),
      rs.getString("name")
    );
    user.setId(rs.getLong("id"));
    user.setPasswordHash(rs.getString("password_hash"));
    user.setSalt(rs.getString("salt"));
    user.setAdmin(rs.getBoolean("is_admin"));
    user.setBlocked(rs.getBoolean("is_blocked"));
    return user;
  }
}
