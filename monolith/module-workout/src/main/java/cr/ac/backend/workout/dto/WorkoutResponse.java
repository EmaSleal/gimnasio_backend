package cr.ac.backend.workout.dto;

import cr.ac.backend.workout.domain.MuscularLoad;

public record WorkoutResponse(
        Long id,
        String name,
        MuscularGroupResponse muscularGroup,
        MuscularLoad muscularLoad
) {}
