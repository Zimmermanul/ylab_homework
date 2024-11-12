package tests;

import com.mkhabibullin.habitTracker.domain.model.User;
import com.mkhabibullin.habitTracker.presentation.controller.SystemRestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SystemRestControllerTest extends BaseTest {
  
  private SystemRestController systemController;
  
  @Override
  protected void setupMockMvc() {
    systemController = new SystemRestController();
    mockMvc = buildMockMvc(systemController);
  }
  
  @DisplayName("Should return full system status including user details for authenticated user")
  @Test
  void getStatusWithAuthenticatedUserShouldReturnFullStatus() throws Exception {
    User testUser = createTestUser();
    performRequest(get("/api/system/status"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.status", is("running")))
      .andExpect(jsonPath("$.timestamp", notNullValue()))
      .andExpect(jsonPath("$.startupTime", notNullValue()))
      .andExpect(jsonPath("$.uptime", notNullValue()))
      .andExpect(jsonPath("$.uptime.days", notNullValue()))
      .andExpect(jsonPath("$.uptime.hours", notNullValue()))
      .andExpect(jsonPath("$.uptime.minutes", notNullValue()))
      .andExpect(jsonPath("$.uptime.seconds", notNullValue()))
      .andExpect(jsonPath("$.user.id", is(testUser.getId().intValue())))
      .andExpect(jsonPath("$.user.email", is(testUser.getEmail())))
      .andExpect(jsonPath("$.user.name", is(testUser.getName())))
      .andExpect(jsonPath("$.user.admin", is(testUser.isAdmin())))
      .andExpect(jsonPath("$.authenticated", is(true)));
  }
  
  @DisplayName("Should return basic system status without user details for unauthenticated request")
  @Test
  void getStatusWithoutUserShouldReturnBasicStatus() throws Exception {
    mockMvc.perform(get("/api/system/status")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.status", is("running")))
      .andExpect(jsonPath("$.timestamp", notNullValue()))
      .andExpect(jsonPath("$.startupTime", notNullValue()))
      .andExpect(jsonPath("$.uptime", notNullValue()))
      .andExpect(jsonPath("$.user").doesNotExist())
      .andExpect(jsonPath("$.authenticated", is(false)));
  }
  
  @DisplayName("Should return health status with all component details")
  @Test
  void getHealthShouldReturnHealthStatus() throws Exception {
    performRequest(get("/api/system/health"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.status", is("healthy")))
      .andExpect(jsonPath("$.timestamp", notNullValue()))
      .andExpect(jsonPath("$.components.database.status", is("up")))
      .andExpect(jsonPath("$.components.session.status", is("up")))
      .andExpect(jsonPath("$.components.memory.status", is("up")))
      .andExpect(jsonPath("$.components.memory.details.total", notNullValue()))
      .andExpect(jsonPath("$.components.memory.details.free", notNullValue()))
      .andExpect(jsonPath("$.components.memory.details.max", notNullValue()));
  }
  
  @DisplayName("Should return basic application information")
  @Test
  void getInfoShouldReturnApplicationInfo() throws Exception {
    performRequest(get("/api/system/info"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name", is("Habit Tracker")))
      .andExpect(jsonPath("$.version", is("1.0")));
  }
  
  @DisplayName("Should return identical responses for main and alternative status endpoints")
  @Test
  void alternativeStatusEndpointShouldReturnSameAsMainEndpoint() throws Exception {
    ResultActions mainEndpoint = performRequest(get("/api/system/status"));
    ResultActions alternativeEndpoint = performRequest(get("/api/system/"));
    String mainResponse = mainEndpoint.andReturn().getResponse().getContentAsString();
    String alternativeResponse = alternativeEndpoint.andReturn().getResponse().getContentAsString();
    org.assertj.core.api.Assertions.assertThat(mainResponse)
      .isEqualTo(alternativeResponse);
  }
}