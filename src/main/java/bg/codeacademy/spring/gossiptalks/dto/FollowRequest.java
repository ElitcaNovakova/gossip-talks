package bg.codeacademy.spring.gossiptalks.dto;

import javax.validation.constraints.NotNull;

public class FollowRequest {

  @NotNull
  private boolean follow;
  public boolean isFollow() {
    return follow;
  }
  public FollowRequest(boolean follow) {
    this.follow = follow;
  }


}