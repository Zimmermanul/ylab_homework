package com.mkhabibullin.app.presentation.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.app.application.mapper.HabitMapper;
import com.mkhabibullin.app.application.validation.HabitMapperValidator;
import com.mkhabibullin.app.common.annotation.Audited;
import com.mkhabibullin.app.domain.exception.AuthenticationException;
import com.mkhabibullin.app.domain.exception.ValidationException;
import com.mkhabibullin.app.domain.model.Habit;
import com.mkhabibullin.app.domain.model.User;
import com.mkhabibullin.app.presentation.controller.HabitController;
import com.mkhabibullin.app.presentation.dto.ErrorDTO;
import com.mkhabibullin.app.presentation.dto.MessageDTO;
import com.mkhabibullin.app.presentation.dto.habit.CreateHabitDTO;
import com.mkhabibullin.app.presentation.dto.habit.HabitFilterDTO;
import com.mkhabibullin.app.presentation.dto.habit.HabitResponseDTO;
import com.mkhabibullin.app.presentation.dto.habit.UpdateHabitDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Servlet handling all habit management operations (CRUD).
 */
@WebServlet("/api/habits/*")
public class HabitManagementServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(HabitManagementServlet.class);
  private final HabitController habitController;
  private final HabitMapperValidator habitValidator;
  private final HabitMapper habitMapper;
  private final ObjectMapper objectMapper;
  private static final String CONTENT_TYPE = "application/json";
  private static final String CHARACTER_ENCODING = "UTF-8";
  
  public HabitManagementServlet(HabitController habitController) {
    this.habitController = habitController;
    this.habitValidator = new HabitMapperValidator();
    this.habitMapper = HabitMapper.INSTANCE;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }
  
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = validateUserAuthentication(request);
      String pathInfo = request.getPathInfo();
      
      if (pathInfo == null || pathInfo.equals("/")) {
        handleCreateHabit(request, response, currentUser);
      } else {
        sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
      }
    } catch (AuthenticationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing POST request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = validateUserAuthentication(request);
      String pathInfo = request.getPathInfo();
      
      if (pathInfo == null || pathInfo.equals("/")) {
        handleGetHabits(request, response, currentUser);
      } else {
        sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
      }
    } catch (AuthenticationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing GET request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = validateUserAuthentication(request);
      String pathInfo = validatePathInfo(request);
      String habitId = pathInfo.substring(1); // Remove leading slash
      
      handleUpdateHabit(request, response, habitId, currentUser);
    } catch (AuthenticationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch (ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing PUT request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = validateUserAuthentication(request);
      String pathInfo = validatePathInfo(request);
      String habitId = pathInfo.substring(1); // Remove leading slash
      
      handleDeleteHabit(request, response, habitId, currentUser);
    } catch (AuthenticationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch (ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing DELETE request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  @Audited(operation = "Create habit")
  private void handleCreateHabit(HttpServletRequest request, HttpServletResponse response, User currentUser)
    throws IOException {
    try {
      CreateHabitDTO createDTO = objectMapper.readValue(request.getReader(), CreateHabitDTO.class);
      habitValidator.validateCreateHabitDTO(createDTO);
      habitController.createHabit(
        currentUser.getEmail(),
        createDTO.name(),
        createDTO.description(),
        createDTO.frequency()
      );
      sendJsonResponse(response, HttpServletResponse.SC_CREATED,
        new MessageDTO("Habit created successfully"));
      logger.info("Created new habit for user {}", currentUser.getId());
    } catch (ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  @Audited(operation = "Get list of habits")
  private void handleGetHabits(HttpServletRequest request, HttpServletResponse response, User currentUser)
    throws IOException {
    try {
      HabitFilterDTO filter = parseHabitFilter(request);
      List<Habit> habits = habitController.viewHabits(
        currentUser.getId(),
        filter.filterDate(),
        filter.active()
      );
      List<HabitResponseDTO> habitDTOs = habitMapper.habitsToResponseDtos(habits);
      sendJsonResponse(response, HttpServletResponse.SC_OK, habitDTOs);
      logger.info("Retrieved habits for user {}", currentUser.getId());
    } catch (ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  @Audited(operation = "Update habit")
  private void handleUpdateHabit(HttpServletRequest request, HttpServletResponse response,
                                 String habitId, User currentUser) throws IOException {
    try {
      validateHabitId(habitId);
      UpdateHabitDTO updateDTO = objectMapper.readValue(request.getReader(), UpdateHabitDTO.class);
      habitValidator.validateUpdateHabitDTO(updateDTO);
      habitController.editHabit(
        habitId,
        updateDTO.name(),
        updateDTO.description(),
        updateDTO.frequency()
      );
      sendJsonResponse(response, HttpServletResponse.SC_OK,
        new MessageDTO("Habit updated successfully"));
      logger.info("Updated habit {} for user {}", habitId, currentUser.getId());
    } catch (ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  @Audited(operation = "Delete habit")
  private void handleDeleteHabit(HttpServletRequest request, HttpServletResponse response,
                                 String habitId, User currentUser) throws IOException {
    try {
      Long habitIdLong = validateHabitId(habitId);
      habitController.deleteHabit(habitIdLong);
      response.setStatus(HttpServletResponse.SC_NO_CONTENT);
      logger.info("Deleted habit {} for user {}", habitId, currentUser.getId());
    } catch (ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private User validateUserAuthentication(HttpServletRequest request) throws AuthenticationException {
    HttpSession session = request.getSession(false);
    User user = session != null ? (User) session.getAttribute("user") : null;
    if (user == null) {
      throw new AuthenticationException("User not authenticated");
    }
    return user;
  }
  
  private String validatePathInfo(HttpServletRequest request) throws ValidationException {
    String pathInfo = request.getPathInfo();
    if (pathInfo == null) {
      throw new ValidationException("Invalid request path");
    }
    return pathInfo;
  }
  
  private Long validateHabitId(String habitId) throws ValidationException {
    try {
      return Long.parseLong(habitId);
    } catch (NumberFormatException e) {
      throw new ValidationException("Invalid habit ID format");
    }
  }
  
  private HabitFilterDTO parseHabitFilter(HttpServletRequest request) throws ValidationException {
    try {
      String dateStr = request.getParameter("date");
      String activeStr = request.getParameter("active");
      
      LocalDate filterDate = dateStr != null ? LocalDate.parse(dateStr) : null;
      Boolean active = activeStr != null ? Boolean.parseBoolean(activeStr) : null;
      
      return new HabitFilterDTO(filterDate, active);
    } catch (DateTimeParseException e) {
      throw new ValidationException("Invalid date format. Use YYYY-MM-DD");
    }
  }
  
  private void sendJsonResponse(HttpServletResponse response, int status, Object data)
    throws IOException {
    response.setStatus(status);
    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(CHARACTER_ENCODING);
    objectMapper.writeValue(response.getOutputStream(), data);
  }
  
  private void sendError(HttpServletResponse response, int status, String message)
    throws IOException {
    response.setStatus(status);
    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(CHARACTER_ENCODING);
    objectMapper.writeValue(response.getOutputStream(),
      new ErrorDTO(message, System.currentTimeMillis()));
  }
}