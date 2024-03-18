package cr.ac.backend.exercise.service;

import cr.ac.backend.exercise.model.ExerciseEnums;
import cr.ac.backend.exercise.model.RoutineDay;

import java.util.List;
import java.util.Optional;

public interface RoutineDayService {
    /*methods to read, create, edit and delete*/

    public Optional<List<RoutineDay>> getAll();

    public Optional<RoutineDay> getById(Long id);

    public Optional<RoutineDay> save(RoutineDay rutineDay);

    public Boolean delete(Long id);

    public Optional<RoutineDay> update(RoutineDay rutineDay);

    public RoutineDay findByDay(ExerciseEnums.DayOfWeek dayOfWeek);
}
