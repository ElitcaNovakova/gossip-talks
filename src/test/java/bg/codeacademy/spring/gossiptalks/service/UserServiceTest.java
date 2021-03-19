package bg.codeacademy.spring.gossiptalks.service;
import bg.codeacademy.spring.gossiptalks.model.Gossip;
import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.repository.GossipRepository;
import bg.codeacademy.spring.gossiptalks.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private GossipRepository gossipRepository;
  private UserService userService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    userService = new UserService(userRepository, gossipRepository,
        NoOpPasswordEncoder.getInstance());
  }

  @AfterEach
  void tearDown() {

  }

  private static User newUser(String email) {
    String name = email.split("@")[0];
    return new User().setEmail(email)
        .setUsername(name)
        .setName(name)
        .setId(1)
        .setRegistrationTime(OffsetDateTime.now())
        .setLastLoginTime(OffsetDateTime.now())
        .setPassword(name)
        .setFollowers(new HashSet<User>());
  }

  private static Gossip newGossip(String text) {

    return new Gossip().setText(text)
        .setDateTime(OffsetDateTime.now());
  }

  @Test
  void when_user_exist_Then_unfollow_user_Must_succeed() {
    // 1.setup current user
    User current = newUser("user1@abv.bg");
    // 2.setup user to follow
    User toUnfollow = newUser("toFollow@abv.bg");
    // 3. setup db mock to return 1 & 2
    // казвам на мокнатото repository какво да прави
    // когато се викне findByUser трябва да върне toFollow
    when(userRepository.findByUsername(any()))
        .thenReturn(toUnfollow);
    // 4. call follow
    userService.followUser(current, "toFollow", false);
    // 5. validate current.user.followers contains user 2
    assertFalse(toUnfollow.getFollowers().contains(current));
    // 6. validate that db.save() is called
    // validate that save is called exactly 1 times
    // validate that save is called with parameter 'current'
    verify(userRepository, times(1)).save(toUnfollow);
  }

  @Test
  void when_user_do_not_exist_Then_follow_user_Must_throw_exception() {
    // 1.setup current user
    User current = newUser("user1@abv.bg");

    // repository is not setup, so any username will return null (not found)
    // when not found - assert exception is thrown
    assertThrows(UsernameNotFoundException.class, () -> {
      userService.followUser(current, "alabal", true);
    });
  }

  @Test
  void when_user_exist_Then_follow_user_Must_succeed() {
    // 1.setup current user
    User current = newUser("user1@abv.bg");
    // 2.setup user to follow
    User toFollow = newUser("toFollow@abv.bg");
    // 3. setup db mock to return 1 & 2
    // казвам на мокнатото repository какво да прави
    // когато се викне findByUser трябва да върне toFollow
    when(userRepository.findByUsername(any()))
        .thenReturn(toFollow);
    // 4. call follow
    userService.followUser(current, "toFollow", true);
    // 5. validate current.user.followers contains user 2
    assertTrue(toFollow.getFollowers().contains(current));
    // 6. validate that db.save() is called
    // validate that save is called exactly 1 times
    // validate that save is called with parameter 'current'
    verify(userRepository, times(1)).save(toFollow);
  }

  @Test
  void when_passwordConfirmation_doesnt_match_to_new_password() {
    assertThrows(IllegalArgumentException.class, () -> {
      userService.changePassword("dummy", "newPassword", "alab", "alabala");
    });
    // save must not be called
    verify(userRepository, times(0)).save(any());
  }

  @Test
  void when_old_pass_match_to_new_password() {
    assertThrows(IllegalArgumentException.class, () -> {
      userService.changePassword("dummy", "newPassword", "newPassword", "newPassword");
    });
    // save must not be called
    verify(userRepository, times(0)).save(any());
  }

  @Test
  void when_user_exist_And_password_Match_to_passwordConfirmation_changePassword_Must_succeed() {
    User current = newUser("xyz@abv.bg");
    String newPass = "newPass";
    // казвам на мокнатото repository какво да прави
    // когато се викне findByUser трябва да върне current
    when(userRepository.findByUsername(any()))
        .thenReturn(current);
    userService.changePassword("newPass", newPass, newPass, "xyz");
    verify(userRepository, times(1)).save(current);
    assertEquals(newPass, current.getPassword());
  }

  @Test
  void when_register_new_user_And_the_username_is_already_in_use() {
    User existUser = newUser("user1@abv.bg");
    when(userRepository.findByUsername(any()))
        .thenReturn(existUser);
    assertThrows(IllegalArgumentException.class, () -> {
      userService.register("user1", "password", "password",
          "user1@abv.bg",
          "user1", false);
    });
  }

  @Test
  void when_register_new_user_And_the_password_doest_match_to_password_confirmation() {
    assertThrows(IllegalArgumentException.class, () -> {
      userService.register("user1", "password1", "password2",
          "user1@abv.bg",
          "user1", false);
    });
  }

  @Test
  void when_register_new_user_And_credential_are_ok() {
    when(userRepository.save(any()))
        .thenAnswer(i -> i.getArgument(0));
    User user = userService.register(
        "username",
        "pass1",
        "pass1",
        "something@mail.com",
        "Ivan Ivanov",
        false);
    assertEquals("username", user.getUsername());
    assertEquals("pass1", user.getPassword());
    assertEquals("something@mail.com", user.getEmail());
    assertEquals("Ivan Ivanov", user.getName());
    verify(userRepository, times(1)).save(any());
  }

  @Test
  void when_user_get_follow_is_true_and_username_is_ok() {
    User current = newUser("current@abv.bg");
    User followUser = newUser("follow@abv.bg");
    when(userRepository.findByUsername(any()))
        .thenReturn(followUser);
    when(userRepository.save(any()))
        .thenAnswer(i -> i.getArgument(0));
    userService.followUser(current, "follow", true);
    assertTrue(followUser.getFollowers().contains(current));
    verify(userRepository, times(1)).save(any());
  }

  @Test
  void when_user_get_follow_is_false_and_username_is_ok() {
    User current = newUser("current@abv.bg");
    User followUser = newUser("follow@abv.bg");
    followUser.getFollowers().add(current);
    when(userRepository.findByUsername(any()))
        .thenReturn(followUser);
    when(userRepository.save(any()))
        .thenAnswer(i -> i.getArgument(0));
    userService.followUser(current, "follow", false);
    assertFalse(followUser.getFollowers().contains(current));
    verify(userRepository, times(1)).save(any());
  }

  @Test
  void when_user_get_follow_is_true_and_username_is_not_ok() {
    User current = newUser("current@abv.bg");
    assertThrows(UsernameNotFoundException.class, () -> {
      userService.followUser(current, "follow", false);
    });
    verify(userRepository, times(0)).save(any());
  }

  @Test
  void when_get_users_if_follow_is_false_and_username_is_null() {
    User current = newUser("current@abv.bg");
    userService.getUsers(current, "", false);
    verify(userRepository, times(1)).findByUsernameContainsIgnoreCase(any());
    verify(userRepository, times(1)).findByNameContainsIgnoreCase(any());
  }

  @Test
  void when_get_users_and_follow_is_true_and_username_is_not_null() {
    User current = newUser("current@abv.bg");
    List<Gossip> gossips1 = new ArrayList<Gossip>();
    List<Gossip> gossips2 = new ArrayList<Gossip>();
    User user1 = new User().setUsername("alalala").setName("Follow").setPassword("pass")
        .setEmail("current@abv.bg");
    User user2 = new User().setUsername("alalal1").setName("follow").setPassword("pass")
        .setEmail("current@abv.bg");
    current.getFollowers().add(user1);
    current.getFollowers().add(user2);
    Gossip gossip1 = newGossip("text1").setAuthor(user1);
    Gossip gossip2 = newGossip("text2").setAuthor(user1);
    gossips1.add(gossip1);
    gossips1.add(gossip2);
    Gossip gossip3 = newGossip("text1").setAuthor(user2);
    Gossip gossip4 = newGossip("text2").setAuthor(user2);
    Gossip gossip5 = newGossip("text2").setAuthor(user2);
    gossips2.add(gossip3);
    gossips2.add(gossip4);
    gossips2.add(gossip5);
    when(gossipRepository.findByAuthor_Id(user1.getId()))
        .thenReturn(gossips1);
    when(gossipRepository.findByAuthor_Id(user2.getId()))
        .thenReturn(gossips2);
    List<User> userList = userService.getUsers(current, "follow", true);

    assertEquals(userList.size(), 2);
    assertTrue(userList.get(0).equals(user2) && userList.get(1).equals(user1));
    verify(gossipRepository, times(2)).findByAuthor_Id(any());
  }
}