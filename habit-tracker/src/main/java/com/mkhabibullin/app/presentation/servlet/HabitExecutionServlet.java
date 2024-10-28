package com.mkhabibullin.app.presentation.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.app.application.mapper.HabitExecutionMapper;
import com.mkhabibullin.app.common.annotation.Audited;
import com.mkhabibullin.app.domain.exception.AuthenticationException;
import com.mkhabibullin.app.domain.model.HabitExecution;
import com.mkhabibullin.app.domain.model.User;
import com.mkhabibullin.app.presentation.controller.HabitExecutionController;
import com.mkhabibullin.app.presentation.dto.ErrorDTO;
import com.mkhabibullin.app.presentation.dto.MessageDTO;
import com.mkhabibullin.app.presentation.dto.habitExecution.DateRangeDTO;
import com.mkhabibullin.app.presentation.dto.habitExecution.HabitExecutionRequestDTO;
import com.mkhabibullin.app.presentation.dto.habitExecution.HabitExecutionResponseDTO;
import com.mkhabibullin.app.presentation.dto.habitExecution.HabitProgressReportDTO;
import com.mkhabibullin.app.presentation.dto.habitExecution.HabitStatisticsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servlet handling all habit execution related operations including tracking, statistics, and progress reports.
 */
@WebServlet("/api/habit-executions/*")
public class HabitExecutionServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(HabitExecutionServlet.class);
  private final HabitExecutionController executionController;
  private final HabitExecutionMapper executionMapper;
  private final ObjectMapper objectMapper;
  private static final String CONTENT_TYPE = "application/json";
  private static final String CHARACTER_ENCODING = "UTF-8";
  
  public HabitExecutionServlet(HabitExecutionController executionController) {
    this.executionController = executionController;
    this.executionMapper = HabitExecutionMapper.INSTANCE;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = validateUserAuthentication(request);
      String pathInfo = request.getPathInfo();
      
      if (pathInfo != null && pathInfo.startsWith("/track/")) {
        String habitId = pathInfo.substring("/track/".length());
        handleTrackExecution(request, response, habitId, currentUser);
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
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = validateUserAuthentication(request);
      String pathInfo = request.getPathInfo();
      
      if (pathInfo != null) {
        if (pathInfo.startsWith("/history/")) {
          String habitId = pathInfo.substring("/history/".length());
          handleGetExecutionHistory(request, response, habitId, currentUser);
        } else if (pathInfo.startsWith("/statistics/")) {
          String habitId = pathInfo.substring("/statistics/".length());
          handleGetStatistics(request, response, habitId, currentUser);
        } else if (pathInfo.startsWith("/progress/")) {
          String habitId = pathInfo.substring("/progress/".length());
          handleGetProgressReport(request, response, habitId, currentUser);
        } else {
          sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
        }
      } else {
        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Path info is required");
      }
    } catch (AuthenticationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing GET request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  @Audited(operation = "Track Habit Execution")
  private void handleTrackExecution(HttpServletRequest request, HttpServletResponse response,
                                    String habitId, User currentUser) throws IOException {
    try {
      Long habitIdLong = Long.parseLong(habitId);
      HabitExecutionRequestDTO executionDTO = objectMapper.readValue(
        request.getReader(),
        HabitExecutionRequestDTO.class
      );
      HabitExecution execution = executionMapper.requestDtoToExecution(executionDTO, habitIdLong);
      
      executionController.markHabitExecution(
        execution.getHabitId(),
        execution.getDate(),
        execution.isCompleted()
      );
      sendJsonResponse(response, HttpServletResponse.SC_OK,
        new MessageDTO("Habit execution recorded successfully"));
      logger.info("Recorded execution for habit {} by user {}", habitId, currentUser.getId());
    } catch (IllegalArgumentException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  @Audited(operation = "Get Execution History")
  private void handleGetExecutionHistory(HttpServletRequest request, HttpServletResponse response,
                                         String habitId, User currentUser) throws IOException {
    try {
      Long habitIdLong = Long.parseLong(habitId);
      List<HabitExecution> history = executionController.getHabitExecutionHistory(habitIdLong);
      List<HabitExecutionResponseDTO> historyDTOs = executionMapper.executionsToResponseDtos(history);
      sendJsonResponse(response, HttpServletResponse.SC_OK, historyDTOs);
      logger.info("Retrieved execution history for habit {} by user {}", habitId, currentUser.getId());
    } catch (NumberFormatException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid habit ID format");
    }
  }
  
  @Audited(operation = "Get Execution Statistics")
  private void handleGetStatistics(HttpServletRequest request, HttpServletResponse response,
                                   String habitId, User currentUser) throws IOException {
    try {
      Long habitIdLong = Long.parseLong(habitId);
      DateRangeDTO dateRange = parseDateRange(request);
      List<HabitExecution> history = executionController.getHabitExecutionHistory(habitIdLong);
      List<HabitExecution> filteredHistory = filterHistoryByDateRange(
        history,
        dateRange.startDate(),
        dateRange.endDate()
      );
      int currentStreak = executionController.getCurrentStreak(habitIdLong);
      double successPercentage = executionController.getSuccessPercentage(
        habitIdLong,
        dateRange.startDate(),
        dateRange.endDate()
      );
      Map<DayOfWeek, Long> completionsByDay = calculateCompletionsByDay(filteredHistory);
      HabitStatisticsDTO statistics = executionMapper.createStatisticsDto(
        currentStreak,
        successPercentage,
        filteredHistory.size(),
        filteredHistory.stream().filter(HabitExecution::isCompleted).count(),
        filteredHistory.stream().filter(e -> !e.isCompleted()).count(),
        completionsByDay
      );
      sendJsonResponse(response, HttpServletResponse.SC_OK, statistics);
      logger.info("Retrieved statistics for habit {} by user {}", habitId, currentUser.getId());
    } catch (IllegalArgumentException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  @Audited(operation = "Get Progress Report")
  private void handleGetProgressReport(HttpServletRequest request, HttpServletResponse response,
                                       String habitId, User currentUser) throws IOException {
    try {
      Long habitIdLong = Long.parseLong(habitId);
      DateRangeDTO dateRange = parseDateRange(request);
      
      List<HabitExecution> history = executionController.getHabitExecutionHistory(habitIdLong);
      List<HabitExecution> filteredHistory = filterHistoryByDateRange(
        history,
        dateRange.startDate(),
        dateRange.endDate()
      );
      
      String report = executionController.generateProgressReport(
        habitIdLong,
        dateRange.startDate(),
        dateRange.endDate()
      );
      
      boolean improving = executionController.isImprovingTrend(filteredHistory);
      int longestStreak = executionController.calculateLongestStreak(filteredHistory);
      List<String> suggestions = executionController.generateSuggestions(null, filteredHistory);
      
      HabitProgressReportDTO progressReport = executionMapper.createProgressReportDto(
        report,
        improving,
        longestStreak,
        suggestions
      );
      sendJsonResponse(response, HttpServletResponse.SC_OK, progressReport);
      logger.info("Retrieved progress report for habit {} by user {}", habitId, currentUser.getId());
    } catch (IllegalArgumentException e) {
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
  
  private DateRangeDTO parseDateRange(HttpServletRequest request) {
    try {
      String startDateStr = request.getParameter("startDate");
      String endDateStr = request.getParameter("endDate");
      if (startDateStr == null || endDateStr == null) {
        throw new IllegalArgumentException("Start date and end date are required");
      }
      LocalDate startDate = LocalDate.parse(startDateStr);
      LocalDate endDate = LocalDate.parse(endDateStr);
      if (endDate.isBefore(startDate)) {
        throw new IllegalArgumentException("End date cannot be before start date");
      }
      return new DateRangeDTO(startDate, endDate);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD");
    }
  }
  
  private List<HabitExecution> filterHistoryByDateRange(
    List<HabitExecution> history,
    LocalDate startDate,
    LocalDate endDate) {
    return history.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .sorted(Comparator.comparing(HabitExecution::getDate))
      .collect(Collectors.toList());
  }
  
  private Map<DayOfWeek, Long> calculateCompletionsByDay(List<HabitExecution> history) {
    return history.stream()
      .filter(HabitExecution::isCompleted)
      .collect(Collectors.groupingBy(
        e -> e.getDate().getDayOfWeek(),
        Collectors.counting()
      ));
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