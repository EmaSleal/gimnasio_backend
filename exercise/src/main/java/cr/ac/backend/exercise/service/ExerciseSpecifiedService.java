package cr.ac.backend.exercise.service;

import cr.ac.backend.exercise.model.ExerciseSpecified;

import java.util.List;
import java.util.Optional;

public interface ExerciseSpecifiedService {
    /*methods to read, create, edit and delete*/

    public Optional<List<ExerciseSpecified>> getAll();

    public Optional<ExerciseSpecified> getById(Long id);

    public Optional<ExerciseSpecified> save(ExerciseSpecified exerciseSpecified);

    public Boolean delete(Long id);

    public Optional<ExerciseSpecified> update(ExerciseSpecified exerciseSpecified);
}
