package com.mkhabibullin.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object containing basic application information.
 * Provides essential metadata about the running application instance.
 *
 * @param name    Name of the application
 * @param version Current version of the application
 */
@Schema(description = "Application information")
public record ApplicationInfo(
  @Schema(description = "Application name")
  String name,
  @Schema(description = "Application version")
  String version
) {
}
