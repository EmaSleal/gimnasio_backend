package cr.ac.backend.plan.service;

import cr.ac.backend.plan.dto.CreateDailyRoutineRequest;
import cr.ac.backend.plan.dto.DailyRoutineResponse;
import cr.ac.backend.plan.dto.UpdateDailyRoutineRequest;
import java.util.List;

public interface DailyRoutineService {
    DailyRoutineResponse create(CreateDailyRoutineRequest request);
    DailyRoutineResponse findById(Long id);
    List<DailyRoutineResponse> findByWorkoutPlan(Long workoutPlanId);
    DailyRoutineResponse update(Long id, UpdateDailyRoutineRequest request);
    void delete(Long id);
}
