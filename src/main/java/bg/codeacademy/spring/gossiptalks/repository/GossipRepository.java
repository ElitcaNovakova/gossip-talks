package bg.codeacademy.spring.gossiptalks.repository;

import bg.codeacademy.spring.gossiptalks.model.Gossip;
import bg.codeacademy.spring.gossiptalks.model.User;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GossipRepository extends JpaRepository<Gossip, Long> {

  List<Gossip> findByAuthor_Id(Long Id);

  Page<Gossip> findByAuthor_Username(String author, Pageable pageable);

  Page<Gossip> findByAuthorIn(Collection<User> friends, Pageable pageable);

}