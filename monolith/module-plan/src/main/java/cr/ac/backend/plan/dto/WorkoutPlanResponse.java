package cr.ac.backend.plan.dto;

import cr.ac.backend.plan.domain.PlanStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkoutPlanResponse(
    Long id,
    Long userId,
    Long trainerId,
    String description,
    PlanStatus status,
    LocalDate startDate,
    LocalDate endDate,
    boolean isTemplate,
    LocalDateTime createdAt
) {}
