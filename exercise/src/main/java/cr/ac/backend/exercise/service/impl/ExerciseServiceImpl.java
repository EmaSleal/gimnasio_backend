package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.Exercise;
import cr.ac.backend.exercise.repo.ExerciseRepo;
import cr.ac.backend.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepo exerciseRepo;


    @Override
    public Optional<List<Exercise>> getAll() {
        var list = exerciseRepo.findAll();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<Exercise> getById(Long id) {
        return exerciseRepo.findById(id);
    }

    @Override
    public Optional<Exercise> save(Exercise exercise) {
        try {
            return Optional.of(exerciseRepo.save(exercise));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public Boolean delete(Long id) {
        try {
            exerciseRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Exercise> update(Exercise exercise) {
        try {
            return Optional.of(exerciseRepo.save(exercise));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
