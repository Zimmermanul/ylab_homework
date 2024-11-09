package com.mkhabibullin.presentation.exception;

import com.mkhabibullin.domain.exception.CustomAuthenticationException;
import com.mkhabibullin.domain.exception.ValidationException;
import com.mkhabibullin.presentation.dto.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all controllers
 * by translating exceptions into appropriate HTTP responses with
 * standardized error messages.
 */
@ControllerAdvice
@Tag(name = "Error Handling", description = "Global exception handling for all endpoints")
public class GlobalExceptionHandler {
  
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  
  /**
   * Handles authentication-related exceptions.
   * Translates AuthenticationException into HTTP 401 Unauthorized responses.
   *
   * @param ex      The authentication exception that was thrown
   * @param request The web request during which the exception occurred
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(CustomAuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  @Operation(summary = "Handle Authentication Errors",
    description = "Handles failed authentication attempts and unauthorized access")
  @ApiResponse(
    responseCode = "401",
    description = "Unauthorized - Authentication failed or not provided",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON_VALUE,
      schema = @Schema(implementation = ErrorDTO.class)
    )
  )
  public ResponseEntity<ErrorDTO> handleAuthenticationException(
    CustomAuthenticationException ex,
    WebRequest request) {
    log.error("Authentication error: {}", ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO(
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(errorDTO);
  }
  
  /**
   * Handles validation-related exceptions.
   * Translates ValidationException into HTTP 400 Bad Request responses.
   * Used when request data fails validation checks.
   *
   * @param ex The validation exception that was thrown
   * @param request The web request during which the exception occurred
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @Operation(summary = "Handle Validation Errors",
    description = "Handles invalid input data and validation failures")
  @ApiResponse(
    responseCode = "400",
    description = "Bad Request - Invalid input data",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON_VALUE,
      schema = @Schema(implementation = ErrorDTO.class)
    )
  )
  public ResponseEntity<ErrorDTO> handleValidationException(
    ValidationException ex,
    WebRequest request) {
    log.error("Validation error: {}", ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO(
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(errorDTO);
  }
  
  /**
   * Handles entity not found exceptions.
   * Translates EntityNotFoundException into HTTP 404 Not Found responses.
   * Used when requested resources cannot be found.
   *
   * @param ex The entity not found exception that was thrown
   * @param request The web request during which the exception occurred
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  @Operation(summary = "Handle Not Found Errors")
  @ApiResponse(
    responseCode = "404",
    description = "Resource not found",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON_VALUE,
      schema = @Schema(implementation = ErrorDTO.class)
    )
  )
  public ResponseEntity<ErrorDTO> handleEntityNotFoundException(
    EntityNotFoundException ex,
    WebRequest request) {
    log.error("Entity not found: {}", ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO(
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(errorDTO);
  }
  
  /**
   * Handles all unhandled exceptions.
   * Translates any unexpected exceptions into HTTP 500 Internal Server Error responses.
   * Provides a generic error message to avoid exposing internal details.
   *
   * @param ex The unexpected exception that was thrown
   * @param request The web request during which the exception occurred
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  @Operation(summary = "Handle Unexpected Errors",
    description = "Handles any unexpected errors that occur during request processing")
  @ApiResponse(
    responseCode = "500",
    description = "Internal Server Error - An unexpected error occurred",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON_VALUE,
      schema = @Schema(implementation = ErrorDTO.class)
    )
  )
  public ResponseEntity<ErrorDTO> handleGlobalException(
    Exception ex,
    WebRequest request) {
    log.error("Unexpected error:", ex);
    ErrorDTO errorDTO = new ErrorDTO(
      "Internal server error",
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(errorDTO);
  }
  
  /**
   * Handles HTTP method not allowed exceptions.
   * Returns HTTP 405 Method Not Allowed response.
   *
   * @param ex The HttpRequestMethodNotSupportedException that was thrown
   * @param request The web request during which the exception occurred
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  @ResponseBody
  public ResponseEntity<ErrorDTO> handleMethodNotAllowed(
    HttpRequestMethodNotSupportedException ex,
    WebRequest request) {
    log.error("Method not allowed: {}", ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO(
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.METHOD_NOT_ALLOWED)
      .body(errorDTO);
  }
  
  /**
   * Handles missing servlet request parameter exceptions.
   * Returns HTTP 400 Bad Request response.
   *
   * @param ex The MissingServletRequestParameterException that was thrown
   * @param request The web request during which the exception occurred
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ErrorDTO> handleMissingParams(
    MissingServletRequestParameterException ex,
    WebRequest request) {
    log.error("Missing parameter: {}", ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO(
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(errorDTO);
  }
  
  /**
   * Handles constraint violation exceptions.
   * Translates ConstraintViolationException into HTTP 400 Bad Request responses.
   * Used when request data violates validation constraints.
   *
   * @param ex      The constraint violation exception that was thrown
   * @param request The web request during which the exception occurred
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @Operation(summary = "Handle Constraint Violation Errors",
    description = "Handles violations of validation constraints")
  @ApiResponse(
    responseCode = "400",
    description = "Bad Request - Validation constraints violated",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON_VALUE,
      schema = @Schema(implementation = ErrorDTO.class)
    )
  )
  public ResponseEntity<ErrorDTO> handleConstraintViolationException(
    ConstraintViolationException ex,
    WebRequest request) {
    log.error("Constraint violation: {}", ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO(
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(errorDTO);
  }
  
  /**
   * Handles authentication exceptions thrown during request processing.
   *
   * @param ex The authentication exception that was thrown
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ErrorDTO> handleAuthenticationException(AuthenticationException ex) {
    log.error("Authentication error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  /**
   * Handles illegal argument exceptions thrown during request processing.
   *
   * @param ex The illegal argument exception that was thrown
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error("Validation error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
  
  /**
   * Handles authentication exceptions thrown during request processing.
   * Returns appropriate error response with authentication failure details.
   *
   * @param ex      The authentication exception that was thrown
   * @param request The web request that triggered the exception
   * @return ResponseEntity containing error details
   */
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ErrorDTO> handleAuthenticationException(
    AccessDeniedException ex,
    WebRequest request) {
    log.error("Authentication error: {}", ex.getMessage());
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(new ErrorDTO(ex.getMessage(), System.currentTimeMillis()));
  }
}