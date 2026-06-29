package cr.ac.backend.workout.dto;

public record WorkoutSpecificationResponse(
        Long id,
        Long workoutId,
        String description,
        Integer repsNumber,
        int setsNumber,
        double recommendedWeight,
        double trainerRating,
        boolean isTimeBased,
        Integer timeSeconds
) {}
