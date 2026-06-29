package cr.ac.backend.workout.repository;

import cr.ac.backend.workout.domain.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByMuscularGroupId(Long muscularGroupId);
    boolean existsByMuscularGroupId(Long muscularGroupId);
}
