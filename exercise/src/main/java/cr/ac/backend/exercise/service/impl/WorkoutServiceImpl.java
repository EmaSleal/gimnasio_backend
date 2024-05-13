package cr.ac.backend.exercise.service.impl;

import cr.ac.backend.exercise.model.MuscularGroupDto;
import cr.ac.backend.exercise.model.Workout;
import cr.ac.backend.exercise.model.WorkoutDto;
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
    public Optional<List<WorkoutDto>> getAll() {
        var list = workoutRepo.findAll();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.stream().map(workout -> WorkoutDto.builder()
                .id(workout.getId())
                .name(workout.getName())
                .muscularGroup(MuscularGroupDto.builder().name(workout.getMuscularGroup().getName()).id(workout.getMuscularGroup().getId()).build())
                .muscularLoad(workout.getMuscularLoad())
                .build()).toList());
    }

    @Override
    public Optional<WorkoutDto> getById(Long id) {
        var workout = workoutRepo.findById(id);
        if (workout.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(WorkoutDto.builder()
                .id(workout.get().getId())
                .name(workout.get().getName())
                .muscularGroup(MuscularGroupDto.builder().name(workout.get().getMuscularGroup().getName()).id(workout.get().getMuscularGroup().getId()).build())
                .muscularLoad(workout.get().getMuscularLoad())
                .build());
    }

    @Override
    public Optional<Workout> save(Workout workout) {
        try {
            var saved = workoutRepo.save(workout);
            return Optional.of(saved);
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
    public Optional<WorkoutDto> update(Workout workout) {
        try {
            var updated = workoutRepo.save(workout);
            return Optional.of(WorkoutDto.builder()
                    .id(updated.getId())
                    .name(updated.getName())
                    .muscularGroup(MuscularGroupDto.builder().name(updated.getMuscularGroup().getName()).id(updated.getMuscularGroup().getId()).build())
                    .muscularLoad(updated.getMuscularLoad())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
