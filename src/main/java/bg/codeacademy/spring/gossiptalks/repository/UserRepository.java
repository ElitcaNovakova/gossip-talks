package bg.codeacademy.spring.gossiptalks.repository;

import bg.codeacademy.spring.gossiptalks.model.User;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUsername(String username);

  Set<User> findByUsernameOrNameContainingAllIgnoreCase(String username, String name);

  List<User> findByFollowers_Id(Long id);

}
