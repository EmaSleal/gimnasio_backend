package cr.ac.backend.workout.repository;

import cr.ac.backend.workout.domain.MuscularGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MuscularGroupRepository extends JpaRepository<MuscularGroup, Long> {
    Optional<MuscularGroup> findByName(String name);
    boolean existsByName(String name);
}
