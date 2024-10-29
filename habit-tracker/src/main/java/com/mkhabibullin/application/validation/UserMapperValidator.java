package com.mkhabibullin.application.validation;

import com.mkhabibullin.application.mapper.UserMapper;
import com.mkhabibullin.presentation.dto.user.RegisterUserDTO;
import com.mkhabibullin.presentation.dto.user.UpdateEmailDTO;
import com.mkhabibullin.presentation.dto.user.UpdateNameDTO;
import com.mkhabibullin.presentation.dto.user.UpdatePasswordDTO;
import com.mkhabibullin.presentation.dto.user.UpdateUserDTO;
import com.mkhabibullin.presentation.dto.user.UserEmailDTO;
import org.mapstruct.BeforeMapping;

import java.util.regex.Pattern;

/**
 * Validator class for user-related DTOs.
 * Provides validation methods used by {@link UserMapper} to ensure data integrity
 * during user registration, updates, and other operations.
 */
public class UserMapperValidator {
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[A-Za-z0-9+_.-]+@(.+)$"
  );
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
  );
  
  /**
   * Validates user registration data before mapping.
   * Checks email format, password strength, and name requirements.
   *
   * @param dto The registration data to validate
   */
  @BeforeMapping
  public void validateRegisterUserDTO(RegisterUserDTO dto) {
    validateEmail(dto.email());
    validatePassword(dto.password());
    validateName(dto.name());
  }
  
  /**
   * Validates user update data before mapping.
   *
   * @param dto The update data to validate
   */
  @BeforeMapping
  public void validateUpdateUserDTO(UpdateUserDTO dto) {
    if (dto.email() != null) {
      validateEmail(dto.email());
    }
    if (dto.name() != null) {
      validateName(dto.name());
    }
  }
  
  /**
   * Validates email update data before mapping.
   * @param dto The email update data to validate
   */
  @BeforeMapping
  public void validateUpdateEmailDTO(UpdateEmailDTO dto) {
    validateEmail(dto.newEmail());
  }
  
  /**
   * Validates name update data before mapping.
   * @param dto The name update data to validate
   */
  @BeforeMapping
  public void validateUpdateNameDTO(UpdateNameDTO dto) {
    validateName(dto.newName());
  }
  
  /**
   * Validates password update data before mapping.
   * @param dto The password update data to validate
   */
  @BeforeMapping
  public void validateUpdatePasswordDTO(UpdatePasswordDTO dto) {
    validatePassword(dto.newPassword());
  }
  
  /**
   * Validates email-only DTO before mapping.
   * @param dto The email-only data to validate
   */
  @BeforeMapping
  public void validateUserEmailDTO(UserEmailDTO dto) {
    validateEmail(dto.email());
  }
  
  private void validateEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new ValidationException("Email is required");
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new ValidationException("Invalid email format");
    }
  }
  
  private void validatePassword(String password) {
    if (password == null || password.trim().isEmpty()) {
      throw new ValidationException("Password is required");
    }
    if (!PASSWORD_PATTERN.matcher(password).matches()) {
      throw new ValidationException("Password must be at least 8 characters long and contain at least " +
                                    "one digit, one lowercase letter, one uppercase letter, and one special character");
    }
  }
  
  private void validateName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new ValidationException("Name is required");
    }
    if (name.length() < 2) {
      throw new ValidationException("Name must be at least 2 characters long");
    }
  }
  
  public static class ValidationException extends RuntimeException {
    public ValidationException(String message) {
      super(message);
    }
  }
}