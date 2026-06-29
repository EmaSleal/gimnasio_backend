package cr.ac.backend.workout.service.impl;

import cr.ac.backend.shared.exception.BusinessRuleViolationException;
import cr.ac.backend.shared.exception.ResourceNotFoundException;
import cr.ac.backend.workout.domain.Workout;
import cr.ac.backend.workout.dto.CreateWorkoutRequest;
import cr.ac.backend.workout.dto.UpdateWorkoutRequest;
import cr.ac.backend.workout.dto.WorkoutResponse;
import cr.ac.backend.workout.mapper.WorkoutMapper;
import cr.ac.backend.workout.repository.MuscularGroupRepository;
import cr.ac.backend.workout.repository.WorkoutRepository;
import cr.ac.backend.workout.repository.WorkoutSpecificationRepository;
import cr.ac.backend.workout.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final MuscularGroupRepository muscularGroupRepository;
    private final WorkoutSpecificationRepository workoutSpecificationRepository;
    private final WorkoutMapper workoutMapper;

    @Override
    public WorkoutResponse create(CreateWorkoutRequest request) {
        var muscularGroup = muscularGroupRepository.findById(request.muscularGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Muscular group not found with id: " + request.muscularGroupId()));
        var workout = Workout.builder()
                .name(request.name())
                .muscularGroup(muscularGroup)
                .muscularLoad(request.muscularLoad())
                .build();
        var saved = workoutRepository.save(workout);
        return workoutMapper.toResponse(saved);
    }

    @Override
    public WorkoutResponse findById(Long id) {
        var workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
        return workoutMapper.toResponse(workout);
    }

    @Override
    public List<WorkoutResponse> findAll() {
        return workoutMapper.toResponseList(workoutRepository.findAll());
    }

    @Override
    public List<WorkoutResponse> findByMuscularGroup(Long muscularGroupId) {
        return workoutMapper.toResponseList(workoutRepository.findByMuscularGroupId(muscularGroupId));
    }

    @Override
    public WorkoutResponse update(Long id, UpdateWorkoutRequest request) {
        var workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
        if (request.name() != null) {
            workout.setName(request.name());
        }
        if (request.muscularGroupId() != null) {
            var mg = muscularGroupRepository.findById(request.muscularGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Muscular group not found with id: " + request.muscularGroupId()));
            workout.setMuscularGroup(mg);
        }
        if (request.muscularLoad() != null) {
            workout.setMuscularLoad(request.muscularLoad());
        }
        var saved = workoutRepository.save(workout);
        return workoutMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
        if (workoutSpecificationRepository.existsByWorkoutId(id)) {
            throw new BusinessRuleViolationException("cannot delete workout with existing specifications");
        }
        workoutRepository.deleteById(id);
    }
}
