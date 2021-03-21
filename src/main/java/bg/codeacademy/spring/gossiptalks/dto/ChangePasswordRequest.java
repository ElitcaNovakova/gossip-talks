package bg.codeacademy.spring.gossiptalks.dto;

import bg.codeacademy.spring.gossiptalks.validation.ValidPassword;
import javax.validation.constraints.NotNull;

public class ChangePasswordRequest {

  @NotNull
  @ValidPassword
  private String oldPassword;
  @NotNull
  @ValidPassword
  private String newPassword;

  public String getOldPassword() {
    return oldPassword;
  }

  public ChangePasswordRequest setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
    return this;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public ChangePasswordRequest setNewPassword(String newPassword) {
    this.newPassword = newPassword;
    return this;
  }
}

