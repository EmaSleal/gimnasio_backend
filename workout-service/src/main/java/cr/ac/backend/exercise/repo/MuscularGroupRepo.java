package cr.ac.backend.exercise.repo;

import cr.ac.backend.exercise.model.MuscularGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuscularGroupRepo extends JpaRepository<MuscularGroup, Long> {
}
