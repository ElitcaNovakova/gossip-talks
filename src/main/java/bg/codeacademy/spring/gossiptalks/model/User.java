package bg.codeacademy.spring.gossiptalks.model;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class User implements UserDetails {

  @Id
  @GeneratedValue
  private long id;

  private String name;
  @NotNull
  @Email
  private String email;
  @NotNull
  @Pattern(regexp = "^[a-z0-8\\\\.\\-]+$")
  @Column(unique = true)
  private String username;
  @NotNull
  @Size(max = 1024)
  private String password;
  @NotNull
  private OffsetDateTime registrationTime;
  private OffsetDateTime lastLoginTime;
  private int numberGossip;

  @ManyToMany
  private Set<User> followers;

  public User setId(long id) {
    this.id = id;
    return this;
  }

  public long getId() {
    return id;
  }

  public User setId(int id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public User setName(String name) {
    this.name = name;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public User setEmail(String email) {
    this.email = email;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public User setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public User setPassword(String password) {
    this.password = password;
    return this;
  }

  public OffsetDateTime getLastLoginTime() {
    return lastLoginTime;
  }

  public User setLastLoginTime(OffsetDateTime lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
    return this;
  }

  public OffsetDateTime getRegistrationTime() {
    return registrationTime;
  }

  public User setRegistrationTime(OffsetDateTime registrationTime) {
    this.registrationTime = registrationTime;
    return this;
  }

  public Set<User> getFollowers() {
    return followers;
  }

  public User setFollowers(Set<User> followers) {
    this.followers = followers;
    return this;
  }

  public int getNumberGossip() {
    return numberGossip;
  }

  public void setNumberGossip(int numberGossip) {
    this.numberGossip = numberGossip;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", registrationTime=" + registrationTime +
        ", lastLoginTime=" + lastLoginTime +
        ", followers=" + followers +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return id == user.id &&
        username.equals(user.username) &&
        password.equals(user.password) &&
        email.equals(user.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, password, email);
  }
}