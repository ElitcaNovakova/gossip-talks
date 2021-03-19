package bg.codeacademy.spring.gossiptalks.model;

import bg.codeacademy.spring.gossiptalks.validation.ValidText;
import java.time.OffsetDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Gossip {


  @Id
  @GeneratedValue
  private Long id;
  @NotNull
  @Size(min = 2, max = 255)
  @ValidText
  private String text;
  @NotNull
  private OffsetDateTime dateTime;
  @ManyToOne
  private User author;

  public Long getId() {
    return id;
  }


  public Gossip setId(Long id) {
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

  public String getText() {
    return text;
  }

  public Gossip setText(String text) {
    this.text = text;
    return this;
  }

  public User getAuthor() {
    return author;
  }

  public Gossip setAuthor(User author) {
    this.author = author;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Gossip gossip = (Gossip) o;
    return
        text.equals(gossip.text) &&
        dateTime.equals(gossip.dateTime);
  }

  @Override
  public int hashCode() {//TODO
    return Objects.hash(text, id, dateTime);
  }
}
