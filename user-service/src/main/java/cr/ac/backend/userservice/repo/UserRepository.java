package cr.ac.backend.userservice.repo;


import cr.ac.backend.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String username);

    Optional<List<User>> findByCreatedBy(Long id);
}
