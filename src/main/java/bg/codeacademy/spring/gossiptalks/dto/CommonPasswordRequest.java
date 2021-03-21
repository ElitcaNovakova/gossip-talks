package bg.codeacademy.spring.gossiptalks.dto;

import javax.validation.constraints.NotNull;

public class CommonPasswordRequest {

  @NotNull
  private String password;
  @NotNull
  private String passwordConfirmation;

  public String getPassword() {
    return password;
  }

  public CommonPasswordRequest setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getPasswordConfirmation() {
    return passwordConfirmation;
  }

  public CommonPasswordRequest setPasswordConfirmation(String passwordConfirmation) {
    this.passwordConfirmation = passwordConfirmation;
    return this;
  }

}
