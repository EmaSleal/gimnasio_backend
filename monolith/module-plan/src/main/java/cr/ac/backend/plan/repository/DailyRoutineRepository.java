package cr.ac.backend.plan.repository;

import cr.ac.backend.plan.domain.DailyRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.DayOfWeek;
import java.util.List;

public interface DailyRoutineRepository extends JpaRepository<DailyRoutine, Long> {
    List<DailyRoutine> findByWorkoutPlan_Id(Long workoutPlanId);
    boolean existsByWorkoutPlan_IdAndDayOfWeek(Long workoutPlanId, DayOfWeek dayOfWeek);
    boolean existsByWorkoutPlan_Id(Long workoutPlanId);
}
