package cr.ac.backend.authentication.model;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record UserAuth(Long id, String userName, String email, User.Rol role, String password) implements Serializable {
}