package com.mkhabibullin.infrastructure.persistence.repository;

import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.infrastructure.persistence.queries.HabitRepositoryQueries;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing Habit entities in the database.
 * Provides CRUD (Create, Read, Update, Delete) operations for habits using JDBC.
 * All database operations are performed on the 'entity.habits' table.
 */
public class HabitDbRepository {
  private final DataSource dataSource;
  
  /**
   * Constructs a new HabitDbRepository with the specified data source.
   *
   * @param dataSource The data source to be used for database connections
   */
  public HabitDbRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  
  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
  
  /**
   * Creates a new habit record in the database.
   * The habit's ID will be set after successful creation.
   *
   * @param habit The habit object to be persisted
   */
  public void create(Habit habit) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitRepositoryQueries.CREATE_HABIT)) {
      pstmt.setLong(1, habit.getUserId());
      pstmt.setString(2, habit.getName());
      pstmt.setString(3, habit.getDescription());
      pstmt.setString(4, habit.getFrequency().toString());
      pstmt.setDate(5, java.sql.Date.valueOf(habit.getCreationDate()));
      pstmt.setBoolean(6, habit.isActive());
      
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          habit.setId(rs.getLong("id"));
        }
      }
    } catch (SQLException e) {
      System.out.println("Error creating habit: \n " + e.getMessage());
    }
  }
  
  /**
   * Updates an existing habit record in the database.
   *
   * @param habit The habit object with updated values
   */
  public void update(Habit habit) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitRepositoryQueries.UPDATE_HABIT)) {
      pstmt.setString(1, habit.getName());
      pstmt.setString(2, habit.getDescription());
      pstmt.setString(3, habit.getFrequency().toString());
      pstmt.setBoolean(4, habit.isActive());
      pstmt.setLong(5, habit.getId());
      
      int rowsAffected = pstmt.executeUpdate();
      if (rowsAffected == 0) {
        System.out.println("Habit not found with ID: " + habit.getId());
      }
    } catch (SQLException e) {
      System.out.println("Error updating habit: \n " + e.getMessage());
    }
  }
  
  /**
   * Deletes a habit record from the database.
   *
   * @param id The ID of the habit to delete
   */
  public void delete(Long id) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitRepositoryQueries.DELETE_HABIT)) {
      pstmt.setLong(1, id);
      
      int rowsAffected = pstmt.executeUpdate();
      if (rowsAffected == 0) {
        System.out.println("Habit not found with ID: " + id);
      }
    } catch (SQLException e) {
      System.out.println("Error deleting habit: \n " + e.getMessage());
    }
  }
  
  /**
   * Retrieves all habit records from the database.
   *
   * @return List of all habits in the database
   */
  public List<Habit> readAll() {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitRepositoryQueries.READ_ALL_HABITS);
         ResultSet rs = pstmt.executeQuery()) {
      
      List<Habit> habits = new ArrayList<>();
      while (rs.next()) {
        habits.add(mapResultSetToHabit(rs));
      }
      return habits;
    } catch (SQLException e) {
      System.out.println("Error reading habit: \n " + e.getMessage());
    }
    return new ArrayList<>();
  }
  
  /**
   * Retrieves all habits associated with a specific user.
   *
   * @param userId The ID of the user whose habits to retrieve
   * @return List of habits belonging to the specified user
   */
  public List<Habit> getByUserId(Long userId) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitRepositoryQueries.GET_HABITS_BY_USER_ID)) {
      pstmt.setLong(1, userId);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<Habit> habits = new ArrayList<>();
        while (rs.next()) {
          habits.add(mapResultSetToHabit(rs));
        }
        return habits;
      }
    } catch (SQLException e) {
      System.out.println("Error getting habits by user ID: \n " + e.getMessage());
    }
    return new ArrayList<>();
  }
  
  /**
   * Retrieves a specific habit by its ID.
   *
   * @param id The ID of the habit to retrieve
   * @return The habit object if found, null otherwise
   */
  public Habit getById(Long id) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitRepositoryQueries.GET_HABIT_BY_ID)) {
      pstmt.setLong(1, id);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToHabit(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      System.out.println("Error getting habit by ID: \n " + e.getMessage());
    }
    return null;
  }
  
  private Habit mapResultSetToHabit(ResultSet rs) throws SQLException {
    Habit habit = new Habit();
    habit.setId(rs.getLong("id"));
    habit.setUserId(rs.getLong("user_id"));
    habit.setName(rs.getString("name"));
    habit.setDescription(rs.getString("description"));
    habit.setFrequency(Habit.Frequency.valueOf(rs.getString("frequency")));
    habit.setCreationDate(rs.getDate("creation_date").toLocalDate());
    habit.setActive(rs.getBoolean("is_active"));
    return habit;
  }
}