package com.mkhabibullin.application.validation;

import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.presentation.dto.user.LoginDTO;
import com.mkhabibullin.presentation.dto.user.RegisterUserDTO;
import com.mkhabibullin.presentation.dto.user.UpdateEmailDTO;
import com.mkhabibullin.presentation.dto.user.UpdateNameDTO;
import com.mkhabibullin.presentation.dto.user.UpdatePasswordDTO;
import com.mkhabibullin.presentation.dto.user.UserEmailDTO;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
/**
 * Validator component for user-related DTOs.
 * Provides validation methods for ensuring data integrity during user operations.
 */
@Component
public class UserMapperValidator {
  
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
  );
  
  /**
   * Validates user registration data.
   *
   * @param dto the registration data to validate
   * @throws ValidationException if validation fails
   */
  public void validateRegisterUserDTO(RegisterUserDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("Registration data cannot be null");
    }
    validateEmail(dto.email());
    validatePassword(dto.password());
    validateName(dto.name());
  }
  
  /**
   * Validates login data.
   *
   * @param dto the login data to validate
   * @throws ValidationException if validation fails
   */
  public void validateLoginDTO(LoginDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("Login data cannot be null");
    }
    validateEmail(dto.email());
    if (dto.password() == null || dto.password().trim().isEmpty()) {
      throw new ValidationException("Password is required");
    }
  }
  
  /**
   * Validates email update data.
   *
   * @param dto the email update data to validate
   * @throws ValidationException if validation fails
   */
  public void validateUpdateEmailDTO(UpdateEmailDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("Email update data cannot be null");
    }
    validateEmail(dto.newEmail());
  }
  
  /**
   * Validates name update data.
   *
   * @param dto the name update data to validate
   * @throws ValidationException if validation fails
   */
  public void validateUpdateNameDTO(UpdateNameDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("Name update data cannot be null");
    }
    validateName(dto.newName());
  }
  
  /**
   * Validates password update data.
   *
   * @param dto the password update data to validate
   * @throws ValidationException if validation fails
   */
  public void validateUpdatePasswordDTO(UpdatePasswordDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("Password update data cannot be null");
    }
    validatePassword(dto.newPassword());
  }
  
  /**
   * Validates email-only data.
   *
   * @param dto the email-only data to validate
   * @throws ValidationException if validation fails
   */
  public void validateUserEmailDTO(UserEmailDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException("Email data cannot be null");
    }
    validateEmail(dto.email());
  }
  
  private void validateEmail(String email) throws ValidationException {
    if (email == null || email.trim().isEmpty()) {
      throw new ValidationException("Email is required");
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new ValidationException("Invalid email format");
    }
  }
  
  private void validatePassword(String password) throws ValidationException {
    if (password == null || password.trim().isEmpty()) {
      throw new ValidationException("Password is required");
    }
    if (!PASSWORD_PATTERN.matcher(password).matches()) {
      throw new ValidationException("Password must be at least 8 characters long and contain at least " +
                                    "one digit, one lowercase letter, one uppercase letter, and one special character");
    }
  }
  
  private void validateName(String name) throws ValidationException {
    if (name == null || name.trim().isEmpty()) {
      throw new ValidationException("Name is required");
    }
    if (name.length() < 2) {
      throw new ValidationException("Name must be at least 2 characters long");
    }
  }
}