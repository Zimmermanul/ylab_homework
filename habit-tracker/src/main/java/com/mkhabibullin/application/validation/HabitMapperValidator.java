package com.mkhabibullin.application.validation;

import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.presentation.dto.habit.UpdateHabitDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Spring Validator implementation for habit-related DTOs.
 * Provides validation for CreateHabitDTO and UpdateHabitDTO objects.
 */
@Component
public class HabitMapperValidator implements Validator {
  
  private static final String NAME_FIELD = "name";
  private static final String FREQUENCY_FIELD = "frequency";
  private static final String DESCRIPTION_FIELD = "description";
  
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
    if (target == null) {
      errors.reject("habit.null", "Habit DTO cannot be null");
      return;
    }
    if (target instanceof CreateHabitDTO) {
      validateCreateHabitDTO((CreateHabitDTO) target, errors);
    } else if (target instanceof UpdateHabitDTO) {
      validateUpdateHabitDTO((UpdateHabitDTO) target, errors);
    }
  }
  
  /**
   * Validates a CreateHabitDTO object.
   * Checks for required fields and their validity.
   *
   * @param dto    the CreateHabitDTO to validate
   * @param errors validation errors holder
   */
  private void validateCreateHabitDTO(CreateHabitDTO dto, Errors errors) {
    if (dto.name() == null || dto.name().trim().isEmpty()) {
      errors.rejectValue(NAME_FIELD, "habit.name.required",
        "Habit name is required");
    } else if (dto.name().trim().length() < 2) {
      errors.rejectValue(NAME_FIELD, "habit.name.tooShort",
        "Habit name must be at least 2 characters long");
    }
    if (dto.frequency() == null) {
      errors.rejectValue(FREQUENCY_FIELD, "habit.frequency.required",
        "Habit frequency is required");
    }
    if (dto.description() != null && dto.description().trim().length() > 500) {
      errors.rejectValue(DESCRIPTION_FIELD, "habit.description.tooLong",
        "Description must not exceed 500 characters");
    }
  }
  
  /**
   * Validates an UpdateHabitDTO object.
   * Checks for valid field values when present.
   *
   * @param dto the UpdateHabitDTO to validate
   * @param errors validation errors holder
   */
  private void validateUpdateHabitDTO(UpdateHabitDTO dto, Errors errors) {
    if (dto.name() != null) {
      if (dto.name().trim().isEmpty()) {
        errors.rejectValue(NAME_FIELD, "habit.name.empty",
          "Habit name cannot be empty");
      } else if (dto.name().trim().length() < 2) {
        errors.rejectValue(NAME_FIELD, "habit.name.tooShort",
          "Habit name must be at least 2 characters long");
      }
    }
    if (dto.description() != null && dto.description().trim().length() > 500) {
      errors.rejectValue(DESCRIPTION_FIELD, "habit.description.tooLong",
        "Description must not exceed 500 characters");
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
  
  /**
   * Builds a validation error message from the Errors object.
   *
   * @param errors the Errors object containing validation errors
   * @return formatted error message string
   */
  private String buildValidationErrorMessage(Errors errors) {
    StringBuilder message = new StringBuilder();
    errors.getAllErrors().forEach(error -> {
      if (message.length() > 0) {
        message.append("; ");
      }
      message.append(error.getDefaultMessage());
    });
    return message.toString();
  }
}