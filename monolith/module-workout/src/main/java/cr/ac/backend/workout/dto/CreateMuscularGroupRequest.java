package cr.ac.backend.workout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMuscularGroupRequest(
        @NotBlank @Size(max = 100) String name
) {}
