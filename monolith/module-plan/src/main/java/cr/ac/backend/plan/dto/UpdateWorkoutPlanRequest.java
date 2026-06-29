package cr.ac.backend.plan.dto;

import cr.ac.backend.plan.domain.PlanStatus;
import java.time.LocalDate;

public record UpdateWorkoutPlanRequest(
    String description,
    PlanStatus status,
    LocalDate startDate,
    LocalDate endDate
) {}
