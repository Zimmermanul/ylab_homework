package tests;

import com.mkhabibullin.habitTracker.application.mapper.UserMapper;
import com.mkhabibullin.habitTracker.application.service.UserService;
import com.mkhabibullin.habitTracker.application.validation.AuthenticationValidator;
import com.mkhabibullin.habitTracker.application.validation.UserValidator;
import com.mkhabibullin.habitTracker.domain.exception.CustomAuthenticationException;
import com.mkhabibullin.habitTracker.domain.model.User;
import com.mkhabibullin.habitTracker.presentation.controller.UserRestController;
import com.mkhabibullin.habitTracker.presentation.dto.user.LoginDTO;
import com.mkhabibullin.habitTracker.presentation.dto.user.RegisterUserDTO;
import com.mkhabibullin.habitTracker.presentation.dto.user.UpdateEmailDTO;
import com.mkhabibullin.habitTracker.presentation.dto.user.UserEmailDTO;
import com.mkhabibullin.habitTracker.presentation.dto.user.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRestControllerTest extends BaseTest {
  
  @Mock
  private UserService userService;
  @Mock
  private UserMapper userMapper;
  @Mock
  private UserValidator userValidator;
  @Mock
  private AuthenticationValidator authValidator;
  private UserRestController userController;
  
  @Override
  protected void setupMockMvc() {
    userController = new UserRestController(
      userService, userMapper, userValidator, authValidator);
    mockMvc = buildMockMvc(userController);
  }
  
  @Test
  @DisplayName("Register user with valid data should create user")
  void registerUserWithValidDataShouldCreateUser() throws Exception {
    RegisterUserDTO registerDTO = new RegisterUserDTO(
      TEST_USER_EMAIL,
      TEST_PASSWORD,
      TEST_USER_NAME
    );
    User createdUser = createTestUser();
    UserResponseDTO responseDTO = new UserResponseDTO(
      createdUser.getId(),
      createdUser.getEmail(),
      createdUser.getName(),
      false,
      false
    );
    given(userMapper.registerDtoToUser(registerDTO)).willReturn(createdUser);
    given(userMapper.userToResponseDto(createdUser)).willReturn(responseDTO);
    mockMvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(toJson(registerDTO)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.email").value(TEST_USER_EMAIL))
      .andExpect(jsonPath("$.name").value(TEST_USER_NAME));
    verify(userService).register(
      eq(TEST_USER_EMAIL),
      eq(TEST_PASSWORD),
      eq(TEST_USER_NAME)
    );
  }
  
  @Test
  @DisplayName("Login with valid credentials should authenticate user")
  void loginWithValidCredentialsShouldAuthenticateUser() throws Exception {
    LoginDTO loginDTO = new LoginDTO(TEST_USER_EMAIL, TEST_PASSWORD);
    User user = createTestUser();
    UserResponseDTO responseDTO = new UserResponseDTO(
      user.getId(),
      user.getEmail(),
      user.getName(),
      false,
      false
    );
    given(userService.authenticate(TEST_USER_EMAIL, TEST_PASSWORD))
      .willReturn(true);
    given(userService.getByEmail(TEST_USER_EMAIL)).willReturn(user);
    given(userMapper.userToResponseDto(user)).willReturn(responseDTO);
    mockMvc.perform(post("/api/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(toJson(loginDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value(TEST_USER_EMAIL));
  }
  
  @Test
  @DisplayName("Login with invalid credentials should return unauthorized")
  void loginWithInvalidCredentialsShouldReturnUnauthorized() throws Exception {
    LoginDTO loginDTO = new LoginDTO(TEST_USER_EMAIL, "wrongpassword");
    given(userService.authenticate(TEST_USER_EMAIL, "wrongpassword"))
      .willReturn(false);
    mockMvc.perform(post("/api/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(toJson(loginDTO)))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.message").value("Invalid credentials"));
  }
  
  @Test
  @DisplayName("Logout with valid session should invalidate session")
  void logoutWithValidSessionShouldInvalidateSession() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("user", createTestUser());
    performRequest(post("/api/users/logout")
      .session(session))
      .andExpect(status().isNoContent());
    verify(authValidator).validateSession(any());
  }
  
  @Test
  @DisplayName("Update email with valid data should update email")
  void updateEmailWithValidDataShouldUpdateEmail() throws Exception {
    String newEmail = "newemail@example.com";
    UpdateEmailDTO updateDTO = new UpdateEmailDTO(newEmail);
    User updatedUser = createTestUser();
    updatedUser.setEmail(newEmail);
    UserResponseDTO responseDTO = new UserResponseDTO(
      updatedUser.getId(),
      newEmail,
      updatedUser.getName(),
      false,
      false
    );
    given(userMapper.userToResponseDto(any())).willReturn(responseDTO);
    performRequest(put("/api/users/email")
      .content(toJson(updateDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value(newEmail));
    verify(userService).updateEmail(eq(TEST_USER_ID), eq(newEmail));
  }
  
  @Test
  @DisplayName("Get all users as admin should return all users")
  void getAllUsersAsAdminShouldReturnAllUsers() throws Exception {
    List<User> users = Arrays.asList(
      createTestUser(),
      createTestUser()
    );
    List<UserResponseDTO> userDTOs = Arrays.asList(
      new UserResponseDTO(1L, "user1@example.com", "User 1", false, false),
      new UserResponseDTO(2L, "user2@example.com", "User 2", false, false)
    );
    given(userService.getAll()).willReturn(users);
    given(userMapper.usersToResponseDtos(users)).willReturn(userDTOs);
    performAdminRequest(get("/api/users"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2));
  }
  
  @Test
  @DisplayName("Block user as admin should block user")
  void blockUserAsAdminShouldBlockUser() throws Exception {
    UserEmailDTO emailDTO = new UserEmailDTO(TEST_USER_EMAIL);
    performAdminRequest(put("/api/users/block")
      .content(toJson(emailDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("User blocked successfully"));
    verify(userService).block(TEST_USER_EMAIL);
  }
  
  @Test
  @DisplayName("Block user as non-admin should return forbidden")
  void blockUserAsNonAdminShouldReturnForbidden() throws Exception {
    UserEmailDTO emailDTO = new UserEmailDTO(TEST_USER_EMAIL);
    doThrow(new CustomAuthenticationException("Admin privileges required"))
      .when(authValidator).validateAdminPrivileges(any());
    performRequest(put("/api/users/block")
      .content(toJson(emailDTO)))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.message").value("Admin privileges required"));
  }
}