package bg.codeacademy.spring.gossiptalks.conttroler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bg.codeacademy.spring.gossiptalks.model.Gossip;
import bg.codeacademy.spring.gossiptalks.service.GossipService;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.repository.GossipRepository;
import bg.codeacademy.spring.gossiptalks.repository.UserRepository;
import bg.codeacademy.spring.gossiptalks.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

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

  @Test
  void given_existing_username_When_register_user_Then_fail() throws Exception {
    createUser("user1");
    mvc.perform(post("/api/v1/users")
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .param("password", "password")
        .param("passwordConfirmation", "password")
        .param("email", "elica123@abv.bg")
        .param("name", "elica")
        .param("username", "user1")
        .content("{\"password\":\"password\",\"passwordConfirmation\":\"password\","
            + "\"email\":\"elica123@abv.bg\",\"username\":\"username6\","
            + " \"name\":\"elica123\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void given_correct_credentials_When_register_user_Then_succeed() throws Exception {

    mvc.perform(post("/api/v1/users")
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .param("password", "password")
        .param("passwordConfirmation", "password")
        .param("email", "elica123@abv.bg")
        .param("name", "elica")
        .param("username", "test11")
        .content("{\"password\":\"password\",\"passwordConfirmation\":\"password\","
            + "\"email\":\"elica123@abv.bg\",\"username\":\"test11\","
            + " \"name\":\"elica123\"}"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void given_the_same_password_When_change_password_Then_fail() throws Exception {
    mvc.perform(post("/api/v1/users/me")
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .param("password", "password")
        .param("passwordConfirmation", "password")
        .param("oldPassword", "password")
        .content("{\"password\":\"password\",\"passwordConfirmation\":\"password\","
            + "\"oldPassword\":\"password\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser
  void given_the_different_password_When_change_password_Then_fail() throws Exception {
    mvc.perform(post("/api/v1/users/me")
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .param("password", "password")
        .param("passwordConfirmation", "password1")
        .param("oldPassword", "password")
        .content("{\"password\":\"password\",\"passwordConfirmation\":\"password1\","
            + "\"oldPassword\":\"password\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser("user2")
  void given_correct_credentials_When_change_password_Then_succeed() throws Exception {
    createUser("user2");
    mvc.perform(post("/api/v1/users/me")
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .param("password", "passwordsNew")
        .param("passwordConfirmation", "passwordsNew")
        .param("oldPassword", "password")
        .content("{\"password\":\"passwordsNew\",\"passwordConfirmation\":\"passwordsNew\","
            + "\"oldPassword\":\"passwordOld\"}"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void given_follow_user_When_username_is_wrong_Then_fail() throws Exception {
    mvc.perform(post("/api/v1/users/{username}/follow", "alabaa")
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .param("follow", "true")
        .content(
            "{\"follow\":\"true\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user3")
  void given_follow_user_When_username_is_right_Then_succeed() throws Exception {
    createUser("user3");
    mvc.perform(post("/api/v1/users/{username}/follow", "user3")
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .param("follow", "true")
        .content(
            "{\"follow\":\"true\"}"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "user4")
  void given_users_when_the_username_is_null_and_follow_is_false() throws Exception {
    createUser("user4");
    mvc.perform(get("/api/v1/users")
        .param("name", "")
        .param("f", "false"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "user5")
  void given_users_when_the_username_is_null_and_follow_is_true() throws Exception {
    createUser("user5");
    mvc.perform(get("/api/v1/users")
        .param("name", "user5")
        .param("f", "true"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "getusercurrent")
  void given_users_when_the_username_is_not_null_and_follow_is_true() throws Exception {

    User user1 = createUser("getusercurrent");

    User user4 = createUser("follow3", "name");
    User user5 = createUser("follow4", "name");
    createUserWithGossip(user4, 3);
    createUserWithGossip(user5, 2);
    user1.getFollowers().add(user4);
    user1.getFollowers().add(user5);
    userRepository.save(user1);
    mvc.perform(get("/api/v1/users")
        .param("name", "follow")
        .param("f", "true"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.[*].username", Matchers
            .containsInAnyOrder("follow3", "follow4")));

  }


  private User createUser(String name) {
    return userService
        .register(name, name, name, name + "@abv.bg", "Name", false);
  }

  private User createUser(String username, String name) {
    return userService
        .register(username, name, name, name + "@abv.bg", "Name", false);
  }

  private User createUserWithGossip(User user) {
    for (int j = 0; j < 10; j++) {
      Gossip gossip = gossipService.createGossip(user.getUsername() + j, user);
    }
    return user;
  }

  private User createUserWithGossip(User user, int num) {
    for (int j = 0; j < num; j++) {
      Gossip gossip = gossipService.createGossip(user.getUsername() + j, user);
    }
    return user;
  }
}