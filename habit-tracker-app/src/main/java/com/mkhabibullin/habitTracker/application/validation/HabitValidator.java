package com.mkhabibullin.habitTracker.application.validation;

import com.mkhabibullin.habitTracker.common.MessageConstants;
import com.mkhabibullin.habitTracker.domain.exception.ValidationException;
import com.mkhabibullin.habitTracker.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.habitTracker.presentation.dto.habit.UpdateHabitDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

/**
 * Spring Validator implementation for habit-related DTOs.
 * Provides validation for CreateHabitDTO and UpdateHabitDTO objects.
 */
@Component
public class HabitValidator implements Validator {
  
  /**
   * Determines if this validator can validate instances of the supplied class.
   *
   * @param clazz the class to check
   * @return true if this validator supports the given class
   */
  @Override
  public boolean supports(Class<?> clazz) {
    return CreateHabitDTO.class.isAssignableFrom(clazz) ||
           UpdateHabitDTO.class.isAssignableFrom(clazz);
  }
  
  /**
   * Validates the given object and puts the validation errors in the Errors object.
   *
   * @param target the object to validate
   * @param errors contextual state about the validation process
   */
  @Override
  public void validate(Object target, Errors errors) {
    if (target instanceof CreateHabitDTO) {
      validateCreateHabitDTO((CreateHabitDTO) target, errors);
    } else if (target instanceof UpdateHabitDTO) {
      validateUpdateHabitDTO((UpdateHabitDTO) target, errors);
    }
  }
  
  /**
   * Convenience method to validate CreateHabitDTO and throw exception if invalid.
   *
   * @param dto the CreateHabitDTO to validate
   * @throws ValidationException if validation fails
   */
  public void validateCreateHabitDTO(CreateHabitDTO dto) throws ValidationException {
    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dto, "createHabitDTO");
    validate(dto, errors);
    if (errors.hasErrors()) {
      throw new ValidationException(buildValidationErrorMessage(errors));
    }
  }
  
  /**
   * Convenience method to validate UpdateHabitDTO and throw exception if invalid.
   *
   * @param dto the UpdateHabitDTO to validate
   * @throws ValidationException if validation fails
   */
  public void validateUpdateHabitDTO(UpdateHabitDTO dto) throws ValidationException {
    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dto, "updateHabitDTO");
    validate(dto, errors);
    if (errors.hasErrors()) {
      throw new ValidationException(buildValidationErrorMessage(errors));
    }
  }
  
  private void validateCreateHabitDTO(CreateHabitDTO dto, Errors errors) {
    String name = Optional.ofNullable(dto.name())
      .map(String::trim)
      .orElse("");
    if (name.isEmpty()) {
      errors.rejectValue(
        MessageConstants.FIELD_NAME,
        MessageConstants.ERROR_NAME_REQUIRED,
        MessageConstants.HABIT_NAME_REQUIRED
      );
    } else if (name.length() < 2) {
      errors.rejectValue(
        MessageConstants.FIELD_NAME,
        MessageConstants.ERROR_NAME_TOO_SHORT,
        MessageConstants.HABIT_NAME_TOO_SHORT
      );
    }
    Optional.ofNullable(dto.frequency())
      .ifPresentOrElse(
        frequency -> {
        },
        () -> errors.rejectValue(
          MessageConstants.FIELD_FREQUENCY,
          MessageConstants.ERROR_FREQUENCY_REQUIRED,
          MessageConstants.HABIT_FREQUENCY_REQUIRED
        )
      );
    Optional.ofNullable(dto.description())
      .map(String::trim)
      .filter(desc -> desc.length() > 500)
      .ifPresent(desc -> errors.rejectValue(
        MessageConstants.FIELD_DESCRIPTION,
        MessageConstants.ERROR_DESCRIPTION_TOO_LONG,
        MessageConstants.HABIT_DESCRIPTION_TOO_LONG
      ));
  }
  
  private void validateUpdateHabitDTO(UpdateHabitDTO dto, Errors errors) {
    Optional.ofNullable(dto.name())
      .map(String::trim)
      .ifPresent(name -> {
        if (name.isEmpty()) {
          errors.rejectValue(
            MessageConstants.FIELD_NAME,
            MessageConstants.ERROR_NAME_EMPTY,
            MessageConstants.HABIT_NAME_EMPTY
          );
        } else if (name.length() < 2) {
          errors.rejectValue(
            MessageConstants.FIELD_NAME,
            MessageConstants.ERROR_NAME_TOO_SHORT,
            MessageConstants.HABIT_NAME_TOO_SHORT
          );
        }
      });
    Optional.ofNullable(dto.description())
      .map(String::trim)
      .filter(desc -> desc.length() > 500)
      .ifPresent(desc -> errors.rejectValue(
        MessageConstants.FIELD_DESCRIPTION,
        MessageConstants.ERROR_DESCRIPTION_TOO_LONG,
        MessageConstants.HABIT_DESCRIPTION_TOO_LONG
      ));
  }
  
  private String buildValidationErrorMessage(Errors errors) {
    StringBuilder message = new StringBuilder();
    errors.getAllErrors().forEach(error -> {
      if (!message.isEmpty()) {
        message.append("; ");
      }
      message.append(error.getDefaultMessage());
    });
    return message.toString();
  }
}