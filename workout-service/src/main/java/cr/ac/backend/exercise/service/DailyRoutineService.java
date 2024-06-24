package cr.ac.backend.exercise.service;

import cr.ac.backend.exercise.model.DailyRoutine;
import cr.ac.backend.exercise.model.ExerciseEnums;

import java.util.List;
import java.util.Optional;

public interface DailyRoutineService {
    /*methods to read, create, edit and delete*/

    public Optional<List<DailyRoutine>> getAll();

    public Optional<DailyRoutine> getById(Long id);

    public Optional<DailyRoutine> save(DailyRoutine dailyRoutine);

    public Boolean delete(Long id);

    public Optional<DailyRoutine> update(DailyRoutine dailyRoutine);

    public DailyRoutine findByDay(ExerciseEnums.DayOfWeek dayOfWeek);
}
