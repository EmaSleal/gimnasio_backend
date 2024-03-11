package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.ExerciseSpecified;
import cr.ac.backend.exercise.repo.ExerciseSpecifiedRepo;
import cr.ac.backend.exercise.service.ExerciseSpecifiedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseSpecifiedServiceImpl implements ExerciseSpecifiedService{
    private final ExerciseSpecifiedRepo exerciseSpecifiedRepo;

    @Override
    public Optional<List<ExerciseSpecified>> getAll() {
        var list = exerciseSpecifiedRepo.findAll();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<ExerciseSpecified> getById(Long id) {
        return exerciseSpecifiedRepo.findById(id);
    }

    @Override
    public Optional<ExerciseSpecified> save(ExerciseSpecified exerciseSpecified) {
        try {
            // Check if Exercise relationship is not null before saving
            if (exerciseSpecified.getExercise() != null) {
                return Optional.of(exerciseSpecifiedRepo.save(exerciseSpecified));
            } else {
                System.out.println("ExerciseSpecified creation failed: Exercise relationship is required.");
                return Optional.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            exerciseSpecifiedRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<ExerciseSpecified> update(ExerciseSpecified exerciseSpecified) {
        try {
            return Optional.of(exerciseSpecifiedRepo.save(exerciseSpecified));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
