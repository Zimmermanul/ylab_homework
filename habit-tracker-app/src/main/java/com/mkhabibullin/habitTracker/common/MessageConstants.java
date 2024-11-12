package com.mkhabibullin.habitTracker.common;

import java.util.regex.Pattern;

/**
 * Constants class containing error messages.
 */
public final class MessageConstants {
  private MessageConstants() {
  }
  // Authentication validation messages
  public static final String NO_REQUEST_CONTEXT = "No request context found";
  public static final String USER_NOT_AUTHENTICATED = "User not authenticated";
  public static final String ADMIN_PRIVILEGES_REQUIRED = "Admin privileges required";
  public static final String INSUFFICIENT_PRIVILEGES = "Insufficient privileges to modify user: %s";
  public static final String INVALID_SESSION = "Invalid or expired session";
  
  // Habit execution validation messages
  public static final String HABIT_EXECUTION_NULL = "HabitExecutionRequestDTO cannot be null";
  public static final String DATE_REQUIRED = "Date is required";
  public static final String DATE_FUTURE = "Cannot record executions for future dates";
  public static final String COMPLETION_STATUS_REQUIRED = "Completion status is required";
  
  // Habit validation messages
  public static final String HABIT_NAME_REQUIRED = "Habit name is required";
  public static final String HABIT_NAME_TOO_SHORT = "Habit name must be at least 2 characters long";
  public static final String HABIT_NAME_EMPTY = "Habit name cannot be empty";
  public static final String HABIT_FREQUENCY_REQUIRED = "Habit frequency is required";
  public static final String HABIT_DESCRIPTION_TOO_LONG = "Description must not exceed 500 characters";
  
  // Habit validation field names
  public static final String FIELD_NAME = "name";
  public static final String FIELD_FREQUENCY = "frequency";
  public static final String FIELD_DESCRIPTION = "description";
  
  // Habit validation error codes
  public static final String ERROR_NAME_REQUIRED = "habit.name.required";
  public static final String ERROR_NAME_TOO_SHORT = "habit.name.tooShort";
  public static final String ERROR_NAME_EMPTY = "habit.name.empty";
  public static final String ERROR_FREQUENCY_REQUIRED = "habit.frequency.required";
  public static final String ERROR_DESCRIPTION_TOO_LONG = "habit.description.tooLong";
  
  // User validation patterns
  public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
  public static final Pattern PASSWORD_PATTERN = Pattern.compile(
    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
  );
  
  // User validation messages
  public static final String REGISTER_DATA_NULL = "Registration data cannot be null";
  public static final String LOGIN_DATA_NULL = "Login data cannot be null";
  public static final String EMAIL_UPDATE_NULL = "Email update data cannot be null";
  public static final String NAME_UPDATE_NULL = "Name update data cannot be null";
  public static final String PASSWORD_UPDATE_NULL = "Password update data cannot be null";
  public static final String EMAIL_DATA_NULL = "Email data cannot be null";
  public static final String EMAIL_REQUIRED = "Email is required";
  public static final String EMAIL_INVALID = "Invalid email format";
  public static final String EMAIL_ALREADY_IN_USE = "Email already in use";
  public static final String PASSWORD_REQUIRED = "Password is required";
  public static final String PASSWORD_INVALID = "Password must be at least 8 characters long and contain at least " +
                                                "one digit, one lowercase letter, one uppercase letter, and one special character";
  
  public static final String NAME_REQUIRED = "Name is required";
  public static final String NAME_TOO_SHORT = "Name must be at least 2 characters long";
  public static final String HABIT_NOT_FOUND = "Habit not found with id: %d";
  public static final String USER_NOT_FOUND = "User not found";
  public static final String ADMIN_USER_CANNOT_BE_MANAGED = "Admin user cannot be deleted or blocked";
  
  // DateTime validation
  public static final String START_DATE_REQUIRED = "Start date is required";
  public static final String END_DATE_REQUIRED = "End date is required";
  public static final String DATES_REQUIRED = "Start date and end date are required";
  public static final String INVALID_DATE_RANGE = "End date must be after start date";
  public static final String START_DATE_FUTURE = "Start date cannot be in the future";
  public static final String END_DATE_FUTURE = "End date cannot be in the future";
  
  // Repository error messages
  public static final String ERROR_SAVING = "Error saving %s";
  public static final String ERROR_UPDATING = "Error updating %s";
  public static final String ERROR_DELETING = "Error deleting %s";
  public static final String ERROR_RETRIEVING = "Error retrieving %s";
  public static final String ERROR_RETRIEVING_BY_DATE = "Error retrieving %s by date range";
  public static final String NOT_FOUND_WITH_ID = "%s not found with ID: %s";
  public static final String ERROR_RETRIEVING_BY_USER = "Error retrieving %s for user ID: %s";
  public static final String USER_REQUIRED = "User must not be null";
  public static final String EMAIL_IN_USE = "Email address already in use: %s";
  public static final String NOT_FOUND_WITH_EMAIL = "%s not found with email: %s";
  public static final String ERROR_CHECKING_EMAIL = "Error checking email existence: %s";
  public static final String ERROR_RETRIEVING_BY_EMAIL = "Error retrieving %s by email: %s";
}