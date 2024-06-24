package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.DailyRoutine;
import cr.ac.backend.exercise.model.ExerciseEnums;
import cr.ac.backend.exercise.repo.DailyRoutineRepo;
import cr.ac.backend.exercise.service.DailyRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyRoutineServiceImpl implements DailyRoutineService {
    private final DailyRoutineRepo dailyRoutineRepo;

    @Override
    public Optional<List<DailyRoutine>> getAll() {
        var list = dailyRoutineRepo.findAll();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<DailyRoutine> getById(Long id) {
        return dailyRoutineRepo.findById(id);
    }

    @Override
    public Optional<DailyRoutine> save(DailyRoutine dailyRoutine) {
        try {
            return Optional.of(dailyRoutineRepo.save(dailyRoutine));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            dailyRoutineRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<DailyRoutine> update(DailyRoutine dailyRoutine) {
        try {
            return Optional.of(dailyRoutineRepo.save(dailyRoutine));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public DailyRoutine findByDay(ExerciseEnums.DayOfWeek dayOfWeek) {
        return dailyRoutineRepo.findByDays(dayOfWeek.toString()).orElse(null).get(0);
    }
}
