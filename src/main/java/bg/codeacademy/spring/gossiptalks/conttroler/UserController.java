package bg.codeacademy.spring.gossiptalks.conttroler;


import bg.codeacademy.spring.gossiptalks.dto.GossipList;
import bg.codeacademy.spring.gossiptalks.dto.UserDto;
import bg.codeacademy.spring.gossiptalks.dto.UserResponse;
import bg.codeacademy.spring.gossiptalks.model.Gossip;
import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.service.GossipService;
import bg.codeacademy.spring.gossiptalks.service.UserService;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.stream.Stream;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private UserService userService;
  private final GossipService gossipService;


  public UserController(UserService userService,
      GossipService gossipService) {
    this.userService = userService;
    this.gossipService = gossipService;
  }

  @Multipart
  @PostMapping
  public UserResponse createUser(
      @Valid @RequestParam(value = "password", required = true) String password,
      @Valid @RequestParam(value = "passwordConfirmation", required = true) String passwordConfirmation,
      @Valid @RequestParam(value = "email", required = true) String email,
      @Valid @ApiParam(value = "", required = true) @RequestParam(value = "username", required = true) String username,
      @RequestParam(value = "name", required = false) String name) {
    User newUser = userService.register(username, password,
        passwordConfirmation, email,
        name, false);
    return toDTO(newUser, newUser);
  }

  @GetMapping
  public UserResponse[] getUsers(
      @NotNull @RequestParam(value = "name", required = false) String name,
      @RequestParam(name = "f", required = false, defaultValue = "false") boolean f) {

    List<User> users;
    User userCurrent = userService.requireUser(getCurrentUserName());
    if (name == null) {
      users = userService.getUsers(userCurrent);
    } else {
      users = userService.getUsers(userCurrent, name, f);
    }
    User current = userService.requireUser(getCurrentUserName());
    return users.stream()
        .map(user -> toDTO(user, current))
        .toArray(UserResponse[]::new);
  }

  @Multipart
  @PostMapping(consumes = {"multipart/form-data"}, value = {"/{username}/follow"})
  UserResponse followUser(
      @PathVariable("username") String username,
      @RequestParam("follow") boolean follow
  ) {
    String name = getCurrentUserName();
    User currentUser = userService.requireUser(name);
    User toFollow = userService.followUser(currentUser, username, follow);
    return toDTO(toFollow, currentUser);
  }

  @Multipart
  @PostMapping(consumes = {"multipart/form-data"}, value = {"/me"})
  public UserResponse changeCurrentUserPassword(
      @RequestParam(value = "password", required = true) String password,
      @RequestParam(value = "passwordConfirmation", required = true) String passwordConfirmation,
      @RequestParam(value = "oldPassword", required = true) String oldPassword) {
    User currentUser = userService
        .changePassword(getCurrentUserName(), password, passwordConfirmation, oldPassword);
    return toDTO(currentUser, currentUser);

  }

  @GetMapping("/me")
  public UserResponse currentUser() {
    String name = getCurrentUserName();
    User currentUser = userService.requireUser(name);
    return toDTO(currentUser, currentUser);

  }

  @GetMapping("/{username}/gossips")
  public GossipList getUserGossips(
      @Min(0) @RequestParam(value = "pageNo", required = false, defaultValue = "0") Integer pageNo,
      @Min(0) @Max(50) @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
      @PathVariable(value = "username", required = true) String username
  ) {
    Page<Gossip> gossips = gossipService.getGossipsByAuthor(pageNo, pageSize, username);
    return GossipController.toDTO(gossips);
  }

  static UserResponse toDTO(User user, User current) {
    return new UserResponse()
        .setEmail(user.getEmail())
        .setName(user.getName())
        .setFollowing(user.getFollowers().contains(current))
        .setUsername(user.getUsername()
        );
  }


  static String getCurrentUserName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      username = authentication.getName();
    }
    return username;
  }
}
