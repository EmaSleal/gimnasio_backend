package cr.ac.backend.exercise.model;

import lombok.Builder;

import java.io.Serializable;


@Builder
public record UserDto(Long id, String userName, String email, ExerciseEnums.Rol role, boolean enabled, boolean accountNonExpired,
                      boolean credentialsNonExpired, boolean accountNonLocked) implements Serializable {
}