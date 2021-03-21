package bg.codeacademy.spring.gossiptalks.service;

import bg.codeacademy.spring.gossiptalks.model.Gossip;
import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.repository.GossipRepository;
import bg.codeacademy.spring.gossiptalks.repository.UserRepository;
import bg.codeacademy.spring.gossiptalks.validation.ValidText;
import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Service
@Validated
public class GossipService {

  private final UserRepository userRepository;
  private final GossipRepository gossipRepository;


  public GossipService(UserRepository userRepository, GossipRepository gossipRepository) {
    this.userRepository = userRepository;
    this.gossipRepository = gossipRepository;
  }

  // find gossips of particular user
  public Page<Gossip> getGossipsByAuthor(int pageNo, int pageSize, String author) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
    return gossipRepository.findByAuthor_Username(author, pageable);
  }


  // find gossips of the friends
  public Page<Gossip> getGossips(Integer pageNo, Integer pageSize, @NotNull User user) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
    // 1. get Users, that you follow
    List<User> friends = userRepository.findByFollowers_Id(user.getId());
    // 2. get Gossips of those Users
    return gossipRepository.findByAuthorIn(friends, pageable);
  }

  public Gossip createGossip(@ValidText String text, User user) {
    OffsetDateTime dateTime = OffsetDateTime.now();
    user.setNumberGossip(user.getNumberGossip()+1);
    return gossipRepository.save(new Gossip()
        .setAuthor(user)
        .setDateTime(dateTime)
        .setText(text));

  }
}
