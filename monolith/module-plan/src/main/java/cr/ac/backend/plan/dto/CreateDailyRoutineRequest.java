package cr.ac.backend.plan.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.util.Set;

public record CreateDailyRoutineRequest(
    @NotNull Long workoutPlanId,
    @NotNull DayOfWeek dayOfWeek,
    @NotEmpty Set<Long> workoutSpecificationIds
) {}
