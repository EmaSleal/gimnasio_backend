package cr.ac.backend.plan.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record UpdateDailyRoutineRequest(
    @NotEmpty Set<Long> workoutSpecificationIds
) {}
