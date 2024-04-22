package cr.ac.backend.exercise.repo;

import cr.ac.backend.exercise.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepo extends JpaRepository<Workout, Long>{
}
