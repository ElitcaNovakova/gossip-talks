package bg.codeacademy.spring.gossiptalks.conttroler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bg.codeacademy.spring.gossiptalks.model.Gossip;
import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.repository.GossipRepository;
import bg.codeacademy.spring.gossiptalks.repository.UserRepository;
import bg.codeacademy.spring.gossiptalks.service.GossipService;
import bg.codeacademy.spring.gossiptalks.service.UserService;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GossipControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  private UserService userService;
  @Autowired
  private GossipService gossipService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private GossipRepository gossipRepository;
  @LocalServerPort
  int port;

  @BeforeEach
  public void beforeEachTest() {
    // init port and logging
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @AfterEach
  public void afterEachTest() {
    RestAssured.reset();
  }

  @Test
  @WithMockUser(username = "user7")
  void given_valid_paging_When_get_list_gossips_Then_pass() throws Exception {
    User user = createUser("user7");
    createUserWithGossip(user);
    mvc.perform(get("/api/v1/users/{username}/gossips", "user7")
        .param("pageNo", "0")
        .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.count").value(10))
        .andExpect(jsonPath("$.content[*].text").isArray())
        .andExpect(jsonPath("$.content[*].text", Matchers
            .containsInAnyOrder("user70", "user71", "user72", "user73", "user74", "user75",
                "user76", "user77", "user78", "user79")));
  }

  @Test
  @WithMockUser
  void given_exception_When_get_gossips_and_username_is_not_valid_Then_fail() throws Exception {
    mvc.perform(get("/api/v1/users/{username}/gossips", "testttt")
        .param("pageNo", "0")
        .param("pageSize", "10"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "user12")
  void given_valid_paging_When_get_gossips_Then_pass() throws Exception {
    User user1 = createUser("user11");
    createUserWithGossip(user1);
    User user2 = createUser("user12");
    userService.followUser(user2, "user11", true);
    mvc.perform(get("/api/v1/gossips")
        .param("pageNo", "2")
        .param("pageSize", "3"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.count").value(3))
        .andExpect(jsonPath("$.content[*].text").isArray())
        .andExpect(jsonPath("$.content[*].text",
            Matchers.containsInAnyOrder("user118", "user117", "user116")));
  }

  @Test
  @WithMockUser(username = "user8")
  void given_save_new_correct_gossip() throws Exception {
    createUser("user8");
    mvc.perform(post("/api/v1/gossips")
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .param("text", "hello")
        .content(
            "{\"text\":\"hello\"}"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "user10")
  void given_save_new_gossip_containing_html_fail() throws Exception {
    createUser("user10");
    mvc.perform(post("/api/v1/gossips")
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .param("text", "<body>text</body>")
        .content(
            "{\"text\":\"hello\"}"))
        .andDo(print())
        .andExpect(status().isInternalServerError());
  }

  private User createUser(String name) {
    return userService
        .register(name, name, name, name + "@abv.bg", "Name", false);
  }

  private User createUserWithGossip(User user) {
    for (int j = 0; j < 10; j++) {
      Gossip gossip = gossipService.createGossip(user.getUsername() + j, user);
    }
    return user;
  }
}