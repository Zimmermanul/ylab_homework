package com.mkhabibullin.presentation.controller;


import com.mkhabibullin.presentation.dto.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
@Tag(name = "Error Handling", description = "Endpoints for handling various error scenarios")
public class ErrorController {
  
  @Operation(
    summary = "Handle 404 Not Found",
    description = "Returns a standard error response for resources that cannot be found"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Resource not found error",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON_VALUE,
      schema = @Schema(implementation = ErrorDTO.class)
    )
  )
  @RequestMapping("/404")
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO handle404() {
    return new ErrorDTO("Resource not found", System.currentTimeMillis());
  }
  
  @Operation(
    summary = "Handle 500 Internal Server Error",
    description = "Returns a standard error response for internal server errors"
  )
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON_VALUE,
      schema = @Schema(implementation = ErrorDTO.class)
    )
  )
  @RequestMapping("/500")
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO handle500() {
    return new ErrorDTO("Internal server error", System.currentTimeMillis());
  }
  
  @Operation(
    summary = "Handle Generic Error",
    description = "Returns a standard error response for unspecified error scenarios"
  )
  @ApiResponse(
    responseCode = "500",
    description = "Generic error response",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON_VALUE,
      schema = @Schema(implementation = ErrorDTO.class)
    )
  )
  @RequestMapping
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO handleError() {
    return new ErrorDTO("An error occurred", System.currentTimeMillis());
  }
}