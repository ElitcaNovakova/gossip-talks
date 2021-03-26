package bg.codeacademy.spring.gossiptalks.service;

import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.repository.GossipRepository;
import bg.codeacademy.spring.gossiptalks.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final GossipRepository gossipRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository,
      GossipRepository gossipRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.gossipRepository = gossipRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(s);
    if (user == null) {
      throw new UsernameNotFoundException("The user have to loggin");
    }
    return user;
  }


  public User register(String userName, String userPassword, String passConfirmation,
      String userEmail,
      String name, boolean folowing) {
    if (!userPassword.equals(passConfirmation)) {
      throw new IllegalArgumentException("The password doesn't match");
    }
    if (userRepository.findByUsername(userName) != null) {
      throw new IllegalArgumentException("The username already exist");
    }

    return userRepository.save(new User()
        .setUsername(userName)
        .setPassword(passwordEncoder.encode(userPassword))
        .setRegistrationTime(OffsetDateTime.now())
        .setFollowers(new HashSet<User>())
        .setLastLoginTime(OffsetDateTime.now())
        .setEmail(userEmail)
        .setName(name)
    );
  }

  public User changePassword(String currentName, String newPassword, String passwordConfirmation,
      String oldPassword) {

    if (!passwordConfirmation.equals(newPassword)) {
      throw new IllegalArgumentException("The password doesn't match");
    }
    if (newPassword.equals(oldPassword)) {
      throw new IllegalArgumentException("The passwords are the same");
    }
    //set new password and save
    User user = requireUser(currentName);
    if (user == null) {
      throw new UsernameNotFoundException("The user have to loggin");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    return userRepository.save(user);
  }

  public User followUser(User currentUser, String username, boolean follow) {
    // make sure if the user exist
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("The user not found");
    }
    if (follow) {
      //add User to Followers
      user.getFollowers().add(currentUser);

    } else {
      //remove from Followers
      user.getFollowers().remove(currentUser);
    }
    return userRepository.save(user);
  }

  public List<User> getUsers(User currentUser) {
    return getUsers(currentUser, "", false);
  }

  public List<User> getUsers(User currentUser, String name, boolean f) {
    Set<User> userList = new HashSet<User>();
    if (!f) {

     userList = userRepository.findByUsernameOrNameContainingAllIgnoreCase(name,name);
    } else {
      if (name != null) {
        String match = name.toUpperCase();
        userList = currentUser.getFollowers().stream()
            .filter(user ->
                (user.getUsername().toUpperCase().contains(match)) ||
                    (user.getName().toUpperCase().contains(match)))
            .collect(Collectors.toSet());
      }
    }
    return userList.stream()
        .sorted((User u1, User u2) ->
            (u1.getNumberGossip() < u2.getNumberGossip()) ?
                -1 : 1)
        .collect(Collectors.toList());
  }


  public User requireUser(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("The user have to loggin");
    }
    return user;
  }
}
