package cr.ac.backend.plan.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateWorkoutPlanRequest(
    @NotNull Long userId,
    @NotNull Long trainerId,
    String description,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    boolean isTemplate
) {}
