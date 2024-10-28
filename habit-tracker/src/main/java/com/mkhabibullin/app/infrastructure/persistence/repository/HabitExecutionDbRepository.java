package com.mkhabibullin.app.infrastructure.persistence.repository;

import com.mkhabibullin.app.domain.model.HabitExecution;
import com.mkhabibullin.app.infrastructure.persistence.queries.HabitExecutionRepositoryQueries;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Repository class for managing HabitExecution entities in the database.
 * Provides operations for tracking habit completions and retrieving execution history.
 * All database operations are performed on the 'entity.habit_executions' table.
 */
public class HabitExecutionDbRepository {
  private final DataSource dataSource;
  
  /**
   * Constructs a new HabitExecutionDbRepository with the specified data source.
   *
   * @param dataSource The data source to be used for database connections
   */
  public HabitExecutionDbRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  
  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
  
  /**
   * Saves a new habit execution record in the database.
   * The execution's ID will be set after successful creation.
   *
   * @param execution The habit execution object to be persisted
   */
  public void save(HabitExecution execution) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitExecutionRepositoryQueries.SAVE_EXECUTION)) {
      pstmt.setLong(1, execution.getHabitId());
      pstmt.setDate(2, java.sql.Date.valueOf(execution.getDate()));
      pstmt.setBoolean(3, execution.isCompleted());
      
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          execution.setId(rs.getLong("id"));
        }
      }
    } catch (SQLException e) {
      System.out.println("Error saving habit execution: \n " + e.getMessage());
    }
  }
  
  /**
   * Retrieves all execution records for a specific habit.
   *
   * @param habitId The ID of the habit whose executions to retrieve
   * @return List of executions for the specified habit, ordered by date
   */
  public List<HabitExecution> getByHabitId(Long habitId) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitExecutionRepositoryQueries.GET_BY_HABIT_ID)) {
      pstmt.setLong(1, habitId);
      try (ResultSet rs = pstmt.executeQuery()) {
        List<HabitExecution> executions = new ArrayList<>();
        while (rs.next()) {
          executions.add(mapResultSetToHabitExecution(rs));
        }
        return executions;
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving habit executions: \n " + e.getMessage());
    }
    return new ArrayList<>();
  }
  
  /**
   * Updates an existing habit execution record in the database.
   *
   * @param execution The habit execution object with updated values
   */
  public void update(HabitExecution execution) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitExecutionRepositoryQueries.UPDATE_EXECUTION)) {
      pstmt.setDate(1, java.sql.Date.valueOf(execution.getDate()));
      pstmt.setBoolean(2, execution.isCompleted());
      pstmt.setLong(3, execution.getId());
      
      int rowsAffected = pstmt.executeUpdate();
      if (rowsAffected == 0) {
        System.out.println("Habit execution not found with ID: " + execution.getId());
      }
    } catch (SQLException e) {
      System.out.println("Error updating habit execution: \n " + e.getMessage());
    }
  }
  
  /**
   * Deletes a habit execution record from the database.
   *
   * @param executionId The ID of the execution record to delete
   */
  public void delete(Long executionId) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitExecutionRepositoryQueries.DELETE_EXECUTION)) {
      pstmt.setLong(1, executionId);
      
      int rowsAffected = pstmt.executeUpdate();
      if (rowsAffected == 0) {
        System.out.println("Habit execution not found with ID: " + executionId);
      }
    } catch (SQLException e) {
      System.out.println("Error deleting habit execution: \n " + e.getMessage());
    }
  }
  
  /**
   * Retrieves a specific habit execution by its ID.
   *
   * @param id The ID of the execution record to retrieve
   * @return The habit execution object if found, null otherwise
   */
  public HabitExecution getById(Long id) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitExecutionRepositoryQueries.GET_BY_ID)) {
      pstmt.setLong(1, id);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToHabitExecution(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      System.out.println("Error getting habit execution by ID: \n " + e.getMessage());
    }
    return null;
  }
  
  /**
   * Retrieves all execution records for a specific habit within a date range.
   *
   * @param habitId The ID of the habit whose executions to retrieve
   * @param startDate The start date of the range (inclusive)
   * @param endDate The end date of the range (inclusive)
   * @return List of executions within the specified date range, ordered by date
   */
  public List<HabitExecution> getByHabitAndDateRange(Long habitId, LocalDate startDate, LocalDate endDate) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(HabitExecutionRepositoryQueries.GET_BY_HABIT_AND_DATE_RANGE)) {
      pstmt.setLong(1, habitId);
      pstmt.setDate(2, java.sql.Date.valueOf(startDate));
      pstmt.setDate(3, java.sql.Date.valueOf(endDate));
      
      try (ResultSet rs = pstmt.executeQuery()) {
        List<HabitExecution> executions = new ArrayList<>();
        while (rs.next()) {
          executions.add(mapResultSetToHabitExecution(rs));
        }
        return executions;
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving habit executions by date range: \n " + e.getMessage());
    }
    return new ArrayList<>();
  }
  
  private HabitExecution mapResultSetToHabitExecution(ResultSet rs) throws SQLException {
    Long habitId = rs.getLong("habit_id");
    LocalDate date = rs.getDate("date").toLocalDate();
    boolean completed = rs.getBoolean("completed");
    
    HabitExecution execution = new HabitExecution(habitId, date, completed);
    execution.setId(rs.getLong("id"));
    return execution;
  }
}