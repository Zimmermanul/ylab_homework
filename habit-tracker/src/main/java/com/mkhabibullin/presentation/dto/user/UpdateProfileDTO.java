package com.mkhabibullin.presentation.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for updating user profile information.
 * Allows updating email, name, and password in a single request.
 * All fields are optional - only provided fields will be updated.
 */
@Schema(description = "User profile update request")
public record UpdateProfileDTO(@Schema(
  description = "New email address",
  example = "new.email@example.com",
  requiredMode = Schema.RequiredMode.NOT_REQUIRED
)
                               String newEmail,
                               
                               @Schema(
                                 description = "New display name",
                                 example = "John Smith",
                                 requiredMode = Schema.RequiredMode.NOT_REQUIRED
                               )
                               String newName,
                               
                               @Schema(
                                 description = "Current password (required for password update)",
                                 example = "oldPassword123",
                                 requiredMode = Schema.RequiredMode.NOT_REQUIRED
                               )
                               String oldPassword,
                               
                               @Schema(
                                 description = "New password",
                                 example = "newPassword123",
                                 requiredMode = Schema.RequiredMode.NOT_REQUIRED
                               )
                               String newPassword
) {
  /**
   * Checks if any update fields are present in the request.
   *
   * @return true if at least one update field is present
   */
  public boolean hasUpdates() {
    return newEmail != null || newName != null ||
           (oldPassword != null && newPassword != null);
  }
  
  /**
   * Validates that password update fields are consistent.
   *
   * @return true if password update is valid
   */
  public boolean isPasswordUpdateValid() {
    return (oldPassword == null && newPassword == null) ||
           (oldPassword != null && newPassword != null);
  }
}