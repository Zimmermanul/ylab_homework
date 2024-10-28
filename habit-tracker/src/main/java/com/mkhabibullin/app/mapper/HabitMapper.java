package com.mkhabibullin.app.mapper;

import com.mkhabibullin.app.dto.habit.HabitResponseDTO;
import com.mkhabibullin.app.model.Habit;
import com.mkhabibullin.app.validation.HabitMapperValidator;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper interface for converting between Habit entities and DTOs.
 * This mapper facilitates the transformation of habit data while performing validation
 * through {@link HabitMapperValidator}.
 *
 * <p>The mapper is configured with:</p>
 * <ul>
 *   <li>Default component model for simple instantiation</li>
 *   <li>Integration with {@link HabitMapperValidator} for field validation</li>
 *   <li>A singleton INSTANCE for stateless mapping operations</li>
 * </ul>
 *
 * @see org.mapstruct.Mapper
 * @see HabitMapperValidator
 * @see Habit
 * @see HabitResponseDTO
 */
@Mapper(componentModel = "default", uses = HabitMapperValidator.class)
public interface HabitMapper {
  /**
   * Singleton instance of the mapper.
   * Use this instance for all mapping operations to ensure consistent behavior
   * and optimal resource usage.
   */
  HabitMapper INSTANCE = Mappers.getMapper(HabitMapper.class);
  
  /**
   * Converts a list of habit entities to a list of habit response DTOs.
   * This method performs the bulk conversion of habits to their DTO representation,
   * maintaining the order of the input list.
   */
  List<HabitResponseDTO> habitsToResponseDtos(List<Habit> habits);
}