package com.mkhabibullin.application.mapper;

import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.presentation.dto.habit.HabitResponseDTO;
import com.mkhabibullin.presentation.dto.habit.UpdateHabitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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
  
  /**
   * Converts a CreateHabitDTO to a Habit entity.
   * This mapping ignores certain fields that should be set separately or managed by the system:
   * - id: Generated by the system
   * - userId: Set based on the authenticated user
   * - creationDate: Set by the system
   * - active: Defaulted to true for new habits
   *
   * @param createHabitDTO the DTO containing the habit creation data
   * @return a new Habit entity with mapped values from the DTO
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "active", constant = "true")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "frequency", target = "frequency")
  Habit createDtoToHabit(CreateHabitDTO createHabitDTO);
  
  /**
   * Updates an existing Habit entity with values from UpdateHabitDTO.
   * This mapping updates only the modifiable fields while preserving system-managed fields.
   *
   * @param updateDTO the DTO containing the updated habit data
   * @param habit     the existing habit entity to update
   * @return the updated Habit entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "active", ignore = true)
  Habit updateHabitFromDto(UpdateHabitDTO updateDTO, @MappingTarget Habit habit);
}