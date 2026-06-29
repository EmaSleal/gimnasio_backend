package cr.ac.backend.workout.service.impl;

import cr.ac.backend.shared.exception.BusinessRuleViolationException;
import cr.ac.backend.shared.exception.ResourceNotFoundException;
import cr.ac.backend.workout.domain.WorkoutSpecification;
import cr.ac.backend.workout.dto.CreateWorkoutSpecificationRequest;
import cr.ac.backend.workout.dto.UpdateWorkoutSpecificationRequest;
import cr.ac.backend.workout.dto.WorkoutSpecificationResponse;
import cr.ac.backend.workout.mapper.WorkoutSpecificationMapper;
import cr.ac.backend.workout.repository.WorkoutRepository;
import cr.ac.backend.workout.repository.WorkoutSpecificationRepository;
import cr.ac.backend.workout.service.WorkoutSpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutSpecificationServiceImpl implements WorkoutSpecificationService {

    private final WorkoutSpecificationRepository workoutSpecificationRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutSpecificationMapper workoutSpecificationMapper;

    @Override
    public WorkoutSpecificationResponse create(CreateWorkoutSpecificationRequest request) {
        var workout = workoutRepository.findById(request.workoutId())
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + request.workoutId()));
        validateTimeBased(request.isTimeBased(), request.repsNumber(), request.timeSeconds());
        var spec = WorkoutSpecification.builder()
                .workout(workout)
                .description(request.description())
                .repsNumber(request.repsNumber())
                .setsNumber(request.setsNumber())
                .recommendedWeight(request.recommendedWeight())
                .trainerRating(request.trainerRating())
                .isTimeBased(request.isTimeBased())
                .timeSeconds(request.timeSeconds())
                .build();
        var saved = workoutSpecificationRepository.save(spec);
        return workoutSpecificationMapper.toResponse(saved);
    }

    @Override
    public WorkoutSpecificationResponse findById(Long id) {
        var spec = workoutSpecificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout specification not found with id: " + id));
        return workoutSpecificationMapper.toResponse(spec);
    }

    @Override
    public List<WorkoutSpecificationResponse> findAll() {
        return workoutSpecificationMapper.toResponseList(workoutSpecificationRepository.findAll());
    }

    @Override
    public List<WorkoutSpecificationResponse> findByWorkout(Long workoutId) {
        return workoutSpecificationMapper.toResponseList(workoutSpecificationRepository.findByWorkoutId(workoutId));
    }

    @Override
    public WorkoutSpecificationResponse update(Long id, UpdateWorkoutSpecificationRequest request) {
        var spec = workoutSpecificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout specification not found with id: " + id));
        workoutSpecificationMapper.updateEntity(spec, request);
        boolean effectiveTimeBased = request.isTimeBased() != null ? request.isTimeBased() : spec.isTimeBased();
        Integer effectiveReps = request.repsNumber() != null ? request.repsNumber() : spec.getRepsNumber();
        Integer effectiveTime = request.timeSeconds() != null ? request.timeSeconds() : spec.getTimeSeconds();
        validateTimeBased(effectiveTimeBased, effectiveReps, effectiveTime);
        var saved = workoutSpecificationRepository.save(spec);
        return workoutSpecificationMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        workoutSpecificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout specification not found with id: " + id));
        workoutSpecificationRepository.deleteById(id);
    }

    private void validateTimeBased(boolean isTimeBased, Integer repsNumber, Integer timeSeconds) {
        if (isTimeBased) {
            if (timeSeconds == null || timeSeconds <= 0) {
                throw new BusinessRuleViolationException(
                        "timeSeconds required for time-based exercises", "timeSeconds");
            }
        } else {
            if (repsNumber == null || repsNumber <= 0) {
                throw new BusinessRuleViolationException(
                        "repsNumber required for rep-based exercises", "repsNumber");
            }
        }
    }
}
