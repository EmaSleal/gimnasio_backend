package cr.ac.backend.workout.service;

import cr.ac.backend.workout.dto.CreateWorkoutSpecificationRequest;
import cr.ac.backend.workout.dto.UpdateWorkoutSpecificationRequest;
import cr.ac.backend.workout.dto.WorkoutSpecificationResponse;

import java.util.List;

public interface WorkoutSpecificationService {
    WorkoutSpecificationResponse create(CreateWorkoutSpecificationRequest request);
    WorkoutSpecificationResponse findById(Long id);
    List<WorkoutSpecificationResponse> findAll();
    List<WorkoutSpecificationResponse> findByWorkout(Long workoutId);
    WorkoutSpecificationResponse update(Long id, UpdateWorkoutSpecificationRequest request);
    void delete(Long id);
}
