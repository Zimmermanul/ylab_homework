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
 * Provides mapping functionality for habit execution data transformations, including
 * request/response DTOs, statistics, and progress reports.
 */
@Mapper(componentModel = "spring")
public interface HabitExecutionMapper {
  
  /**
   * Converts a habit execution request DTO to a HabitExecution entity.
   * The ID field is ignored during mapping as it will be generated.
   *
   * @param dto     the habit execution request DTO to convert
   * @param habitId the ID of the parent habit
   * @return the mapped HabitExecution entity
   */
  @Mapping(target = "id", ignore = true)
  HabitExecution requestDtoToExecution(HabitExecutionRequestDTO dto, Long habitId);
  
  /**
   * Converts a list of HabitExecution entities to response DTOs.
   *
   * @param executions the list of habit execution entities to convert
   * @return list of mapped habit execution response DTOs
   */
  List<HabitExecutionResponseDTO> executionsToResponseDtos(List<HabitExecution> executions);
  
  
  /**
   * Creates a statistics DTO containing habit execution metrics.
   *
   * @param currentStreak       the current streak of completed executions
   * @param successPercentage   the percentage of successful executions
   * @param totalExecutions     the total number of executions
   * @param completedExecutions the number of completed executions
   * @param missedExecutions    the number of missed executions
   * @param completionsByDay    map of completions grouped by day of week
   * @return the habit statistics DTO containing the compiled metrics
   */
  HabitStatisticsDTO createStatisticsDto(
    int currentStreak,
    double successPercentage,
    long totalExecutions,
    long completedExecutions,
    long missedExecutions,
    Map<DayOfWeek, Long> completionsByDay
  );
  
  @Mapping(target = "report", source = "reportData")
  @Mapping(target = "improvingTrend", source = "improvingTrend")
  @Mapping(target = "longestStreak", source = "longestStreak")
  @Mapping(target = "suggestions", source = "suggestions")
  HabitProgressReportDTO createProgressReportDto(
    Map<String, String> reportData,
    boolean improvingTrend,
    int longestStreak,
    List<String> suggestions
  );
}