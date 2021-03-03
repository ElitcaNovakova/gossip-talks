package bg.codeacademy.spring.gossiptalks.service;

import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Page<User> listUsers(Pageable pageable, String name, boolean follow) {
    if (follow){
      return  userRepository.findByNameAndFollowTrue(name);
    }else{
      return userRepository.findByName(name);
    }
  }
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    // create admin user
    if (userRepository.count() == 0) {
      register("admin", "admin", "admin","admin.admin@gmail", "Admin",  false);
    }
    Optional<User> user = userRepository.findByUsername(s);
    if (!user.isPresent()) {
      throw new UsernameNotFoundException("Not Found.");
    }
    User realUser = user.get();
    realUser.setLastLoginTime(OffsetDateTime.now());
    return userRepository.save(realUser);
  }
  public User register(String userName, String userPassword, String passConfirmation, String userEmail,
      String name, boolean following) {
    if (!userPassword.equals(passConfirmation)) {
      throw new IllegalArgumentException("The password doesn't match");
    }
    if (userRepository.findByUsername(userName).isPresent()) {
      throw new IllegalArgumentException("The username already exist");
    }
    User user = new User();
    user.setUsername(userName);
    user.setPassword(passwordEncoder.encode(userPassword));
    user.setRegistrationTime(OffsetDateTime.now());
    user.setEmail(userEmail);
    user.setName(name);
    user.setFollowing(following);
    return userRepository.save(user);
  }
  public User changePassword(long userID, String oldPassword, String newPassword) {
    return changePassword(userRepository.getOne(userID), oldPassword, newPassword);
  }
  public User changePassword(User user, String oldPassword, String newPassword) {
    //validate old password
    String currentPassHash = user.getPassword();
    if (!passwordEncoder.matches(oldPassword, currentPassHash)) {
      throw new IllegalArgumentException("The password doesn't match");
    }
    if (passwordEncoder.matches(newPassword, currentPassHash)) {
      throw new IllegalArgumentException("The passwords are the same");
    }
    //set new password and save
    user.setPassword(passwordEncoder.encode(newPassword));
    return userRepository.save(user);
  }
}
