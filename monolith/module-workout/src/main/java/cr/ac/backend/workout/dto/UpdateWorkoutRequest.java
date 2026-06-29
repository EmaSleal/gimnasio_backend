package cr.ac.backend.workout.dto;

import cr.ac.backend.workout.domain.MuscularLoad;

public record UpdateWorkoutRequest(
        String name,
        Long muscularGroupId,
        MuscularLoad muscularLoad
) {}
