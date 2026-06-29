package cr.ac.backend.workout.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateWorkoutSpecificationRequest(
        @NotNull Long workoutId,
        String description,
        Integer repsNumber,
        @Min(1) int setsNumber,
        @PositiveOrZero double recommendedWeight,
        @DecimalMin("0.0") @DecimalMax("5.0") double trainerRating,
        boolean isTimeBased,
        Integer timeSeconds
) {}
