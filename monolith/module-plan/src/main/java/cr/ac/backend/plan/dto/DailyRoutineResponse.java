package cr.ac.backend.plan.dto;

import cr.ac.backend.workout.dto.WorkoutSpecificationResponse;
import java.time.DayOfWeek;
import java.util.List;

public record DailyRoutineResponse(
    Long id,
    Long workoutPlanId,
    DayOfWeek dayOfWeek,
    List<WorkoutSpecificationResponse> workoutSpecifications
) {}
