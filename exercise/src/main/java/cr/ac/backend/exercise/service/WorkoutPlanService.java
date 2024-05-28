package cr.ac.backend.exercise.service;



import cr.ac.backend.exercise.model.WorkoutPlan;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanService {
    /*methods to read, create, edit and delete*/

    public Optional<List<WorkoutPlan>> getAll();

    public Optional<WorkoutPlan> getById(Long id);

    public Optional<WorkoutPlan> save(WorkoutPlan workoutPlan);

    public Boolean delete(Long id);

    public Optional<WorkoutPlan> update(WorkoutPlan workoutPlan);

    public Optional<List<WorkoutPlan>> getTempletes();
}
