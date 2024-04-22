package cr.ac.backend.exercise.service;

import cr.ac.backend.exercise.model.WorkoutSpecification;

import java.util.List;
import java.util.Optional;

public interface WorkoutSpecificationService {
    /*methods to read, create, edit and delete*/

    public Optional<List<WorkoutSpecification>> getAll();

    public Optional<WorkoutSpecification> getById(Long id);

    public Optional<WorkoutSpecification> save(WorkoutSpecification workoutSpecification);

    public Boolean delete(Long id);

    public Optional<WorkoutSpecification> update(WorkoutSpecification workoutSpecification);
}
