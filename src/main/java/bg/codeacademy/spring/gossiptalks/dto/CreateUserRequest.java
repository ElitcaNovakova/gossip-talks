package bg.codeacademy.spring.gossiptalks.dto;

import bg.codeacademy.spring.gossiptalks.validation.ValidPassword;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


public class CreateUserRequest {


  @Pattern(regexp = "^[a-z0-8\\.\\-]+$")
  @NotNull
  private String username;
  @NotNull
  @ValidPassword
  private String password;
  @NotNull
  @ValidPassword
  private String passwordConfirmation;
  private String name;
  @NotNull
  @Email
  private String email;
  private boolean following;

  public String getUsername() {
    return username;
  }

  public CreateUserRequest setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public CreateUserRequest setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getPasswordConfirmation() {
    return passwordConfirmation;
  }

  public CreateUserRequest setPasswordConfirmation(String passwordConfirmation) {
    this.passwordConfirmation = passwordConfirmation;
    return this;
  }

  public String getName() {
    return name;
  }

  public CreateUserRequest setName(String name) {
    this.name = name;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public CreateUserRequest setEmail(String email) {
    this.email = email;
    return this;
  }

  public boolean isFollowing() {
    return following;
  }

  public CreateUserRequest setFollowing(boolean following) {
    this.following = following;
    return this;
  }

}

