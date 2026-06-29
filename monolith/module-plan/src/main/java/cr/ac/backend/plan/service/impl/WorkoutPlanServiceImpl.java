package cr.ac.backend.plan.service.impl;

import cr.ac.backend.plan.domain.PlanStatus;
import cr.ac.backend.plan.domain.WorkoutPlan;
import cr.ac.backend.plan.dto.CreateWorkoutPlanRequest;
import cr.ac.backend.plan.dto.UpdateWorkoutPlanRequest;
import cr.ac.backend.plan.dto.WorkoutPlanResponse;
import cr.ac.backend.plan.event.WorkoutPlanAssignedEvent;
import cr.ac.backend.plan.event.WorkoutPlanCompletedEvent;
import cr.ac.backend.plan.mapper.WorkoutPlanMapper;
import cr.ac.backend.plan.repository.DailyRoutineRepository;
import cr.ac.backend.plan.repository.WorkoutPlanRepository;
import cr.ac.backend.plan.service.WorkoutPlanService;
import cr.ac.backend.shared.exception.BusinessRuleViolationException;
import cr.ac.backend.shared.exception.ResourceNotFoundException;
import cr.ac.backend.shared.security.UserRole;
import cr.ac.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final DailyRoutineRepository dailyRoutineRepository;
    private final UserService userService;
    private final WorkoutPlanMapper workoutPlanMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public WorkoutPlanResponse create(CreateWorkoutPlanRequest request) {
        userService.findById(request.userId());
        var trainer = userService.findById(request.trainerId());
        if (trainer.role() == UserRole.CLIENT) {
            throw new BusinessRuleViolationException("trainer must have TRAINER or ADMIN role");
        }
        if (!request.startDate().isBefore(request.endDate())) {
            throw new BusinessRuleViolationException("startDate must be before endDate");
        }
        if (!request.isTemplate()) {
            if (workoutPlanRepository.findByUserIdAndStatus(request.userId(), PlanStatus.ACTIVE).isPresent()) {
                throw new BusinessRuleViolationException("user already has an active plan");
            }
        }
        WorkoutPlan plan = WorkoutPlan.builder()
            .userId(request.userId())
            .trainerId(request.trainerId())
            .description(request.description())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .isTemplate(request.isTemplate())
            .build();
        WorkoutPlan savedPlan = workoutPlanRepository.save(plan);
        if (!savedPlan.isTemplate()) {
            eventPublisher.publishEvent(new WorkoutPlanAssignedEvent(savedPlan.getId(), savedPlan.getUserId(), savedPlan.getTrainerId()));
        }
        return workoutPlanMapper.toResponse(savedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutPlanResponse findById(Long id) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + id));
        return workoutPlanMapper.toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutPlanResponse> findAll() {
        return workoutPlanMapper.toResponseList(workoutPlanRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutPlanResponse> findByUser(Long userId) {
        return workoutPlanMapper.toResponseList(workoutPlanRepository.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutPlanResponse> findByTrainer(Long trainerId) {
        return workoutPlanMapper.toResponseList(workoutPlanRepository.findByTrainerId(trainerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutPlanResponse> findTemplates() {
        return workoutPlanMapper.toResponseList(workoutPlanRepository.findByIsTemplate(true));
    }

    @Override
    public WorkoutPlanResponse update(Long id, UpdateWorkoutPlanRequest request) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + id));
        if (plan.getStatus() == PlanStatus.COMPLETED || plan.getStatus() == PlanStatus.CANCELLED) {
            throw new BusinessRuleViolationException("cannot modify a completed or cancelled plan");
        }
        if (request.status() != null) {
            validateStatusTransition(plan.getStatus(), request.status());
        }
        LocalDate effectiveStart = request.startDate() != null ? request.startDate() : plan.getStartDate();
        LocalDate effectiveEnd = request.endDate() != null ? request.endDate() : plan.getEndDate();
        if (!effectiveStart.isBefore(effectiveEnd)) {
            throw new BusinessRuleViolationException("startDate must be before endDate");
        }
        if (request.description() != null) {
            plan.setDescription(request.description());
        }
        if (request.status() != null) {
            plan.setStatus(request.status());
        }
        if (request.startDate() != null) {
            plan.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            plan.setEndDate(request.endDate());
        }
        WorkoutPlan saved = workoutPlanRepository.save(plan);
        if (request.status() == PlanStatus.COMPLETED && !saved.isTemplate()) {
            eventPublisher.publishEvent(new WorkoutPlanCompletedEvent(saved.getId(), saved.getUserId()));
        }
        return workoutPlanMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + id));
        if (!plan.isTemplate() && dailyRoutineRepository.existsByWorkoutPlan_Id(id)) {
            throw new BusinessRuleViolationException("cannot delete an assigned plan with routines — cancel it instead");
        }
        workoutPlanRepository.deleteById(id);
    }

    private void validateStatusTransition(PlanStatus current, PlanStatus next) {
        boolean valid = switch (current) {
            case ACTIVE -> next == PlanStatus.COMPLETED || next == PlanStatus.CANCELLED || next == PlanStatus.SUSPENDED;
            case SUSPENDED -> next == PlanStatus.ACTIVE;
            default -> false;
        };
        if (!valid) {
            throw new BusinessRuleViolationException("invalid status transition: " + current + " -> " + next);
        }
    }
}
