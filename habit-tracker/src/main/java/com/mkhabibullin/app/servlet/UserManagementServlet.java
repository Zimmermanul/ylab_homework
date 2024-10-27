package com.mkhabibullin.app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.app.controller.UserController;
import com.mkhabibullin.app.dto.ErrorDTO;
import com.mkhabibullin.app.dto.MessageDTO;
import com.mkhabibullin.app.dto.user.LoginDTO;
import com.mkhabibullin.app.dto.user.RegisterUserDTO;
import com.mkhabibullin.app.dto.user.UpdateEmailDTO;
import com.mkhabibullin.app.dto.user.UpdateNameDTO;
import com.mkhabibullin.app.dto.user.UpdatePasswordDTO;
import com.mkhabibullin.app.dto.user.UserEmailDTO;
import com.mkhabibullin.app.dto.user.UserResponseDTO;
import com.mkhabibullin.app.exception.AuthenticationException;
import com.mkhabibullin.app.exception.ValidationException;
import com.mkhabibullin.app.mapper.UserMapper;
import com.mkhabibullin.app.model.User;
import com.mkhabibullin.app.validation.AuthenticationValidator;
import com.mkhabibullin.app.validation.UserMapperValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servlet handling all user-related operations including authentication,
 * registration, and profile management.
 */
@WebServlet("/api/users/*")
public class UserManagementServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(UserManagementServlet.class);
  private final UserController userController;
  private final UserMapper userMapper;
  private final ObjectMapper objectMapper;
  
  private static final String CONTENT_TYPE = "application/json";
  private static final String CHARACTER_ENCODING = "UTF-8";
  
  public UserManagementServlet(UserController userController) {
    this.userController = userController;
    this.userMapper = UserMapper.INSTANCE;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }
  
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String pathInfo = request.getPathInfo();
    try {
      if (pathInfo == null || pathInfo.equals("/")) {
        handleRegistration(request, response);
      } else if (pathInfo.equals("/login")) {
        handleLogin(request, response);
      } else if (pathInfo.equals("/logout")) {
        handleLogout(request, response);
      } else {
        sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
      }
    } catch (Exception e) {
      logger.error("Error processing POST request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = AuthenticationValidator.validateAuthentication(request);
      String pathInfo = request.getPathInfo();
      
      if (pathInfo == null) {
        throw new ValidationException("Invalid request path");
      }
      switch (pathInfo) {
        case "/email" -> handleUpdateEmail(request, response, currentUser);
        case "/name" -> handleUpdateName(request, response, currentUser);
        case "/password" -> handleUpdatePassword(request, response, currentUser);
        case "/block" -> {
          AuthenticationValidator.validateAdminPrivileges(currentUser);
          handleBlockUser(request, response, currentUser);
        }
        case "/unblock" -> {
          AuthenticationValidator.validateAdminPrivileges(currentUser);
          handleUnblockUser(request, response, currentUser);
        }
        default -> sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
      }
    } catch (AuthenticationValidator.AuthenticationException |
             AuthenticationValidator.AuthorizationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch (ValidationException | UserMapperValidator.ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing PUT request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = AuthenticationValidator.validateAuthentication(request);
      AuthenticationValidator.validateAdminPrivileges(currentUser);
      
      handleGetAllUsers(request, response);
    } catch (AuthenticationValidator.AuthenticationException |
             AuthenticationValidator.AuthorizationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing GET request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      User currentUser = AuthenticationValidator.validateAuthentication(request);
      String pathInfo = request.getPathInfo();
      
      if (pathInfo == null || pathInfo.equals("/")) {
        throw new ValidationException("Invalid request path");
      }
      String userEmail = pathInfo.substring(1);
      AuthenticationValidator.validateModificationPermission(currentUser, userEmail);
      handleDeleteUser(request, response, userEmail, currentUser);
    } catch (AuthenticationValidator.AuthenticationException |
             AuthenticationValidator.AuthorizationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch (ValidationException | UserMapperValidator.ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing DELETE request", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    }
  }
  
  private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    try {
      RegisterUserDTO registerDTO = objectMapper.readValue(request.getReader(), RegisterUserDTO.class);
      User user = userMapper.registerDtoToUser(registerDTO);
      userController.registerUser(user.getEmail(), registerDTO.password(), user.getName());
      sendJsonResponse(response, HttpServletResponse.SC_CREATED,
        userMapper.userToResponseDto(user));
      logger.info("User registered successfully: {}", user.getEmail());
    } catch (UserMapperValidator.ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private void handleLogin(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    try {
      LoginDTO loginDTO = objectMapper.readValue(request.getReader(), LoginDTO.class);
      
      if (loginDTO.email() == null || loginDTO.email().trim().isEmpty() ||
          loginDTO.password() == null || loginDTO.password().trim().isEmpty()) {
        throw new ValidationException("Email and password are required");
      }
      User user = userController.loginUser(loginDTO.email(), loginDTO.password());
      if (user == null) {
        throw new AuthenticationException("Invalid credentials");
      }
      if (user.isBlocked()) {
        throw new AuthenticationException("Account is blocked");
      }
      HttpSession session = request.getSession(true);
      session.setAttribute("user", user);
      sendJsonResponse(response, HttpServletResponse.SC_OK,
        userMapper.userToResponseDto(user));
      logger.info("User logged in successfully: {}", user.getEmail());
    } catch (ValidationException | AuthenticationException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
  }
  
  private void handleLogout(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
      logger.info("User logged out successfully");
    }
    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }
  
  private void handleGetAllUsers(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    List<User> users = userController.getAllUsers();
    List<UserResponseDTO> userDTOs = users.stream()
      .map(userMapper::userToResponseDto)
      .collect(Collectors.toList());
    sendJsonResponse(response, HttpServletResponse.SC_OK, userDTOs);
    logger.debug("Retrieved all users list");
  }
  
  private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response,
                                String userEmail, User currentUser) throws IOException {
    try {
      userController.deleteUserAccount(userEmail);
      if (currentUser.getEmail().equals(userEmail)) {
        request.getSession().invalidate();
      }
      response.setStatus(HttpServletResponse.SC_NO_CONTENT);
      logger.info("User deleted: {}", userEmail);
    } catch (Exception e) {
      logger.error("Error deleting user", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete user");
    }
  }
  
  private void handleUpdateEmail(HttpServletRequest request, HttpServletResponse response, User currentUser)
    throws IOException {
    try {
      UpdateEmailDTO updateDTO = objectMapper.readValue(request.getReader(), UpdateEmailDTO.class);
      userController.updateUserEmail(currentUser.getId(), updateDTO.newEmail());
      currentUser.setEmail(updateDTO.newEmail());
      sendJsonResponse(response, HttpServletResponse.SC_OK,
        userMapper.userToResponseDto(currentUser));
      logger.info("User email updated: {}", currentUser.getId());
    } catch (UserMapperValidator.ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private void handleUpdateName(HttpServletRequest request, HttpServletResponse response, User currentUser)
    throws IOException {
    try {
      UpdateNameDTO updateDTO = objectMapper.readValue(request.getReader(), UpdateNameDTO.class);
      userController.updateUserName(currentUser.getId(), updateDTO.newName());
      currentUser.setName(updateDTO.newName());
      sendJsonResponse(response, HttpServletResponse.SC_OK,
        userMapper.userToResponseDto(currentUser));
      logger.info("User name updated: {}", currentUser.getId());
    } catch (UserMapperValidator.ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private void handleUpdatePassword(HttpServletRequest request, HttpServletResponse response, User currentUser)
    throws IOException {
    try {
      UpdatePasswordDTO updateDTO = objectMapper.readValue(request.getReader(), UpdatePasswordDTO.class);
      userController.updateUserPassword(currentUser.getId(), updateDTO.newPassword());
      sendJsonResponse(response, HttpServletResponse.SC_OK,
        new MessageDTO("Password updated successfully"));
      logger.info("User password updated: {}", currentUser.getId());
    } catch (UserMapperValidator.ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private void handleBlockUser(HttpServletRequest request, HttpServletResponse response, User currentUser)
    throws IOException {
    try {
      UserEmailDTO userDTO = objectMapper.readValue(request.getReader(), UserEmailDTO.class);
      userController.blockUser(userDTO.email());
      sendJsonResponse(response, HttpServletResponse.SC_OK,
        new MessageDTO("User blocked successfully"));
      logger.info("User blocked: {}", userDTO.email());
    } catch (UserMapperValidator.ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private void handleUnblockUser(HttpServletRequest request, HttpServletResponse response, User currentUser)
    throws IOException {
    try {
      UserEmailDTO userDTO = objectMapper.readValue(request.getReader(), UserEmailDTO.class);
      userController.unblockUser(userDTO.email());
      
      sendJsonResponse(response, HttpServletResponse.SC_OK,
        new MessageDTO("User unblocked successfully"));
      logger.info("User unblocked: {}", userDTO.email());
    } catch (UserMapperValidator.ValidationException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
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