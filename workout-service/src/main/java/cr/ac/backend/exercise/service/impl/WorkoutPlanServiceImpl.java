package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.DailyRoutine;
import cr.ac.backend.exercise.model.WorkoutPlan;
import cr.ac.backend.exercise.model.WorkoutSpecification;
import cr.ac.backend.exercise.repo.WorkoutPlanRepo;
import cr.ac.backend.exercise.service.DailyRoutineService;
import cr.ac.backend.exercise.service.WorkoutPlanService;
import cr.ac.backend.exercise.service.WorkoutSpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutPlanServiceImpl implements WorkoutPlanService {
    private final WorkoutPlanRepo workoutPlanRepo;
    private final DailyRoutineService dailyRoutineService;
    private final WorkoutSpecificationService workoutSpecificationService;

    @Override
    public Optional<List<WorkoutPlan>> getAll() {
        return Optional.of(workoutPlanRepo.findAll());
    }

    @Override
    public Optional<WorkoutPlan> getById(Long id) {
        return workoutPlanRepo.findById(id);
    }

    @Override
    public Optional<List<WorkoutPlan>> getByIdUser(Long id) {
        var list = workoutPlanRepo.findByIdUser(id);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    @Override
    public Optional<WorkoutPlan> save(WorkoutPlan workoutPlan) {

        return Optional.of(workoutPlanRepo.save(workoutPlan));
    }

    @Override
    public Boolean delete(Long id) {
        workoutPlanRepo.deleteById(id);
        return true;
    }

    @Override
    public Optional<WorkoutPlan> update(WorkoutPlan workoutPlan) {
        //get the current date of the system with format "yyyy-MM-dd:HH:mm:ss"
        var currentDate = new java.sql.Timestamp(System.currentTimeMillis());
        workoutPlan.setUpdatedAt(currentDate.toString());
        return Optional.of(workoutPlanRepo.save(workoutPlan));
    }

    @Override
    public Optional<List<WorkoutPlan>> getTempletes() {
        var list = workoutPlanRepo.findByIsTemplate(true);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }
}
