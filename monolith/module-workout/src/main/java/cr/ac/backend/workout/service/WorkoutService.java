package cr.ac.backend.workout.service;

import cr.ac.backend.workout.dto.CreateWorkoutRequest;
import cr.ac.backend.workout.dto.UpdateWorkoutRequest;
import cr.ac.backend.workout.dto.WorkoutResponse;

import java.util.List;

public interface WorkoutService {
    WorkoutResponse create(CreateWorkoutRequest request);
    WorkoutResponse findById(Long id);
    List<WorkoutResponse> findAll();
    List<WorkoutResponse> findByMuscularGroup(Long muscularGroupId);
    WorkoutResponse update(Long id, UpdateWorkoutRequest request);
    void delete(Long id);
}
