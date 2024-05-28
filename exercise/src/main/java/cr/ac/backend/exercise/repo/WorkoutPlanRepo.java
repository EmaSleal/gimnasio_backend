package cr.ac.backend.exercise.repo;

import cr.ac.backend.exercise.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutPlanRepo extends JpaRepository<WorkoutPlan, Long> {

    public List<WorkoutPlan> findByIdTrainer(Long id);

    public List<WorkoutPlan> findByIdUser(Long id);

    public List<WorkoutPlan> findByIsTemplate(Boolean isTemplate);
}
