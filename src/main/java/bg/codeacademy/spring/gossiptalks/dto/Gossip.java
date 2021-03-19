package bg.codeacademy.spring.gossiptalks.dto;


import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


public class Gossip extends CreateGossipRequest {

  @Pattern(regexp = "[A-Z0-9]+")
  @NotNull
  private String id;
  @NotNull
  private String username;
  @NotNull
  private OffsetDateTime dateTime;




  public String getUsername() {
    return username;
  }

  public Gossip setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getId() {
    return id;
  }

  public Gossip setId(String id) {
    this.id = id;
    return this;
  }

  public OffsetDateTime getDateTime() {
    return dateTime;
  }

  public Gossip setDateTime(OffsetDateTime dateTime) {
    this.dateTime = dateTime;
    return this;
  }
}

