package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.WorkoutSpecification;
import cr.ac.backend.exercise.repo.WorkoutSpecificationRepo;
import cr.ac.backend.exercise.service.WorkoutSpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutSpecificationServiceImpl implements WorkoutSpecificationService {
    private final WorkoutSpecificationRepo workoutSpecificationRepo;

    @Override
    public Optional<List<WorkoutSpecification>> getAll() {
        var list = workoutSpecificationRepo.findAll();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<WorkoutSpecification> getById(Long id) {
        return workoutSpecificationRepo.findById(id);
    }

    @Override
    public Optional<WorkoutSpecification> save(WorkoutSpecification workoutSpecification) {
        try {
            // Check if Workout relationship is not null before saving
            if (workoutSpecification.getWorkout() != null) {
                return Optional.of(workoutSpecificationRepo.save(workoutSpecification));
            } else {
                System.out.println("WorkoutSpecification creation failed: Workout relationship is required.");
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
            workoutSpecificationRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<WorkoutSpecification> update(WorkoutSpecification workoutSpecification) {
        try {

            return Optional.of(workoutSpecificationRepo.save(workoutSpecification));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
