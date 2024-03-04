package cr.ac.backend.exercise.repo;

import cr.ac.backend.exercise.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepo extends JpaRepository<Exercise, Long>{
}
