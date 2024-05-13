package cr.ac.backend.exercise.service;

import cr.ac.backend.exercise.model.Workout;
import cr.ac.backend.exercise.model.WorkoutDto;

import java.util.List;
import java.util.Optional;

public interface WorkoutService {
    /*methods to read, create, edit and delete*/

    public Optional<List<WorkoutDto>> getAll();

    public Optional<WorkoutDto> getById(Long id);

    public Optional<Workout> save(Workout workout);

    public Boolean delete(Long id);

    public Optional<WorkoutDto> update(Workout workout);

}
