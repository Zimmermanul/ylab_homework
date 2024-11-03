package com.mkhabibullin.application.mapper;

import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.presentation.dto.habit.HabitResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper interface for converting between Habit entities and DTOs.
 * Provides mapping functionality for transforming habit data between different representations.
 */
@Mapper(componentModel = "spring")
public interface HabitMapper {
  
  /**
   * Converts a list of Habit entities to response DTOs.
   *
   * @param habits the list of habit entities to convert
   * @return list of mapped habit response DTOs
   */
  List<HabitResponseDTO> habitsToResponseDtos(List<Habit> habits);
  
  /**
   * Converts a single Habit entity to a response DTO.
   *
   * @param habit the habit entity to convert
   * @return the mapped habit response DTO
   */
  HabitResponseDTO habitToResponseDto(Habit habit);
}