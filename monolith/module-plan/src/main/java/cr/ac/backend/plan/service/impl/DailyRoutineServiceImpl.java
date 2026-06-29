package cr.ac.backend.plan.service.impl;

import cr.ac.backend.plan.domain.DailyRoutine;
import cr.ac.backend.plan.domain.PlanStatus;
import cr.ac.backend.plan.domain.WorkoutPlan;
import cr.ac.backend.plan.dto.CreateDailyRoutineRequest;
import cr.ac.backend.plan.dto.DailyRoutineResponse;
import cr.ac.backend.plan.dto.UpdateDailyRoutineRequest;
import cr.ac.backend.plan.mapper.DailyRoutineMapper;
import cr.ac.backend.plan.repository.DailyRoutineRepository;
import cr.ac.backend.plan.repository.WorkoutPlanRepository;
import cr.ac.backend.plan.service.DailyRoutineService;
import cr.ac.backend.shared.exception.BusinessRuleViolationException;
import cr.ac.backend.shared.exception.DuplicateResourceException;
import cr.ac.backend.shared.exception.ResourceNotFoundException;
import cr.ac.backend.workout.domain.WorkoutSpecification;
import cr.ac.backend.workout.repository.WorkoutSpecificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyRoutineServiceImpl implements DailyRoutineService {

    private final DailyRoutineRepository dailyRoutineRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutSpecificationRepository workoutSpecificationRepository;
    private final DailyRoutineMapper dailyRoutineMapper;

    @Override
    public DailyRoutineResponse create(CreateDailyRoutineRequest request) {
        WorkoutPlan plan = workoutPlanRepository.findById(request.workoutPlanId())
            .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + request.workoutPlanId()));
        if (plan.getStatus() == PlanStatus.COMPLETED || plan.getStatus() == PlanStatus.CANCELLED) {
            throw new BusinessRuleViolationException("cannot add routines to a completed or cancelled plan");
        }
        if (dailyRoutineRepository.existsByWorkoutPlan_IdAndDayOfWeek(request.workoutPlanId(), request.dayOfWeek())) {
            throw new DuplicateResourceException("plan already has a routine for " + request.dayOfWeek(), "dayOfWeek");
        }
        Set<WorkoutSpecification> specs = new HashSet<>();
        for (Long specId : request.workoutSpecificationIds()) {
            WorkoutSpecification spec = workoutSpecificationRepository.findById(specId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSpecification not found with id: " + specId));
            specs.add(spec);
        }
        DailyRoutine routine = DailyRoutine.builder()
            .workoutPlan(plan)
            .dayOfWeek(request.dayOfWeek())
            .workoutSpecifications(specs)
            .build();
        DailyRoutine saved = dailyRoutineRepository.save(routine);
        return dailyRoutineMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DailyRoutineResponse findById(Long id) {
        DailyRoutine routine = dailyRoutineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("DailyRoutine not found with id: " + id));
        return dailyRoutineMapper.toResponse(routine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyRoutineResponse> findByWorkoutPlan(Long workoutPlanId) {
        return dailyRoutineMapper.toResponseList(dailyRoutineRepository.findByWorkoutPlan_Id(workoutPlanId));
    }

    @Override
    public DailyRoutineResponse update(Long id, UpdateDailyRoutineRequest request) {
        DailyRoutine routine = dailyRoutineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("DailyRoutine not found with id: " + id));
        WorkoutPlan plan = routine.getWorkoutPlan();
        if (plan.getStatus() == PlanStatus.COMPLETED || plan.getStatus() == PlanStatus.CANCELLED) {
            throw new BusinessRuleViolationException("cannot modify a routine from a completed or cancelled plan");
        }
        Set<WorkoutSpecification> newSpecs = new HashSet<>();
        for (Long specId : request.workoutSpecificationIds()) {
            WorkoutSpecification spec = workoutSpecificationRepository.findById(specId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutSpecification not found with id: " + specId));
            newSpecs.add(spec);
        }
        routine.setWorkoutSpecifications(newSpecs);
        DailyRoutine saved = dailyRoutineRepository.save(routine);
        return dailyRoutineMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        DailyRoutine routine = dailyRoutineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("DailyRoutine not found with id: " + id));
        WorkoutPlan plan = routine.getWorkoutPlan();
        if (plan.getStatus() == PlanStatus.COMPLETED || plan.getStatus() == PlanStatus.CANCELLED) {
            throw new BusinessRuleViolationException("cannot delete a routine from a completed or cancelled plan");
        }
        dailyRoutineRepository.deleteById(id);
    }
}
