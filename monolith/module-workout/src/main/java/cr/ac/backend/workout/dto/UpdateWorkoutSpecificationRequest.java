package cr.ac.backend.workout.dto;

public record UpdateWorkoutSpecificationRequest(
        String description,
        Integer repsNumber,
        Integer setsNumber,
        Double recommendedWeight,
        Double trainerRating,
        Boolean isTimeBased,
        Integer timeSeconds
) {}
