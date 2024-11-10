package com.mkhabibullin.presentation.dto.habit;

import com.mkhabibullin.domain.model.Habit;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * Data Transfer Object for updating an existing habit.
 * This record contains the modifiable fields of a habit that can be updated.
 *
 * @param name        The new name for the habit
 * @param description The new description for the habit
 * @param frequency   The new frequency for the habit (e.g., DAILY, WEEKLY)
 */
@Schema(description = "Request to update an existing habit")
public record UpdateHabitDTO(
  @Schema(description = "Updated name of the habit", example = "Morning Yoga")
  String name,
  
  @Schema(description = "Updated description of the habit", example = "15 minutes of morning yoga routine")
  String description,
  
  @Schema(description = "Updated frequency of the habit", example = "DAILY")
  Habit.Frequency frequency
) {
}
