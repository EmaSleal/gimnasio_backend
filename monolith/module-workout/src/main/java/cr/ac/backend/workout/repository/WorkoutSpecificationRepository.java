package cr.ac.backend.workout.repository;

import cr.ac.backend.workout.domain.WorkoutSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSpecificationRepository extends JpaRepository<WorkoutSpecification, Long> {
    List<WorkoutSpecification> findByWorkoutId(Long workoutId);
    boolean existsByWorkoutId(Long workoutId);
}
