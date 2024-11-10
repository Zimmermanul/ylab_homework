package com.mkhabibullin.presentation.controller;

import com.mkhabibullin.application.mapper.UserMapper;
import com.mkhabibullin.application.service.UserService;
import com.mkhabibullin.application.validation.AuthenticationValidator;
import com.mkhabibullin.application.validation.UserValidator;
import com.mkhabibullin.common.Audited;
import com.mkhabibullin.domain.exception.CustomAuthenticationException;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import com.mkhabibullin.presentation.dto.MessageDTO;
import com.mkhabibullin.presentation.dto.user.LoginDTO;
import com.mkhabibullin.presentation.dto.user.RegisterUserDTO;
import com.mkhabibullin.presentation.dto.user.UpdateEmailDTO;
import com.mkhabibullin.presentation.dto.user.UpdateNameDTO;
import com.mkhabibullin.presentation.dto.user.UpdatePasswordDTO;
import com.mkhabibullin.presentation.dto.user.UpdateProfileDTO;
import com.mkhabibullin.presentation.dto.user.UserEmailDTO;
import com.mkhabibullin.presentation.dto.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for user management operations.
 * Handles user registration, authentication, profile management, and administrative functions.
 * <p>
 * Provides endpoints for:
 * - User registration and authentication
 * - Profile management (email, name, password updates)
 * - Account management (deletion, blocking)
 * - Administrative functions (user listing, blocking/unblocking)
 * <p>
 * All operations are audited and include appropriate access control.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API endpoints for user registration, authentication, and account management")
public class UserRestController {
  private static final Logger log = LoggerFactory.getLogger(UserRestController.class);
  private final UserService userService;
  private final UserMapper userMapper;
  private final UserValidator userValidator;
  private final AuthenticationValidator authValidator;
  
  /**
   * Constructs a new UserRestController with required dependencies.
   *
   * @param userService   Service for handling user operations
   * @param userMapper    Mapper for converting between user domain models and DTOs
   * @param userValidator Validator for user-related data
   * @param authValidator Validator for authentication and authorization
   */
  public UserRestController(UserService userService,
                            UserMapper userMapper,
                            UserValidator userValidator,
                            AuthenticationValidator authValidator) {
    this.userService = userService;
    this.userMapper = userMapper;
    this.userValidator = userValidator;
    this.authValidator = authValidator;
  }
  
  /**
   * Registers a new user account.
   * Creates a new user with the provided information after validation.
   *
   * @param registerDTO Registration information including email, password, and name
   * @return ResponseEntity containing the created user's information
   * @throws IOException         if there's an error processing the request
   * @throws ValidationException if the registration data is invalid
   */
  @Operation(
    summary = "Register new user",
    description = "Creates a new user account with the provided information"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "201",
      description = "User registered successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = UserResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid input data",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Audited(audited = "User Registration")
  public ResponseEntity<UserResponseDTO> registerUser(@RequestBody RegisterUserDTO registerDTO)
    throws IOException, ValidationException {
    log.debug("Processing user registration request for email: {}", registerDTO.email());
    userValidator.validateRegisterUserDTO(registerDTO);
    User user = userMapper.registerDtoToUser(registerDTO);
    userService.register(user.getEmail(), registerDTO.password(), user.getName());
    log.info("User registered successfully: {}", user.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(userMapper.userToResponseDto(user));
  }
  
  /**
   * Authenticates a user and creates a session.
   * Validates credentials and handles session management.
   *
   * @param loginDTO Login credentials (email and password)
   * @param session  HTTP session for managing user state
   * @return ResponseEntity containing authenticated user's information
   * @throws AuthenticationException if credentials are invalid
   * @throws ValidationException     if login data is invalid
   * @throws IOException             if there's an error processing the request
   */
  @Operation(
    summary = "User login",
    description = "Authenticates user credentials and creates a session"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Login successful",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = UserResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Invalid credentials",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid input data",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "User Login")
  public ResponseEntity<UserResponseDTO> login(
    @RequestBody LoginDTO loginDTO,
    HttpSession session) throws AuthenticationException, ValidationException, IOException {
    log.debug("Processing login request for email: {}", loginDTO.email());
    userValidator.validateLoginDTO(loginDTO);
    if (!userService.authenticate(loginDTO.email(), loginDTO.password())) {
      throw new CustomAuthenticationException("Invalid credentials");
    }
    User user = userService.getByEmail(loginDTO.email());
    if (user.isBlocked()) {
      throw new CustomAuthenticationException("Account is blocked");
    }
    session.setAttribute("user", user);
    log.info("User logged in successfully: {}", user.getEmail());
    return ResponseEntity.ok(userMapper.userToResponseDto(user));
  }
  
  /**
   * Logs out the current user by invalidating their session.
   *
   * @param currentUser Currently authenticated user
   * @param session     HTTP session to invalidate
   * @throws AuthenticationException if user is not authenticated
   */
  @Operation(
    summary = "User logout",
    description = "Invalidates the current user session"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "204",
      description = "Logout successful"
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Audited(audited = "User Logout")
  public void logout(
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser,
    HttpSession session) throws AuthenticationException {
    authValidator.validateSession(session);
    log.debug("Processing logout request for user: {}", currentUser.getEmail());
    session.invalidate();
    log.info("User logged out successfully: {}", currentUser.getEmail());
  }
  
  /**
   * Updates user profile information. Allows updating email, name, and password
   * in a single request. Only provided fields will be updated.
   *
   * @param updateDTO   User profile update information
   * @param currentUser Currently authenticated user
   * @return ResponseEntity containing updated user information
   * @throws ValidationException if any update data is invalid
   */
  @Operation(
    summary = "Update user profile",
    description = "Updates user profile information including email, name, and password. Only provided fields will be updated"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Profile updated successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = UserResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid input data",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "409",
      description = "Email already in use",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @PutMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Update User Profile")
  public ResponseEntity<UserResponseDTO> updateUserProfile(
    @RequestBody UpdateProfileDTO updateDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser
  ) throws ValidationException {
    log.debug("Processing profile update request for user: {}", currentUser.getEmail());
    validateUpdateRequest(updateDTO);
    if (updateDTO.newEmail() != null) {
      userService.updateEmail(currentUser.getId(), updateDTO.newEmail());
      currentUser.setEmail(updateDTO.newEmail());
    }
    if (updateDTO.newName() != null) {
      userService.updateName(currentUser.getId(), updateDTO.newName());
      currentUser.setName(updateDTO.newName());
    }
    if (updateDTO.newPassword() != null) {
      userService.updatePassword(currentUser.getId(), updateDTO.newPassword());
    }
    log.info("Profile updated successfully for user ID: {}", currentUser.getId());
    return ResponseEntity.ok(userMapper.userToResponseDto(currentUser));
  }
  
  /**
   * Validates the update profile request.
   *
   * @param updateDTO the update request to validate
   * @throws ValidationException if the request is invalid
   */
  private void validateUpdateRequest(UpdateProfileDTO updateDTO) throws ValidationException {
    if (!updateDTO.hasUpdates()) {
      throw new ValidationException("No update fields provided");
    }
    
    if (!updateDTO.isPasswordUpdateValid()) {
      throw new ValidationException("Both old and new password must be provided for password update");
    }
    
    // Validate individual fields if present
    if (updateDTO.newEmail() != null) {
      userValidator.validateUpdateEmailDTO(new UpdateEmailDTO(updateDTO.newEmail()));
    }
    
    if (updateDTO.newName() != null) {
      userValidator.validateUpdateNameDTO(new UpdateNameDTO(updateDTO.newName()));
    }
    
    if (updateDTO.newPassword() != null) {
      userValidator.validateUpdatePasswordDTO(
        new UpdatePasswordDTO(updateDTO.oldPassword(), updateDTO.newPassword())
      );
    }
  }
  
  /**
   * Deletes a user account. Admins can delete any account, users can only delete their own.
   *
   * @param email       Email of the account to delete
   * @param currentUser Currently authenticated user
   * @param session     Current HTTP session
   * @throws AuthenticationException if user is not authenticated
   * @throws AccessDeniedException   if user lacks required permissions
   */
  @Operation(
    summary = "Delete user account",
    description = "Permanently deletes a user account. Admin can delete any account, users can only delete their own"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "204",
      description = "Account deleted successfully"
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "403",
      description = "Not authorized",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @DeleteMapping(value = "/{email}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Audited(audited = "Delete Account")
  public void deleteAccount(
    @Parameter(description = "Email of the account to delete", example = "user@example.com", required = true)
    @PathVariable("email") String email,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser,
    HttpSession session) throws CustomAuthenticationException, AccessDeniedException {
    log.debug("Processing account deletion request for email: {}", email);
    authValidator.validateModificationPermission(currentUser, email);
    userService.deleteAccount(email);
    if (currentUser.getEmail().equals(email)) {
      session.invalidate();
    }
    log.info("Account deleted successfully: {}", email);
  }
  
  /**
   * Retrieves all registered users. Restricted to administrators only.
   *
   * @param currentUser Currently authenticated user
   * @return ResponseEntity containing list of all users
   * @throws AccessDeniedException  if user is not an administrator
   */
  @Operation(
    summary = "Get all users (Admin only)",
    description = "Retrieves a list of all registered users. Requires administrator privileges"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Users retrieved successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = UserResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "403",
      description = "Not authorized - Admin only",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "View All Users")
  public ResponseEntity<List<UserResponseDTO>> getAllUsers(
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws AccessDeniedException {
    log.debug("Processing get all users request by admin: {}", currentUser.getEmail());
    authValidator.validateAdminPrivileges(currentUser);
    List<User> users = userService.getAll();
    List<UserResponseDTO> userDTOs = userMapper.usersToResponseDtos(users);
    log.info("Retrieved {} users by admin: {}", users.size(), currentUser.getEmail());
    return ResponseEntity.ok(userDTOs);
  }
  
  /**
   * Blocks a user account. Restricted to administrators only.
   *
   * @param userEmailDTO Email of the user to block
   * @param currentUser  Currently authenticated administrator
   * @return ResponseEntity containing success message
   * @throws ValidationException    if the email is invalid
   * @throws AccessDeniedException  if user is not an administrator
   */
  @Operation(
    summary = "Block user (Admin only)",
    description = "Blocks a user account preventing them from logging in. Requires administrator privileges"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "User blocked successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = MessageDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid email",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "403",
      description = "Not authorized - Admin only",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @PutMapping(value = "/block", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Block User")
  public ResponseEntity<MessageDTO> blockUser(
    @RequestBody UserEmailDTO userEmailDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException, AccessDeniedException {
    log.debug("Processing block user request for email: {} by admin: {}",
      userEmailDTO.email(), currentUser.getEmail());
    authValidator.validateAdminPrivileges(currentUser);
    userValidator.validateUserEmailDTO(userEmailDTO);
    userService.block(userEmailDTO.email());
    log.info("User blocked successfully: {} by admin: {}",
      userEmailDTO.email(), currentUser.getEmail());
    return ResponseEntity.ok(new MessageDTO("User blocked successfully"));
  }
  
  /**
   * Unblocks a user account. Restricted to administrators only.
   *
   * @param userEmailDTO Email of the user to unblock
   * @param currentUser  Currently authenticated administrator
   * @return ResponseEntity containing success message
   * @throws ValidationException    if the email is invalid
   * @throws AccessDeniedException  if user is not an administrator
   */
  @Operation(
    summary = "Unblock user (Admin only)",
    description = "Unblocks a previously blocked user account. Requires administrator privileges"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "User unblocked successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = MessageDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid email",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "403",
      description = "Not authorized - Admin only",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "User not found",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = ErrorDTO.class)
      )
    )
  })
  @PutMapping(value = "/unblock", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Unblock User")
  public ResponseEntity<MessageDTO> unblockUser(
    @RequestBody UserEmailDTO userEmailDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException, AccessDeniedException {
    log.debug("Processing unblock user request for email: {} by admin: {}",
      userEmailDTO.email(), currentUser.getEmail());
    authValidator.validateAdminPrivileges(currentUser);
    userValidator.validateUserEmailDTO(userEmailDTO);
    userService.unblock(userEmailDTO.email());
    log.info("User unblocked successfully: {} by admin: {}",
      userEmailDTO.email(), currentUser.getEmail());
    return ResponseEntity.ok(new MessageDTO("User unblocked successfully"));
  }
}