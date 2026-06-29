package cr.ac.backend.plan.repository;

import cr.ac.backend.plan.domain.PlanStatus;
import cr.ac.backend.plan.domain.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserId(Long userId);
    List<WorkoutPlan> findByTrainerId(Long trainerId);
    List<WorkoutPlan> findByIsTemplate(boolean isTemplate);
    Optional<WorkoutPlan> findByUserIdAndStatus(Long userId, PlanStatus status);
}
