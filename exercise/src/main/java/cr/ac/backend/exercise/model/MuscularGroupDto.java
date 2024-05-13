package cr.ac.backend.exercise.model;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link MuscularGroup}
 */
@Value
@Builder
public class MuscularGroupDto implements Serializable {
    Long id;
    String name;
}