package cr.ac.backend.plan.service;

import cr.ac.backend.plan.dto.CreateWorkoutPlanRequest;
import cr.ac.backend.plan.dto.UpdateWorkoutPlanRequest;
import cr.ac.backend.plan.dto.WorkoutPlanResponse;
import java.util.List;

public interface WorkoutPlanService {
    WorkoutPlanResponse create(CreateWorkoutPlanRequest request);
    WorkoutPlanResponse findById(Long id);
    List<WorkoutPlanResponse> findAll();
    List<WorkoutPlanResponse> findByUser(Long userId);
    List<WorkoutPlanResponse> findByTrainer(Long trainerId);
    List<WorkoutPlanResponse> findTemplates();
    WorkoutPlanResponse update(Long id, UpdateWorkoutPlanRequest request);
    void delete(Long id);
}
