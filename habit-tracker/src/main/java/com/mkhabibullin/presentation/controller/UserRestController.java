package com.mkhabibullin.presentation.controller;

import com.mkhabibullin.application.mapper.UserMapper;
import com.mkhabibullin.application.service.UserService;
import com.mkhabibullin.application.validation.AuthenticationValidator;
import com.mkhabibullin.application.validation.UserMapperValidator;
import com.mkhabibullin.common.Audited;
import com.mkhabibullin.domain.exception.AuthenticationException;
import com.mkhabibullin.domain.exception.AuthorizationException;
import com.mkhabibullin.domain.exception.EntityNotFoundException;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import com.mkhabibullin.presentation.dto.MessageDTO;
import com.mkhabibullin.presentation.dto.user.LoginDTO;
import com.mkhabibullin.presentation.dto.user.RegisterUserDTO;
import com.mkhabibullin.presentation.dto.user.UpdateEmailDTO;
import com.mkhabibullin.presentation.dto.user.UpdateNameDTO;
import com.mkhabibullin.presentation.dto.user.UpdatePasswordDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
 * REST Controller for managing user-related operations.
 * Provides endpoints for user registration, authentication, and profile management.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API endpoints for user registration, authentication, and account management")
public class UserRestController {
  private static final Logger log = LoggerFactory.getLogger(UserRestController.class);
  private final UserService userService;
  private final UserMapper userMapper;
  private final UserMapperValidator userValidator;
  private final AuthenticationValidator authValidator;
  
  public UserRestController(UserService userService,
                            UserMapper userMapper,
                            UserMapperValidator userValidator,
                            AuthenticationValidator authValidator) {
    this.userService = userService;
    this.userMapper = userMapper;
    this.userValidator = userValidator;
    this.authValidator = authValidator;
  }
  
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
    userService.registerUser(user.getEmail(), registerDTO.password(), user.getName());
    log.info("User registered successfully: {}", user.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(userMapper.userToResponseDto(user));
  }
  
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
    
    if (!userService.authenticateUser(loginDTO.email(), loginDTO.password())) {
      throw new AuthenticationException("Invalid credentials");
    }
    
    User user = userService.getUserByEmail(loginDTO.email());
    if (user.isBlocked()) {
      throw new AuthenticationException("Account is blocked");
    }
    
    session.setAttribute("user", user);
    log.info("User logged in successfully: {}", user.getEmail());
    return ResponseEntity.ok(userMapper.userToResponseDto(user));
  }
  
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
  
  @Operation(
    summary = "Update user email",
    description = "Updates the email address for the authenticated user"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Email updated successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = UserResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid email format",
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
  @PutMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Update Email")
  public ResponseEntity<UserResponseDTO> updateEmail(
    @RequestBody UpdateEmailDTO updateDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException {
    log.debug("Processing email update request for user: {}", currentUser.getEmail());
    userValidator.validateUpdateEmailDTO(updateDTO);
    userService.updateUserEmail(currentUser.getId(), updateDTO.newEmail());
    currentUser.setEmail(updateDTO.newEmail());
    log.info("Email updated successfully for user ID: {}", currentUser.getId());
    return ResponseEntity.ok(userMapper.userToResponseDto(currentUser));
  }
  
  @Operation(
    summary = "Update user name",
    description = "Updates the display name for the authenticated user"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Name updated successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = UserResponseDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid name format",
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
    )
  })
  @PutMapping(value = "/name", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Update Name")
  public ResponseEntity<UserResponseDTO> updateName(
    @RequestBody UpdateNameDTO updateDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException {
    log.debug("Processing name update request for user: {}", currentUser.getEmail());
    userValidator.validateUpdateNameDTO(updateDTO);
    userService.updateUserName(currentUser.getId(), updateDTO.newName());
    currentUser.setName(updateDTO.newName());
    log.info("Name updated successfully for user ID: {}", currentUser.getId());
    return ResponseEntity.ok(userMapper.userToResponseDto(currentUser));
  }
  
  @Operation(
    summary = "Update user password",
    description = "Updates the password for the authenticated user"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Password updated successfully",
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = MessageDTO.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid password format",
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
    )
  })
  @PutMapping(value = "/password", produces = MediaType.APPLICATION_JSON_VALUE)
  @Audited(audited = "Update Password")
  public ResponseEntity<MessageDTO> updatePassword(
    @RequestBody UpdatePasswordDTO updateDTO,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException {
    log.debug("Processing password update request for user: {}", currentUser.getEmail());
    userValidator.validateUpdatePasswordDTO(updateDTO);
    userService.updateUserPassword(currentUser.getId(), updateDTO.newPassword());
    log.info("Password updated successfully for user ID: {}", currentUser.getId());
    return ResponseEntity.ok(new MessageDTO("Password updated successfully"));
  }
  
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
    @PathVariable String email,
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser,
    HttpSession session) throws AuthenticationException, AuthorizationException {
    log.debug("Processing account deletion request for email: {}", email);
    authValidator.validateModificationPermission(currentUser, email);
    userService.deleteUserAccount(email);
    if (currentUser.getEmail().equals(email)) {
      session.invalidate();
    }
    log.info("Account deleted successfully: {}", email);
  }
  
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
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws AuthorizationException {
    log.debug("Processing get all users request by admin: {}", currentUser.getEmail());
    authValidator.validateAdminPrivileges(currentUser);
    List<User> users = userService.getAllUsers();
    List<UserResponseDTO> userDTOs = userMapper.usersToResponseDtos(users);
    log.info("Retrieved {} users by admin: {}", users.size(), currentUser.getEmail());
    return ResponseEntity.ok(userDTOs);
  }
  
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
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException, AuthorizationException {
    log.debug("Processing block user request for email: {} by admin: {}",
      userEmailDTO.email(), currentUser.getEmail());
    authValidator.validateAdminPrivileges(currentUser);
    userValidator.validateUserEmailDTO(userEmailDTO);
    userService.blockUser(userEmailDTO.email());
    log.info("User blocked successfully: {} by admin: {}",
      userEmailDTO.email(), currentUser.getEmail());
    return ResponseEntity.ok(new MessageDTO("User blocked successfully"));
  }
  
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
    @Parameter(hidden = true) @SessionAttribute("user") User currentUser) throws ValidationException, AuthorizationException {
    log.debug("Processing unblock user request for email: {} by admin: {}",
      userEmailDTO.email(), currentUser.getEmail());
    authValidator.validateAdminPrivileges(currentUser);
    userValidator.validateUserEmailDTO(userEmailDTO);
    userService.unblockUser(userEmailDTO.email());
    log.info("User unblocked successfully: {} by admin: {}",
      userEmailDTO.email(), currentUser.getEmail());
    return ResponseEntity.ok(new MessageDTO("User unblocked successfully"));
  }
  
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ErrorDTO> handleAuthenticationException(AuthenticationException ex) {
    log.error("Authentication error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .contentType(MediaType.APPLICATION_JSON)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDTO> handleValidationException(ValidationException ex) {
    log.error("Validation error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .contentType(MediaType.APPLICATION_JSON)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorDTO> handleEntityNotFoundException(EntityNotFoundException ex) {
    log.error("Entity not found: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .contentType(MediaType.APPLICATION_JSON)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorDTO> handleException(Exception ex) {
    log.error("Unexpected error: ", ex);
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .contentType(MediaType.APPLICATION_JSON)
      .body(new ErrorDTO("Internal server error", System.currentTimeMillis()));
  }
}