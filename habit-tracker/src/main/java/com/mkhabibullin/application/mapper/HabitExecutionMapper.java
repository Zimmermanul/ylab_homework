package com.mkhabibullin.application.mapper;

import com.mkhabibullin.application.validation.HabitExecutionMapperValidator;
import com.mkhabibullin.domain.model.HabitExecution;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionRequestDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitExecutionResponseDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitProgressReportDTO;
import com.mkhabibullin.presentation.dto.habitExecution.HabitStatisticsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

/**
 * MapStruct mapper interface for converting between Habit Execution DTOs and entities.
 * This mapper handles the conversion of habit execution data while performing validation
 * through {@link HabitExecutionMapperValidator}.
 *
 * <p>The mapper is configured with:</p>
 * <ul>
 *   <li>Default component model for simple instantiation</li>
 *   <li>Integration with {@link HabitExecutionMapperValidator} for field validation</li>
 *   <li>A singleton INSTANCE for stateless mapping operations</li>
 * </ul>
 *
 * @see org.mapstruct.Mapper
 * @see HabitExecutionMapperValidator
 * @see HabitExecutionRequestDTO
 * @see HabitExecution
 */
@Mapper(componentModel = "default", uses = HabitExecutionMapperValidator.class)
public interface HabitExecutionMapper {
  /**
   * Singleton instance of the mapper.
   * Use this instance for all mapping operations to ensure consistent behavior
   * and optimal resource usage.
   */
  HabitExecutionMapper INSTANCE = Mappers.getMapper(HabitExecutionMapper.class);
  
  /**
   * Converts a habit execution request DTO to a habit execution entity.
   * This method performs validation on all mapped fields using {@link HabitExecutionMapperValidator}.
   */
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