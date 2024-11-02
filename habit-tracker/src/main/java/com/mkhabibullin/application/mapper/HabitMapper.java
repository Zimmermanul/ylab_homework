package com.mkhabibullin.application.mapper;

import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.presentation.dto.habit.HabitResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;
/**
 * MapStruct mapper interface for converting between Habit entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface HabitMapper {
  /**
   * Converts a list of habit entities to a list of habit response DTOs.
   */
  List<HabitResponseDTO> habitsToResponseDtos(List<Habit> habits);
  
  /**
   * Maps a single habit entity to response DTO.
   */
  HabitResponseDTO habitToResponseDto(Habit habit);
}