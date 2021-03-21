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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class GossipServiceTest {

  public static Long id = Long.valueOf(0);
  @Mock
  private UserRepository userRepository;
  @Mock
  private GossipRepository gossipRepository;
  private UserService userService;
  private GossipService gossipService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    gossipService = new GossipService(userRepository, gossipRepository);
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void when_post_new_gossip_with_correct_text_Must_succeed() {
    User current = newUser("user1@abv.bg");
    Gossip gossip = new Gossip().setAuthor(current).setText("alabala")
        .setDateTime(OffsetDateTime.now());
    ArrayList<Gossip> gossips = new ArrayList<Gossip>();
    gossips.add(gossip);
    when(gossipRepository.save(any()))
        .thenReturn(gossip);
    when(gossipRepository.findByAuthor_Id(any()))
        .thenReturn(gossips);
    gossipService.createGossip("alabala", current);
    verify(gossipRepository, times(1)).save(any());
  }

  @Test
  void when_get_user_existing_gossips_And_sort_by_oldest_Must_succeed() {
    User current = newUser("current@abv.bg");
    Gossip gossip1 = newGossip("text1").setAuthor(current);
    Gossip gossip2 = newGossip("text2").setAuthor(current);
    Pageable pageable = PageRequest.of(0, 2, Sort.by("Id"));
    ArrayList<Gossip> list = new ArrayList<Gossip>();
    list.add(gossip1);
    list.add(gossip2);
    Page<Gossip> page = new PageImpl<Gossip>(list, pageable, 2);
    when(gossipRepository.findByAuthor_Username(any(), any()))
        .thenReturn(page);
    page = gossipService.getGossipsByAuthor(0, 2, "current");
    assertEquals(page.getContent().get(0).getAuthor().getUsername(), "current");
    verify(gossipRepository, times(1)).findByAuthor_Username(any(), any());
  }

  @Test
  void when_get_gossips_to_the_users_which_follow_you_And_sort_Must_succeed() {
    User current = newUser("current@abv.bg");
    List<User> friends = new ArrayList<User>();
    List<Gossip> gossips = new ArrayList<Gossip>();
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("Id"));
    Page<Gossip> pages = new PageImpl<Gossip>(gossips, pageRequest, 3);
    when(userRepository.findByFollowers_Id(any()))
        .thenReturn(friends);
    when(gossipRepository.findByAuthorIn(any(), any()))
        .thenReturn(pages);
    Page<Gossip> gossipPage = gossipService.getGossips(0, 3, current);
    verify(userRepository, times(1)).findByFollowers_Id(any());
    verify(gossipRepository, times(1)).findByAuthorIn(any(), any());

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
    id++;
    return new Gossip().setText(text)
        .setDateTime(OffsetDateTime.now()).setId(id);
  }

}