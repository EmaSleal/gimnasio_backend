package cr.ac.backend.workout.dto;

import cr.ac.backend.workout.domain.MuscularLoad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWorkoutRequest(
        @NotBlank @Size(max = 200) String name,
        @NotNull Long muscularGroupId,
        @NotNull MuscularLoad muscularLoad
) {}
