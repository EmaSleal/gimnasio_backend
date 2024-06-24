package cr.ac.backend.exercise.repo;

import cr.ac.backend.exercise.model.WorkoutSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSpecificationRepo extends JpaRepository<WorkoutSpecification, Long> {
}
