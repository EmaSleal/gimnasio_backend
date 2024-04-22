package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.Workout;
import cr.ac.backend.exercise.repo.WorkoutRepo;
import cr.ac.backend.exercise.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepo workoutRepo;


    @Override
    public Optional<List<Workout>> getAll() {
        var list = workoutRepo.findAll();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<Workout> getById(Long id) {
        return workoutRepo.findById(id);
    }

    @Override
    public Optional<Workout> save(Workout workout) {
        try {
            return Optional.of(workoutRepo.save(workout));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public Boolean delete(Long id) {
        try {
            workoutRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Workout> update(Workout workout) {
        try {
            return Optional.of(workoutRepo.save(workout));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
