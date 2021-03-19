package bg.codeacademy.spring.gossiptalks.dto;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserResponse {

  @NotNull
  @Email
  private String email;
  @NotNull
  @Pattern(regexp = "^[a-z0-8\\\\.\\-]+$")
  private String username;
  private String name;
  private boolean following;


  public String getEmail() {
    return email;
  }

  public UserResponse setEmail(String email) {
    this.email = email;
    return this;
  }
  public String getUsername() {
    return username;
  }

  public UserResponse setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getName() {
    return name;
  }

  public UserResponse setName(String name) {
    this.name = name;
    return this;
  }

  public boolean isFollowing() {
    return following;
  }

  public UserResponse setFollowing(boolean following) {
    this.following = following;
    return this;
  }
}

