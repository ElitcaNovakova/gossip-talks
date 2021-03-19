package bg.codeacademy.spring.gossiptalks.dto;

import java.time.OffsetDateTime;

public class UserDto {

  private String name;
  private String email;
  private String username;
  private String password;
  private OffsetDateTime registrationTime;
  private OffsetDateTime lastLoginTime;
  private boolean following;

  public String getName() {
    return name;
  }

  public UserDto setName(String name) {
    this.name = name;
    return this;
  }
  public String getEmail() {
    return email;
  }

  public UserDto setEmail(String email) {
    this.email = email;
    return this;
  }
  public String getUsername() {
    return username;
  }

  public UserDto setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public UserDto setPassword(String password) {
    this.password = password;
    return this;
  }

  public OffsetDateTime getRegistrationTime() {
    return registrationTime;
  }

  public UserDto setRegistrationTime(OffsetDateTime registrationTime) {
    this.registrationTime = registrationTime;
    return this;
  }
  public OffsetDateTime getLastLoginTime() {
    return lastLoginTime;
  }

  public UserDto setLastLoginTime(OffsetDateTime lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
    return this;
  }

  public boolean isFollowing() {
    return following;
  }

  public UserDto setFollowing(boolean following) {
    this.following = following;
    return this;
  }
}
