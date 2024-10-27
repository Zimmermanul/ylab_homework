package com.mkhabibullin.app.mapper;

import com.mkhabibullin.app.dto.habit.HabitResponseDTO;
import com.mkhabibullin.app.model.Habit;
import com.mkhabibullin.app.validation.HabitMapperValidator;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "default", uses = HabitMapperValidator.class)
public interface HabitMapper {
  HabitMapper INSTANCE = Mappers.getMapper(HabitMapper.class);
  
  List<HabitResponseDTO> habitsToResponseDtos(List<Habit> habits);
}