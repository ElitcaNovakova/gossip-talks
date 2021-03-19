package bg.codeacademy.spring.gossiptalks.service;

import bg.codeacademy.spring.gossiptalks.model.Gossip;
import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.repository.GossipRepository;
import bg.codeacademy.spring.gossiptalks.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GossipService {

  private final UserRepository userRepository;
  private final GossipRepository gossipRepository;


  public GossipService(UserRepository userRepository,
      GossipRepository gossipRepository) {
    this.userRepository = userRepository;
    this.gossipRepository = gossipRepository;

  }

  // find gossips of particular user
  public Page<Gossip> getGossipsByAuthor(int pageNo, int pageSize, String author) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("Id"));
    return gossipRepository.findByAuthor_Username(author, pageable);
  }


  // find gossips of the friends
  public Page<Gossip> getGossips(Integer pageNo, Integer pageSize, @NotNull User user) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("Id"));
    // 1. get Users, that you follow
    List<User> friends = userRepository.findByFollowers_Id(user.getId());
    // 2. get Gossips of those Users
    return gossipRepository.findByAuthorInOrderByDateTime(friends, pageable);
  }

  public Gossip createGossip(String text, User user) {
    OffsetDateTime dateTime = OffsetDateTime.now();
    gossipRepository.save(new Gossip()
        .setAuthor(user)
        .setDateTime(dateTime)
        .setText(text));
    List<Gossip> gossipList = gossipRepository.findByAuthor_Id(user.getId());
    int index = gossipList.size();
    return gossipList.get(index - 1);
  }
}
