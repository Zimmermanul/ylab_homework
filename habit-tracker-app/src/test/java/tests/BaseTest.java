package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mkhabibullin.domain.model.User;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Base test class providing common functionality for controller tests.
 * Includes setup for MockMvc, ObjectMapper, and common test utilities.
 */
public abstract class BaseTest {
  
  protected MockMvc mockMvc;
  
  @Mock
  protected HttpSession mockSession;
  
  protected final ObjectMapper objectMapper;
  
  // Common test constants
  protected static final Long TEST_USER_ID = 1L;
  protected static final String TEST_USER_EMAIL = "test@example.com";
  protected static final String TEST_USER_NAME = "Test User";
  protected static final String TEST_PASSWORD = "Test@Password123";
  protected static final LocalDateTime TEST_TIMESTAMP = LocalDateTime.now();
  protected static final LocalDate TEST_DATE = LocalDate.now();
  
  protected BaseTest() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }
  
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    setupMockMvc();
    setupCommonMocks();
  }
  
  /**
   * Sets up MockMvc for the specific controller being tested.
   */
  protected abstract void setupMockMvc();
  
  /**
   * Sets up common mocks used across all tests.
   */
  protected void setupCommonMocks() {
    when(mockSession.getAttribute("user")).thenReturn(createTestUser());
  }
  
  /**
   * Creates a test user for use in tests.
   */
  protected User createTestUser() {
    User user = new User(TEST_USER_EMAIL, TEST_USER_NAME);
    user.setId(TEST_USER_ID);
    user.setAdmin(false);
    user.setBlocked(false);
    return user;
  }
  
  /**
   * Creates an admin user for use in tests.
   */
  protected User createAdminUser() {
    User admin = createTestUser();
    admin.setAdmin(true);
    return admin;
  }
  
  /**
   * Performs a request and returns the result actions for further assertions.
   */
  protected ResultActions performRequest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
    return mockMvc.perform(requestBuilder
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .sessionAttr("user", createTestUser()));
  }
  
  /**
   * Performs a request as an admin user.
   */
  protected ResultActions performAdminRequest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
    return mockMvc.perform(requestBuilder
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .sessionAttr("user", createAdminUser()));
  }
  
  /**
   * Checks if the response contains expected error details.
   */
  protected Consumer<ResultActions> expectError(int status, String message) {
    return result -> {
      try {
        result
          .andExpect(jsonPath("$.message").value(message))
          .andExpect(jsonPath("$.timestamp").exists());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }
  
  /**
   * Converts an object to JSON string.
   */
  protected String toJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }
  
  /**
   * Builds MockMvc for a specific controller.
   */
  protected MockMvc buildMockMvc(Object controller) {
    return MockMvcBuilders.standaloneSetup(controller)
      .build();
  }
}