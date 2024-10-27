package com.mkhabibullin.app.mapper;

import com.mkhabibullin.app.dto.habitExecution.HabitExecutionRequestDTO;
import com.mkhabibullin.app.dto.habitExecution.HabitExecutionResponseDTO;
import com.mkhabibullin.app.dto.habitExecution.HabitProgressReportDTO;
import com.mkhabibullin.app.dto.habitExecution.HabitStatisticsDTO;
import com.mkhabibullin.app.model.HabitExecution;
import com.mkhabibullin.app.validation.HabitExecutionMapperValidator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "default", uses = HabitExecutionMapperValidator.class)
public interface HabitExecutionMapper {
  HabitExecutionMapper INSTANCE = Mappers.getMapper(HabitExecutionMapper.class);
  
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "date", source = "dto.date", qualifiedByName = "validateDate")
  @Mapping(target = "completed", source = "dto.completed", qualifiedByName = "validateCompleted")
  @Mapping(target = "habitId", source = "habitId", qualifiedByName = "validateHabitId")
  HabitExecution requestDtoToExecution(HabitExecutionRequestDTO dto, Long habitId);
  
  List<HabitExecutionResponseDTO> executionsToResponseDtos(List<HabitExecution> executions);
  
  @Named("createStatistics")
  default HabitStatisticsDTO createStatisticsDto(
    int currentStreak,
    double successPercentage,
    long totalExecutions,
    long completedExecutions,
    long missedExecutions,
    Map<DayOfWeek, Long> completionsByDay
  ) {
    return new HabitStatisticsDTO(
      currentStreak,
      successPercentage,
      totalExecutions,
      completedExecutions,
      missedExecutions,
      completionsByDay
    );
  }
  
  @Named("createProgressReport")
  default HabitProgressReportDTO createProgressReportDto(
    String report,
    boolean improvingTrend,
    int longestStreak,
    List<String> suggestions
  ) {
    return new HabitProgressReportDTO(
      report,
      improvingTrend,
      longestStreak,
      suggestions
    );
  }
}