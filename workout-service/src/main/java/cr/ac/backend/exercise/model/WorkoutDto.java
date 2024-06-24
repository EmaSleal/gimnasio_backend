package cr.ac.backend.exercise.model;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Workout}
 */
@Value
@Builder
public class WorkoutDto implements Serializable {
    Long id;
    String name;
    MuscularGroupDto muscularGroup;
    ExerciseEnums.MuscularLoad muscularLoad;
}