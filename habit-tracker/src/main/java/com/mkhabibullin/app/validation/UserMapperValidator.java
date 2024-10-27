package com.mkhabibullin.app.validation;

import com.mkhabibullin.app.dto.user.RegisterUserDTO;
import com.mkhabibullin.app.dto.user.UpdateEmailDTO;
import com.mkhabibullin.app.dto.user.UpdateNameDTO;
import com.mkhabibullin.app.dto.user.UpdatePasswordDTO;
import com.mkhabibullin.app.dto.user.UpdateUserDTO;
import com.mkhabibullin.app.dto.user.UserEmailDTO;
import org.mapstruct.BeforeMapping;

import java.util.regex.Pattern;

public class UserMapperValidator {
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[A-Za-z0-9+_.-]+@(.+)$"
  );
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
  );
  
  @BeforeMapping
  public void validateRegisterUserDTO(RegisterUserDTO dto) {
    validateEmail(dto.email());
    validatePassword(dto.password());
    validateName(dto.name());
  }
  
  @BeforeMapping
  public void validateUpdateUserDTO(UpdateUserDTO dto) {
    if (dto.email() != null) {
      validateEmail(dto.email());
    }
    if (dto.name() != null) {
      validateName(dto.name());
    }
  }
  
  @BeforeMapping
  public void validateUpdateEmailDTO(UpdateEmailDTO dto) {
    validateEmail(dto.newEmail());
  }
  
  @BeforeMapping
  public void validateUpdateNameDTO(UpdateNameDTO dto) {
    validateName(dto.newName());
  }
  
  @BeforeMapping
  public void validateUpdatePasswordDTO(UpdatePasswordDTO dto) {
    validatePassword(dto.newPassword());
  }
  
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