package cr.ac.backend.exercise.repo;

import cr.ac.backend.exercise.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutineRepo extends JpaRepository<Routine, Long> {
}
