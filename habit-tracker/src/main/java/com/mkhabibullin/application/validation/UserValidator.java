package com.mkhabibullin.application.validation;

import com.mkhabibullin.common.MessageConstants;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.presentation.dto.user.LoginDTO;
import com.mkhabibullin.presentation.dto.user.RegisterUserDTO;
import com.mkhabibullin.presentation.dto.user.UpdateEmailDTO;
import com.mkhabibullin.presentation.dto.user.UpdateNameDTO;
import com.mkhabibullin.presentation.dto.user.UpdatePasswordDTO;
import com.mkhabibullin.presentation.dto.user.UserEmailDTO;
import org.springframework.stereotype.Component;
/**
 * Validator component for user-related DTOs.
 * Provides validation methods for ensuring data integrity during user operations.
 */
@Component
public class UserValidator {
  
  /**
   * Validates user registration data.
   *
   * @param dto the registration data to validate
   * @throws ValidationException if validation fails
   */
  public void validateRegisterUserDTO(RegisterUserDTO dto) throws ValidationException {
    if (dto == null) {
      throw new ValidationException(MessageConstants.REGISTER_DATA_NULL);
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
      throw new ValidationException(MessageConstants.LOGIN_DATA_NULL);
    }
    validateEmail(dto.email());
    if (dto.password() == null || dto.password().trim().isEmpty()) {
      throw new ValidationException(MessageConstants.PASSWORD_REQUIRED);
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
      throw new ValidationException(MessageConstants.EMAIL_UPDATE_NULL);
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
      throw new ValidationException(MessageConstants.NAME_UPDATE_NULL);
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
      throw new ValidationException(MessageConstants.PASSWORD_UPDATE_NULL);
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
      throw new ValidationException(MessageConstants.EMAIL_DATA_NULL);
    }
    validateEmail(dto.email());
  }
  
  private void validateEmail(String email) throws ValidationException {
    if (email == null || email.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.EMAIL_REQUIRED);
    }
    if (!MessageConstants.EMAIL_PATTERN.matcher(email).matches()) {
      throw new ValidationException(MessageConstants.EMAIL_INVALID);
    }
  }
  
  private void validatePassword(String password) throws ValidationException {
    if (password == null || password.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.PASSWORD_REQUIRED);
    }
    if (!MessageConstants.PASSWORD_PATTERN.matcher(password).matches()) {
      throw new ValidationException(MessageConstants.PASSWORD_INVALID);
    }
  }
  
  private void validateName(String name) throws ValidationException {
    if (name == null || name.trim().isEmpty()) {
      throw new ValidationException(MessageConstants.NAME_REQUIRED);
    }
    if (name.length() < 2) {
      throw new ValidationException(MessageConstants.NAME_TOO_SHORT);
    }
  }
}