package cr.ac.backend.userservice.model;

import java.io.Serializable;

public record UserAuth(Long id, String userName, String email, User.Rol role, String password) implements Serializable {
}