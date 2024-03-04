package cr.ac.backend.exercise.service;

import cr.ac.backend.exercise.model.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseService {
    /*methods to read, create, edit and delete*/

    public Optional<List<Exercise>> getAll();

    public Optional<Exercise> getById(Long id);

    public Optional<Exercise> save(Exercise exercise);

    public Boolean delete(Long id);

    public Optional<Exercise> update(Exercise exercise);

}
