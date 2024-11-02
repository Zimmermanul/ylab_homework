package com.mkhabibullin.application.mapper;

import com.mkhabibullin.domain.model.HabitExecution;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionRequestDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionResponseDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitProgressReportDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitStatisticsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
/**
 * MapStruct mapper interface for converting between Habit Execution DTOs and entities.
 */
@Mapper(componentModel = "spring")
public interface HabitExecutionMapper {
  
  @Mapping(target = "id", ignore = true)
  HabitExecution requestDtoToExecution(HabitExecutionRequestDTO dto, Long habitId);
  
  List<HabitExecutionResponseDTO> executionsToResponseDtos(List<HabitExecution> executions);
  
  HabitStatisticsDTO createStatisticsDto(
    int currentStreak,
    double successPercentage,
    long totalExecutions,
    long completedExecutions,
    long missedExecutions,
    Map<DayOfWeek, Long> completionsByDay
  );
  
  HabitProgressReportDTO createProgressReportDto(
    String report,
    boolean improvingTrend,
    int longestStreak,
    List<String> suggestions
  );
}